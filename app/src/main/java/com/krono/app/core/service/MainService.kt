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
import com.krono.app.util.KronoNavigator
import com.krono.app.util.PermissionUtils
import com.krono.app.viewmodel.CountdownViewModel
import com.krono.app.viewmodel.TimerViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.distinctUntilChanged
import androidx.lifecycle.*
import androidx.savedstate.*
import android.provider.Settings

const val ACTION_FOCUS_DISMISSED = "com.krono.app.ACTION_FOCUS_DISMISSED"

/**
 * MainService: O orquestrador central do cronômetro e do overlay.
 * Focado apenas na lógica de fluxo e delegação de tarefas.
 */
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

    private val viewModel: TimerViewModel get() = (application as KronoApp).timerViewModel
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
        overlayManager     = OverlayManager(this, windowManager, dataStore, viewModel, serviceScope, this, this, this)

        val app = application as KronoApp
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
                viewModel.start()
                feedbackManager.triggerFeedback(currentConfig)
            }
            ACTION_PAUSE -> {
                handlePause()
                feedbackManager.triggerFeedback(currentConfig)
            }
            ACTION_RESET -> handleReset()
            ACTION_STOP_SERVICE -> closeAndStop()
            ACTION_SHOW_OVERLAY -> if (checkPermissions()) overlayManager.showOverlayIfHidden()
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
        startNotificationUpdater()
        observeTimerLimit()
        observeDonationState()
    }

    private fun showOverlay() {
        serviceScope.launch {
            currentConfig = dataStore.configFlow.first()
            overlayManager.showOverlay(
                currentConfig = currentConfig,
                onStart = { 
                    if (checkPermissions()) {
                        viewModel.start()
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

    private fun handlePause() {
        val sessionMs = viewModel.currentSessionMs
        viewModel.pause()
        serviceScope.launch {
            dataStore.accumulateTime(sessionMs)
            checkAndShowDonation()
        }
    }

    private fun handleReset() {
        val sessionMs = viewModel.currentSessionMs
        viewModel.reset()
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
        viewModel.reset()
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
        val notification = notificationHelper.buildNotification(viewModel.timerState.value, currentConfig.showHours, currentConfig.showSeconds)
        val type = if (Build.VERSION.SDK_INT >= 34) android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE else 0
        
        if (type != 0) startForeground(NOTIFICATION_ID, notification, type)
        else startForeground(NOTIFICATION_ID, notification)
    }

    private fun startNotificationUpdater() {
        notificationJob?.cancel()
        notificationJob = serviceScope.launch {
            var lastState = viewModel.timerState.value
            viewModel.timerState.collectLatest { state ->
                val isReset = !state.isRunning && state.elapsedMs == 0L
                if (state.isRunning != lastState.isRunning || state.isAtLimit != lastState.isAtLimit || isReset) {
                    val n = notificationHelper.buildNotification(state, currentConfig.showHours, currentConfig.showSeconds)
                    (getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager).notify(NOTIFICATION_ID, n)
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
                viewModel.setTimeLimit(config.timeLimitSeconds)
                
                if (!config.focusModeEnabled) {
                    sendBroadcast(Intent(ACTION_FOCUS_DISMISSED).apply { `package` = packageName })
                } else if (focusTrigger && viewModel.timerState.value.isRunning && overlayManager.overlayVisible) {
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
            var wasRunning = viewModel.timerState.value.isRunning
            viewModel.timerState.collect { state ->
                val started = !wasRunning && state.isRunning
                wasRunning = state.isRunning
                if (started && currentConfig.focusModeEnabled && overlayManager.overlayVisible) {
                    KronoNavigator.startFocusMode(this@MainService)
                }
            }
        }
    }

    private fun observeTimerLimit() {
        serviceScope.launch { viewModel.timerState.collect { if (it.isAtLimit) hideOverlay() } }
    }

    override fun onDestroy() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        if (viewModel.timerState.value.isRunning) viewModel.pause()
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
