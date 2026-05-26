package com.krono.app.core.service

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.krono.app.core.data.OverlayConfig
import com.krono.app.core.data.OverlayDataStore
import com.krono.app.core.tool.ToolState
import com.krono.app.core.ui.theme.KronoTheme
import com.krono.app.feature.pomodoro.PomodoroOverlay
import com.krono.app.feature.stopwatch.StopwatchOverlay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OverlayManager(
    private val context: Context,
    private val windowManager: WindowManager,
    private val dataStore: OverlayDataStore,
    private val serviceScope: CoroutineScope,
    private val lifecycleOwner: LifecycleOwner,
    private val viewModelStoreOwner: ViewModelStoreOwner,
    private val savedStateRegistryOwner: SavedStateRegistryOwner
) {
    private data class OverlayEntry(
        val view: ComposeView,
        val params: WindowManager.LayoutParams,
        var visible: Boolean
    )

    private val overlays = mutableMapOf<String, OverlayEntry>()
    val overlayVisible: Boolean get() = overlays.values.any { it.visible }
    fun isOverlayVisible(toolId: String): Boolean = overlays[toolId]?.visible == true

    companion object {
        private const val EDGE_SNAP_THRESHOLD = 50
    }

    fun showOverlay(
        currentConfig: OverlayConfig,
        toolId: String,
        toolState: StateFlow<ToolState>,
        onStart: () -> Unit,
        onPause: () -> Unit,
        onReset: () -> Unit,
        onNext: () -> Unit,
        onClose: () -> Unit,
        onSettings: () -> Unit,
        onFocusModeStarted: () -> Unit
    ) {
        overlays[toolId]?.let { entry ->
            if (!entry.visible) showOverlayIfHidden(toolId)
            return
        }

        val (savedX, savedY) = savedPosition(currentConfig, toolId)
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = savedX
            y = savedY
        }

        val view = ComposeView(context).apply {
            setContent {
                val config by dataStore.configFlow.collectAsState(initial = OverlayConfig())
                val rawState = toolState.collectAsState().value

                KronoTheme(selectedTheme = config.selectedTheme, appFontSize = config.appFontSize) {
                    when (toolId) {
                        "pomodoro" -> {
                            val pomodoroState = (rawState as? com.krono.app.feature.pomodoro.PomodoroState)
                                ?: com.krono.app.feature.pomodoro.PomodoroState()
                            PomodoroOverlay(
                                state = pomodoroState,
                                config = config,
                                onPlay = onStart,
                                onPause = onPause,
                                onReset = onReset,
                                onNext = onNext,
                                onClose = onClose,
                                onNavigateToApp = onSettings,
                                onToggleFocus = {
                                    serviceScope.launch {
                                        dataStore.updateConfig(config.copy(focusModeEnabled = !config.focusModeEnabled))
                                    }
                                },
                                onToggleKeepScreenOn = {
                                    serviceScope.launch {
                                        dataStore.updateConfig(config.copy(keepScreenOn = !config.keepScreenOn))
                                    }
                                },
                                onToggleAutoLaunch = {
                                    serviceScope.launch {
                                        val enabled = !config.autoLaunch
                                        dataStore.updateConfig(
                                            config.copy(
                                                autoLaunch = enabled,
                                                directLaunchToolId = if (enabled) toolId else config.directLaunchToolId
                                            )
                                        )
                                    }
                                },
                                onToggleBeep = {
                                    serviceScope.launch {
                                        dataStore.updateConfig(
                                            config.copy(
                                                allSoundsEnabled = !config.allSoundsEnabled
                                            )
                                        )
                                    }
                                },
                                onDrag = { dx, dy -> handleDrag(toolId, dx, dy) },
                                onDragEnd = { saveOverlayPosition(toolId) },
                                onMenuVisibilityChange = { menuOpen -> setOverlayFocusable(toolId, menuOpen) }
                            )
                        }
                        "stopwatch" -> {
                            val swState = (rawState as? com.krono.app.feature.stopwatch.StopwatchState)
                                ?: com.krono.app.feature.stopwatch.StopwatchState()
                            StopwatchOverlay(
                                state = swState,
                                config = config,
                                onStart = onStart,
                                onPause = onPause,
                                onReset = onReset,
                                onDrag = { dx, dy -> handleDrag(toolId, dx, dy) },
                                onDragEnd = { saveOverlayPosition(toolId) },
                                onClose = onClose,
                                onSettings = onSettings,
                                onToggleFocus = {
                                    serviceScope.launch {
                                        dataStore.updateConfig(config.copy(focusModeEnabled = !config.focusModeEnabled))
                                    }
                                },
                                onToggleKeepScreenOn = {
                                    serviceScope.launch {
                                        dataStore.updateConfig(config.copy(keepScreenOn = !config.keepScreenOn))
                                    }
                                },
                                onToggleAutoLaunch = {
                                    serviceScope.launch {
                                        val enabled = !config.autoLaunch
                                        dataStore.updateConfig(
                                            config.copy(
                                                autoLaunch = enabled,
                                                directLaunchToolId = if (enabled) toolId else config.directLaunchToolId
                                            )
                                        )
                                    }
                                },
                                onToggleBeep = {
                                    serviceScope.launch {
                                        dataStore.updateConfig(
                                            config.copy(
                                                allSoundsEnabled = !config.allSoundsEnabled
                                            )
                                        )
                                    }
                                },
                                onMenuVisibilityChange = { menuOpen -> setOverlayFocusable(toolId, menuOpen) }
                            )
                        }
                    }
                }
            }
        }

        view.setViewTreeLifecycleOwner(lifecycleOwner)
        view.setViewTreeViewModelStoreOwner(viewModelStoreOwner)
        view.setViewTreeSavedStateRegistryOwner(savedStateRegistryOwner)

        try {
            windowManager.addView(view, params)
            overlays[toolId] = OverlayEntry(view = view, params = params, visible = true)
            if (currentConfig.focusModeEnabled) {
                onFocusModeStarted()
            }
        } catch (e: Exception) {
            android.util.Log.e("OverlayManager", "Error adding $toolId overlay: ${e.message}")
        }
    }

    fun removeOverlay(toolId: String? = null) {
        val keys = toolId?.let { listOf(it) } ?: overlays.keys.toList()
        keys.forEach { key ->
            val entry = overlays.remove(key) ?: return@forEach
            try { windowManager.removeView(entry.view) } catch (_: Exception) { }
        }
    }

    fun hideOverlay(toolId: String? = null, onDismissFocus: () -> Unit) {
        onDismissFocus()
        val entries = toolId?.let { id -> overlays[id]?.let { listOf(id to it) }.orEmpty() }
            ?: overlays.entries.map { it.key to it.value }

        entries.forEach { (_, entry) ->
            if (entry.visible) {
                try { windowManager.removeView(entry.view) } catch (_: Exception) { }
                entry.visible = false
            }
        }
    }

    fun showOverlayIfHidden(toolId: String) {
        val entry = overlays[toolId] ?: return
        if (!entry.visible) {
            try {
                windowManager.addView(entry.view, entry.params)
                entry.visible = true
            } catch (_: Exception) { }
        }
    }

    fun setOverlayFocusable(toolId: String, focusable: Boolean) {
        val entry = overlays[toolId] ?: return
        entry.params.flags = if (focusable) {
            entry.params.flags and WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE.inv()
        } else {
            entry.params.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        }
        try { windowManager.updateViewLayout(entry.view, entry.params) } catch (_: Exception) { }
    }

    private fun handleDrag(toolId: String, dx: Float, dy: Float) {
        val entry = overlays[toolId] ?: return

        val widgetWidth = entry.view.width.takeIf { it > 0 } ?: return
        val widgetHeight = entry.view.height.takeIf { it > 0 } ?: return

        val screenWidth = context.resources.displayMetrics.widthPixels
        val screenHeight = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            windowManager.currentWindowMetrics.bounds.height()
        } else {
            context.resources.displayMetrics.heightPixels
        }

        var newX = entry.params.x + dx.toInt()
        var newY = entry.params.y + dy.toInt()

        val rightEdge = screenWidth - widgetWidth
        val bottomEdge = screenHeight - widgetHeight

        when {
            newX <= EDGE_SNAP_THRESHOLD && dx <= 0 -> newX = 0
            newX >= rightEdge - EDGE_SNAP_THRESHOLD && dx >= 0 -> newX = rightEdge
        }
        when {
            newY <= EDGE_SNAP_THRESHOLD && dy <= 0 -> newY = 0
            newY >= bottomEdge - EDGE_SNAP_THRESHOLD && dy >= 0 -> newY = bottomEdge
        }

        entry.params.x = newX.coerceIn(0, maxOf(0, rightEdge))
        entry.params.y = newY.coerceIn(0, maxOf(0, bottomEdge))

        try { windowManager.updateViewLayout(entry.view, entry.params) } catch (_: Exception) { }
    }

    private fun saveOverlayPosition(toolId: String) {
        val entry = overlays[toolId] ?: return
        serviceScope.launch {
            dataStore.savePosition(toolId, entry.params.x, entry.params.y)
        }
    }

    fun applyKeepScreenOn(enable: Boolean) {
        overlays.values.forEach { entry ->
            if (enable) {
                entry.params.flags = entry.params.flags or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            } else {
                entry.params.flags = entry.params.flags and WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON.inv()
            }
            if (entry.visible) {
                try { windowManager.updateViewLayout(entry.view, entry.params) } catch (_: Exception) { }
            }
        }
    }

    private fun savedPosition(config: OverlayConfig, toolId: String): Pair<Int, Int> {
        return when (toolId) {
            "pomodoro" -> {
                val x = config.pomodoroOverlayLastX.takeIf { it >= 0 } ?: 100
                val y = config.pomodoroOverlayLastY.takeIf { it >= 0 } ?: 320
                x to y
            }
            else -> {
                val x = config.stopwatchOverlayLastX.takeIf { it >= 0 } ?: 100
                val y = config.stopwatchOverlayLastY.takeIf { it >= 0 } ?: 200
                x to y
            }
        }
    }
}
