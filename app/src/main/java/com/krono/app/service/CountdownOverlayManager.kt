package com.krono.app.service

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.krono.app.data.CountdownState
import com.krono.app.data.OverlayConfig
import com.krono.app.data.OverlayDataStore
import com.krono.app.ui.CountdownOverlayUi
import com.krono.app.ui.theme.KronoTheme

class CountdownOverlayManager(
    private val context: Context,
    private val windowManager: WindowManager,
    private val index: Int,               // position index to avoid overlap
    private val onPlay: () -> Unit,
    private val onPause: () -> Unit,
    private val onReset: () -> Unit,
    private val onClose: () -> Unit
) : LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    private val dataStore = OverlayDataStore(context)

    // ── Lifecycle boilerplate (same pattern as OverlayManager) ─────────────

    private val lifecycleRegistry = LifecycleRegistry(this)
    override val lifecycle: Lifecycle get() = lifecycleRegistry

    private val _viewModelStore = ViewModelStore()
    override val viewModelStore: ViewModelStore get() = _viewModelStore

    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    // ── State ──────────────────────────────────────────────────────────────

    private var composeView: ComposeView? = null
    private val countdownState = mutableStateOf<CountdownState?>(null)

    // ── Layout params ──────────────────────────────────────────────────────

    private fun buildLayoutParams(): WindowManager.LayoutParams {
        val overlayHeight = 180   // dp approx — same as main overlay
        val spacing = 16
        val yOffset = index * (overlayHeight + spacing)

        return WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.END
            x = 16
            y = 200 + yOffset   // offset below status bar + index spacing
        }
    }

    // ── Public API ─────────────────────────────────────────────────────────

    fun show(state: CountdownState) {
        if (composeView != null) {
            update(state)
            return
        }

        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED

        countdownState.value = state

        composeView = ComposeView(context).apply {
            setViewTreeLifecycleOwner(this@CountdownOverlayManager)
            setViewTreeViewModelStoreOwner(this@CountdownOverlayManager)
            setViewTreeSavedStateRegistryOwner(this@CountdownOverlayManager)

            setContent {
                val config by dataStore.configFlow.collectAsState(initial = OverlayConfig())
                KronoTheme(selectedTheme = config.selectedTheme) {
                    countdownState.value?.let { s ->
                        CountdownOverlayUi(
                            state = s,
                            onPlay = onPlay,
                            onPause = onPause,
                            onReset = onReset,
                            onClose = onClose
                        )
                    }
                }
            }
        }

        windowManager.addView(composeView, buildLayoutParams())
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
    }

    fun update(state: CountdownState) {
        countdownState.value = state
    }

    fun hide() {
        composeView?.let {
            runCatching { windowManager.removeView(it) }
        }
        composeView = null
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        _viewModelStore.clear()
    }
}
