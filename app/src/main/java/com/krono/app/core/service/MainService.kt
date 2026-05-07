package com.krono.app.core.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.view.WindowManager
import com.krono.app.ACTION_HIDE_OVERLAY
import com.krono.app.ACTION_PAUSE
import com.krono.app.ACTION_PLAY
import com.krono.app.ACTION_RESET
import com.krono.app.ACTION_SHOW_OVERLAY
import com.krono.app.ACTION_START_FOCUS
import com.krono.app.ACTION_STOP_SERVICE
import com.krono.app.KronoApp
import com.krono.app.NOTIFICATION_ID
import com.krono.app.core.data.OverlayConfig
import com.krono.app.core.data.OverlayDataStore
import com.krono.app.core.data.TimerPreferences
import com.krono.app.core.util.KronoNavigator
import com.krono.app.core.util.PermissionUtils
import com.krono.app.core.tool.ToolRegistry
import com.krono.app.core.tool.ToolViewModel
import com.krono.app.core.tool.KronoTool
import com.krono.app.feature.countdown.CountdownViewModel
import com.krono.app.feature.countdown.CountdownManager
import com.krono.app.feature.stopwatch.StopwatchViewModel
import com.krono.app.feature.stopwatch.StopwatchTool
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.distinctUntilChanged
import androidx.lifecycle.*
import androidx.savedstate.*
import android.provider.Settings

const val ACTION_FOCUS_DISMISSED = "com.krono.app.ACTION_FOCUS_DISMISSED"

class MainService : Service(), LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    private lateinit var dataStore: OverlayDataStore
    private lateinit var timerPrefs: TimerPreferences
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var overlayManager: OverlayManager
    private lateinit var feedbackManager: FeedbackManager
    private lateinit var wakeLockManager: WakeLockManager
    private lateinit var countdownManager: CountdownManager
    
    private var activeTool: KronoTool? = null
    private val activeViewModel: ToolViewModel? get() = activeTool?.viewModel
    private var notificationJob: Job? = null
    private var currentConfig: OverlayConfig = OverlayConfig()
    private var observersStarted = false
    private var pendingDonation = false

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        dataStore        = OverlayDataStore(this)
        timerPrefs       = TimerPreferences(this)
        
        notificationHelper = NotificationHelper(this)
        feedbackManager    = FeedbackManager(this)
        wakeLockManager    = WakeLockManager(this)
        
        val app = application as KronoApp

        if (activeTool == null) {
            activeTool = ToolRegistry.getTool("stopwatch")
                ?: StopwatchTool(dataStore, app.stopwatchViewModel)
        }

        overlayManager = OverlayManager(this, windowManager, dataStore, { activeViewModel?.toolState }, serviceScope, this, this, this)

        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)

        countdownManager = CountdownManager(
            context = this,
            windowManager = windowManager,
            feedbackManager = feedbackManager,
            notificationHelper = notificationHelper,
            countdownViewModel = app.countdownViewModel
        )

        startForegroundWithNotification()
        timerPrefs.setServiceActive(true)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        ensureObserversStarted()

        val id = intent?.getStringExtra(CountdownViewModel.EXTRA_COUNTDOWN_ID)

        when (intent?.action) {
            ACTION_PLAY -> {
                if (!checkPermissions()) return START_STICKY
                activeViewModel?.start()
                feedbackManager.triggerFeedback(currentConfig)
            }
            ACTION_PAUSE -> {
                handlePause()
                feedbackManager.triggerFeedback(currentConfig)
            }
            ACTION_RESET -> handleReset()
            ACTION_STOP_SERVICE -> closeAndStop()
            ACTION_SHOW_OVERLAY -> showOverlay()
            ACTION_HIDE_OVERLAY -> hideOverlay()
            ACTION_START_FOCUS -> if (checkPermissions()) {
                if (!overlayManager.overlayVisible) showOverlay()
                KronoNavigator.startFocusMode(this)
            }
            
            CountdownViewModel.ACTION_COUNTDOWN_PLAY         -> id?.let { countdownManager.play(it) }
            CountdownViewModel.ACTION_COUNTDOWN_PAUSE        -> id?.let { countdownManager.pause(it) }
            CountdownViewModel.ACTION_COUNTDOWN_RESET        -> id?.let { countdownManager.reset(it) }
            CountdownViewModel.ACTION_COUNTDOWN_OVERLAY_SHOW -> id?.let { countdownManager.showOverlay(it) }
            CountdownViewModel.ACTION_COUNTDOWN_OVERLAY_HIDE -> id?.let { countdownManager.hideOverlay(it) }
            CountdownViewModel.ACTION_COUNTDOWN_SYNC         -> id?.let { countdownManager.syncOverlay(it) }
            CountdownViewModel.ACTION_COUNTDOWN_DESTROY      -> id?.let { countdownManager.destroy(it) }

            else -> if (checkPermissions() && !overlayManager.overlayVisible) showOverlay()
        }
        return START_STICKY
    }

    private fun checkPermissions(): Boolean {
        return if (PermissionUtils.hasEssentialPermissions(this)) {
            true
        } else {
            KronoNavigator.requestPermissions(this)
            false
        }
    }

    private fun ensureObserversStarted() {
        if (observersStarted) return
        observersStarted = true
        observeConfig()
        observeScreenState()
        observeTimerRunning()
        observeTimerLimit()
        observeDonationState()
        observeActiveTool()
    }

    private fun observeActiveTool() {
        serviceScope.launch {
            dataStore.configFlow
                .map { it.activeToolId }
                .distinctUntilChanged()
                .collect { id ->
                    activeTool = ToolRegistry.getTool(id)
                    startNotificationUpdater()
                }
        }
    }

    private fun showOverlay() {
        serviceScope.launch(Dispatchers.Main.immediate) {
            runCatching {
                if (activeTool == null) {
                    activeTool = ToolRegistry.getTool("stopwatch")
                        ?: StopwatchTool(dataStore, (application as KronoApp).stopwatchViewModel)
                }

                currentConfig = dataStore.configFlow.first()
                overlayManager.showOverlay(
                    currentConfig = currentConfig,
                    onStart = { 
                        if (checkPermissions()) {
                            activeViewModel?.start()
                            feedbackManager.triggerFeedback(currentConfig)
                        }
                    },
                    onPause = { handlePause(); feedbackManager.triggerFeedback(currentConfig) },
                    onReset = { handleReset() },
                    onClose = { hideOverlay() },
                    onSettings = { KronoNavigator.openSettings(this@MainService) },
                    onFocusModeStarted = { KronoNavigator.startFocusMode(this@MainService) }
                )
            }
        }
    }

    private fun handlePause() {
        val sessionMs = (activeViewModel as? StopwatchViewModel)?.currentSessionMs ?: 0L
        activeViewModel?.pause()
        serviceScope.launch {
            dataStore.accumulateTime(sessionMs)
            checkAndShowDonation()
        }
    }

    private fun handleReset() {
        val sessionMs = (activeViewModel as? StopwatchViewModel)?.currentSessionMs ?: 0L
        activeViewModel?.reset()
        serviceScope.launch {
            dataStore.accumulateTime(sessionMs)
            checkAndShowDonation()
        }
    }

    private fun checkAndShowDonation() {
        if (pendingDonation) {
            val wasVisible = overlayManager.overlayVisible
            if (wasVisible) hideOverlay()
            KronoNavigator.showDonation(this)
            pendingDonation = false
        }
    }

    private fun observeDonationState() {
        serviceScope.launch {
            dataStore.configFlow
                .map { it.donationPending }
                .distinctUntilChanged()
                .collect { pending -> pendingDonation = pending }
        }
    }

    private fun hideOverlay() {
        overlayManager.hideOverlay {
            sendBroadcast(Intent(ACTION_FOCUS_DISMISSED).apply { `package` = packageName })
        }
    }

    private fun closeAndStop() {
        activeViewModel?.reset()
        timerPrefs.clearState()
        timerPrefs.setServiceActive(false)
        wakeLockManager.release()
        overlayManager.removeOverlay()
        countdownManager.destroyAll()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    @SuppressLint("InlinedApi")
    private fun startForegroundWithNotification() {
        val state = activeViewModel?.toolState?.value as? com.krono.app.feature.stopwatch.StopwatchState 
            ?: com.krono.app.feature.stopwatch.StopwatchState()
            
        val notification = notificationHelper.buildNotification(state, currentConfig.showHours, currentConfig.showSeconds)
        val type = if (Build.VERSION.SDK_INT >= 34) android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE else 0
        
        if (type != 0) startForeground(NOTIFICATION_ID, notification, type)
        else startForeground(NOTIFICATION_ID, notification)
    }

    private fun startNotificationUpdater() {
        notificationJob?.cancel()
        val viewModel = activeViewModel ?: return
        notificationJob = serviceScope.launch {
            var lastState = viewModel.toolState.value
            viewModel.toolState.collectLatest { state ->
                val swState = state as? com.krono.app.feature.stopwatch.StopwatchState
                val lastSwState = lastState as? com.krono.app.feature.stopwatch.StopwatchState
                
                if (swState != null && lastSwState != null) {
                    val isReset = !swState.isRunning && swState.elapsedMs == 0L
                    if (swState.isRunning != lastSwState.isRunning || swState.isAtLimit != lastSwState.isAtLimit || isReset) {
                        val n = notificationHelper.buildNotification(swState, currentConfig.showHours, currentConfig.showSeconds)
                        (getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager).notify(NOTIFICATION_ID, n)
                    }
                }
                lastState = state
            }
        }
    }

    private fun observeConfig() {
        serviceScope.launch {
            dataStore.configFlow.collectLatest { config ->
                val focusTrigger = !currentConfig.focusModeEnabled && config.focusModeEnabled
                currentConfig = config
                (activeViewModel as? StopwatchViewModel)?.setTimeLimit(config.timeLimitSeconds)
                
                if (!config.focusModeEnabled) {
                    sendBroadcast(Intent(ACTION_FOCUS_DISMISSED).apply { `package` = packageName })
                } else if (focusTrigger && (activeViewModel?.toolState?.value?.isRunning == true) && overlayManager.overlayVisible) {
                    KronoNavigator.startFocusMode(this@MainService)
                }
            }
        }
    }

    private fun observeScreenState() {
        serviceScope.launch { 
            dataStore.configFlow.collect { config ->
                wakeLockManager.applyWakeLock(config.keepScreenOn)
                overlayManager.applyKeepScreenOn(config.keepScreenOn)
            }
        }
    }

    private fun observeTimerRunning() {
        serviceScope.launch {
            var wasRunning = activeViewModel?.toolState?.value?.isRunning == true
            activeViewModel?.toolState?.collect { state ->
                val started = !wasRunning && state.isRunning
                wasRunning = state.isRunning
                if (started && currentConfig.focusModeEnabled && overlayManager.overlayVisible) {
                    KronoNavigator.startFocusMode(this@MainService)
                }
            }
        }
    }

    private fun observeTimerLimit() {
        serviceScope.launch { activeViewModel?.toolState?.collect { if (it.isAtLimit) hideOverlay() } }
    }

    override fun onDestroy() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        if (activeViewModel?.toolState?.value?.isRunning == true) activeViewModel?.pause()
        timerPrefs.setServiceActive(false)
        wakeLockManager.release()
        overlayManager.removeOverlay()
        feedbackManager.release()
        notificationJob?.cancel()
        serviceScope.cancel()
        viewModelStore.clear()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
