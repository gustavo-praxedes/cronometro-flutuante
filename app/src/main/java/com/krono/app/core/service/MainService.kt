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
import com.krono.app.EXTRA_TOOL_ID
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

        overlayManager = OverlayManager(this, windowManager, dataStore, serviceScope, this, this, this)

        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)

        countdownManager = CountdownManager(
            context = this,
            windowManager = windowManager,
            feedbackManager = feedbackManager,
            notificationHelper = notificationHelper,
            countdownViewModel = app.countdownViewModel,
            currentConfigProvider = { currentConfig }
        )

        startForegroundWithNotification()
        timerPrefs.setServiceActive(true)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        ensureObserversStarted()
        val app = application as KronoApp

        val id = intent?.getStringExtra(CountdownViewModel.EXTRA_COUNTDOWN_ID)
        val targetToolId = intent?.getStringExtra(EXTRA_TOOL_ID)

        when (intent?.action) {
            ACTION_PLAY -> {
                if (!checkPermissions()) return START_STICKY
                viewModelForTarget(targetToolId)?.start()
                triggerFeedbackFromLatestConfig()
            }
            ACTION_PAUSE -> {
                handlePause(targetToolId)
                triggerFeedbackFromLatestConfig()
            }
            ACTION_RESET -> handleReset(targetToolId)
            ACTION_STOP_SERVICE -> closeAndStop()
            ACTION_SHOW_OVERLAY -> if (checkPermissions()) showOverlay(targetToolId)
            ACTION_HIDE_OVERLAY -> hideOverlay(targetToolId)
            ACTION_START_FOCUS -> if (checkPermissions()) {
                val focusToolId = targetToolId ?: currentConfig.activeToolId
                if (!overlayManager.isOverlayVisible(focusToolId)) showOverlay(focusToolId)
                KronoNavigator.startFocusMode(this)
            }
            
            CountdownViewModel.ACTION_COUNTDOWN_PLAY         -> id?.let {
                countdownManager.play(it)
                if (intent.getBooleanExtra(CountdownViewModel.EXTRA_FEEDBACK_HANDLED, false).not()) {
                    triggerFeedbackFromLatestConfig()
                }
                if (currentConfig.focusModeEnabled) KronoNavigator.startFocusMode(this)
            }
            CountdownViewModel.ACTION_COUNTDOWN_PAUSE        -> id?.let {
                countdownManager.pause(it)
                if (intent.getBooleanExtra(CountdownViewModel.EXTRA_FEEDBACK_HANDLED, false).not()) {
                    triggerFeedbackFromLatestConfig()
                }
            }
            CountdownViewModel.ACTION_COUNTDOWN_RESET        -> id?.let {
                val alreadyAccumulated = intent.getBooleanExtra(CountdownViewModel.EXTRA_COUNTDOWN_ACCUMULATED, false)
                if (!alreadyAccumulated) {
                    app.countdownViewModel.accumulateElapsedForId(it)
                }
                countdownManager.reset(it)
            }
            CountdownViewModel.ACTION_COUNTDOWN_OVERLAY_SHOW -> if (checkPermissions()) {
                id?.let { countdownManager.showOverlay(it) }
            }
            CountdownViewModel.ACTION_COUNTDOWN_OVERLAY_HIDE -> id?.let { countdownManager.hideOverlay(it) }
            CountdownViewModel.ACTION_COUNTDOWN_SYNC         -> id?.let { countdownManager.syncOverlay(it) }
            CountdownViewModel.ACTION_COUNTDOWN_PLUS_ONE     -> id?.let { countdownManager.addOneMinute(it) }
            CountdownViewModel.ACTION_COUNTDOWN_SET_SECONDS  -> {
                val seconds = intent.getLongExtra(CountdownViewModel.EXTRA_COUNTDOWN_SECONDS, 0L)
                id?.let { countdownManager.setRemaining(it, seconds) }
            }
            CountdownViewModel.ACTION_COUNTDOWN_DESTROY      -> id?.let { countdownManager.destroy(it) }

            else -> if (checkPermissions() && !overlayManager.overlayVisible) showOverlay(targetToolId)
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

    private fun showOverlay(toolId: String? = null) {
        if (!checkPermissions()) return
        serviceScope.launch(Dispatchers.Main.immediate) {
            runCatching {
                if (activeTool == null) {
                    activeTool = ToolRegistry.getTool("stopwatch")
                        ?: StopwatchTool(dataStore, (application as KronoApp).stopwatchViewModel)
                }

                currentConfig = dataStore.configFlow.first()
                val hasExplicitTarget = !toolId.isNullOrBlank()
                val requestedToolId = toolId?.takeIf { it.isNotBlank() } ?: currentConfig.activeToolId
                if (requestedToolId == "countdown") {
                    countdownManager.showOverlay(CountdownViewModel.SCREEN_OVERLAY_ID)
                    return@runCatching
                }

                val overlayTool = ToolRegistry.getTool(requestedToolId)
                    ?: if (!hasExplicitTarget) {
                        ToolRegistry.getTool("stopwatch")
                            ?: StopwatchTool(dataStore, (application as KronoApp).stopwatchViewModel)
                    } else {
                        return@runCatching
                    }
                if (overlayTool.id != "stopwatch" && overlayTool.id != "pomodoro") {
                    return@runCatching
                }
                val overlayViewModel = overlayTool.viewModel
                overlayManager.showOverlay(
                    currentConfig = currentConfig,
                    toolId = overlayTool.id,
                    toolState = overlayViewModel.toolState,
                    onStart = { 
                        if (checkPermissions()) {
                            overlayViewModel.start()
                            triggerFeedbackFromLatestConfig()
                        }
                    },
                    onPause = {
                        overlayViewModel.pause()
                        triggerFeedbackFromLatestConfig()
                    },
                    onReset = { overlayViewModel.reset() },
                    onNext = { (overlayViewModel as? com.krono.app.feature.pomodoro.PomodoroViewModel)?.skipPhase() },
                    onClose = { hideOverlay(overlayTool.id) },
                    onSettings = { KronoNavigator.openTool(this@MainService, overlayTool.id) },
                    onFocusModeStarted = { KronoNavigator.startFocusMode(this@MainService) }
                )
            }
        }
    }

    private fun handlePause(toolId: String? = null) {
        viewModelForTarget(toolId)?.pause()
    }

    private fun handleReset(toolId: String? = null) {
        viewModelForTarget(toolId)?.reset()
    }

    private fun triggerFeedbackFromLatestConfig() {
        serviceScope.launch(Dispatchers.Main.immediate) {
            val latest = dataStore.configFlow.first()
            currentConfig = latest
            feedbackManager.triggerFeedback(latest)
        }
    }

    private fun viewModelForTarget(toolId: String?): ToolViewModel? {
        val hasExplicitTarget = !toolId.isNullOrBlank()
        val targetId = toolId?.takeIf { it.isNotBlank() } ?: currentConfig.activeToolId
        if (targetId == "countdown") return null
        return ToolRegistry.getTool(targetId)?.viewModel
            ?: if (!hasExplicitTarget) activeViewModel else null
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
                .collect { pending ->
                    pendingDonation = pending
                    if (pending) checkAndShowDonation()
                }
        }
    }

    private fun hideOverlay(toolId: String? = null) {
        if (toolId == "countdown") {
            countdownManager.hideOverlay(CountdownViewModel.SCREEN_OVERLAY_ID)
            sendBroadcast(Intent(ACTION_FOCUS_DISMISSED).apply { `package` = packageName })
            return
        }
        overlayManager.hideOverlay(toolId) {
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
            
        val notification = notificationHelper.buildNotification(state, currentConfig.showHours, currentConfig.showMinutes, currentConfig.showSeconds)
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
                        val n = notificationHelper.buildNotification(swState, currentConfig.showHours, currentConfig.showMinutes, currentConfig.showSeconds)
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
                if (!config.focusModeEnabled) {
                    sendBroadcast(Intent(ACTION_FOCUS_DISMISSED).apply { `package` = packageName })
                } else if (focusTrigger && isAnyToolRunning()) {
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
                countdownManager.applyKeepScreenOn(config.keepScreenOn)
            }
        }
    }

    private fun observeTimerRunning() {
        serviceScope.launch {
            var wasRunning = activeViewModel?.toolState?.value?.isRunning == true
            activeViewModel?.toolState?.collect { state ->
                val started = !wasRunning && state.isRunning
                wasRunning = state.isRunning
                if (started && currentConfig.focusModeEnabled) {
                    KronoNavigator.startFocusMode(this@MainService)
                }
            }
        }
    }

    private fun isAnyToolRunning(): Boolean =
        ToolRegistry.getAllTools().any { tool ->
            tool.id != "countdown" && tool.viewModel.toolState.value.isRunning
        } || countdownManager.hasRunningTimer()

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
