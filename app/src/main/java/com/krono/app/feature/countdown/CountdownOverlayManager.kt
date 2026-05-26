package com.krono.app.feature.countdown

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Rect
import android.util.Log
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
import com.krono.app.feature.countdown.CountdownState
import com.krono.app.core.data.OverlayConfig
import com.krono.app.core.data.OverlayDataStore
import com.krono.app.core.ui.theme.KronoTheme
import com.krono.app.core.util.KronoNavigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

class CountdownOverlayManager(
    private val context: Context,
    private val windowManager: WindowManager,
    val id: String,
    private val getPeers: () -> List<CountdownOverlayManager>,
    private val onPlay: () -> Unit,
    private val onPause: () -> Unit,
    private val onReset: () -> Unit,
    private val onPlusOne: () -> Unit,
    private val onClose: () -> Unit
) : LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {
    companion object {
        private const val SCREEN_OVERLAY_ID = CountdownViewModel.SCREEN_OVERLAY_ID
    }

    // ── Lifecycle boilerplate ──────────────────────────────────────────────
    private val lifecycleRegistry = LifecycleRegistry(this)
    override val lifecycle: Lifecycle get() = lifecycleRegistry
    private val _vmStore = ViewModelStore()
    override val viewModelStore: ViewModelStore get() = _vmStore
    private val ssController = SavedStateRegistryController.create(this)
    override val savedStateRegistry: SavedStateRegistry get() = ssController.savedStateRegistry

    // ── State ──────────────────────────────────────────────────────────────
    private var composeView: ComposeView? = null
    private val overlayState = mutableStateOf<CountdownState?>(null)
    private val overlayDataStore = OverlayDataStore(context)
    private val overlayScope = CoroutineScope(SupervisorJob())

    var posX: Int = 0; private set
    var posY: Int = 0; private set
    var overlayW: Int = 0; private set
    var overlayH: Int = 0; private set
    private var params: WindowManager.LayoutParams? = null

    // ── Helpers ────────────────────────────────────────────────────────────
    private val density get() = context.resources.displayMetrics.density
    private val screenW get() = context.resources.displayMetrics.widthPixels
    private val screenH get() = context.resources.displayMetrics.heightPixels

    // Snap threshold: 24dp from edge
    private val edgeSnapPx get() = (24 * density).roundToInt()
    // Magnet distance: 28dp gap triggers snap-to-peer
    private val magnetPx get() = (28 * density).roundToInt()

    // ── Smart initial position — find free slot ────────────────────────────

    private fun findFreeY(): Int {
        val slotH = (160 * density).roundToInt()
        val startY = (160 * density).roundToInt()
        val startX = (16 * density).roundToInt()
        val estW   = (260 * density).roundToInt()

        var candidate = startY
        while (candidate + slotH < screenH) {
            val test = Rect(startX, candidate, startX + estW, candidate + slotH)
            val blocked = getPeers().filter { it.id != id }.any { Rect.intersects(test, it.getBounds()) }
            if (!blocked) return candidate
            candidate += slotH + (8 * density).roundToInt()
        }
        return startY
    }

    // ── Layout params ──────────────────────────────────────────────────────

    private fun buildParams(x: Int, y: Int) = WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
        PixelFormat.TRANSLUCENT
    ).apply {
        gravity = Gravity.TOP or Gravity.START
        this.x = x; this.y = y
    }

    // ── Move ───────────────────────────────────────────────────────────────

    fun moveTo(x: Int, y: Int) {
        posX = x.coerceIn(0, (screenW - overlayW).coerceAtLeast(0))
        posY = y.coerceIn(0, (screenH - overlayH).coerceAtLeast(0))
        params?.let { p ->
            p.x = posX; p.y = posY
            composeView?.let { v -> runCatching { windowManager.updateViewLayout(v, p) } }
        }
    }

    // Called from Compose drag gesture (pixels from the Composable's local coords)
    fun onDrag(dx: Float, dy: Float) {
        moveTo(posX + dx.roundToInt(), posY + dy.roundToInt())
    }

    fun onDragEnd() {
        snapToEdge()
        applyMagnetism()
    }

    private fun setOverlayFocusable(focusable: Boolean) {
        val p = params ?: return
        p.flags = if (focusable) {
            p.flags and WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE.inv()
        } else {
            p.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        }
        composeView?.let { view -> runCatching { windowManager.updateViewLayout(view, p) } }
    }

    // ── Snap to nearest screen edge ────────────────────────────────────────

    private fun snapToEdge() {
        val cx = posX
        val snappedX = when {
            cx < edgeSnapPx -> 0
            cx > screenW - overlayW - edgeSnapPx -> screenW - overlayW
            else -> cx
        }
        moveTo(snappedX, posY)
    }

    // ── Magnetic snap to peers ─────────────────────────────────────────────

    /**
     * After drag ends:
     * 1. If gap between this and a peer < [magnetPx] → snap flush (magnetic glue)
     * 2. If overlapping → push this below the peer
     * 3. Then push any peer this overlay now covers downward
     */
    private fun applyMagnetism() {
        val peers = getPeers().filter { it.id != id }

        for (peer in peers) {
            val pRect = peer.getBounds()

            if (Rect.intersects(getBounds(), pRect)) {
                // Overlapping → push this below peer
                moveTo(posX, pRect.bottom + (4 * density).roundToInt())
                return
            }

            val gapBelow = posY - pRect.bottom      // gap if this is below peer
            val gapAbove = pRect.top - (posY + overlayH) // gap if this is above peer

            when {
                gapBelow in 0..magnetPx -> {
                    moveTo(posX, pRect.bottom + (2 * density).roundToInt())
                    return
                }
                gapAbove in 0..magnetPx -> {
                    moveTo(posX, pRect.top - overlayH - (2 * density).roundToInt())
                    return
                }
            }
        }

        // Final position: push any peer now underneath this overlay
        val finalRect = getBounds()
        for (peer in peers) {
            if (Rect.intersects(finalRect, peer.getBounds())) {
                peer.moveTo(peer.posX, posY + overlayH + (4 * density).roundToInt())
            }
        }
    }

    // ── Public API ─────────────────────────────────────────────────────────

    fun show(state: CountdownState) {
        if (composeView != null) { update(state); return }

        ssController.performRestore(null)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED

        overlayState.value = state
        posX = (16 * density).roundToInt()
        posY = findFreeY()

        val lp = buildParams(posX, posY)
        params = lp

        composeView = ComposeView(context).apply {
            setViewTreeLifecycleOwner(this@CountdownOverlayManager)
            setViewTreeViewModelStoreOwner(this@CountdownOverlayManager)
            setViewTreeSavedStateRegistryOwner(this@CountdownOverlayManager)

            setContent {
                val overlayConfig by overlayDataStore.configFlow.collectAsState(initial = OverlayConfig())
                KronoTheme(selectedTheme = overlayConfig.selectedTheme, appFontSize = overlayConfig.appFontSize) {
                    overlayState.value?.let { s ->
                        val showPlusOne = this@CountdownOverlayManager.id == SCREEN_OVERLAY_ID
                        CountdownOverlayUi(
                            state = s,
                            config = overlayConfig,
                            useToolColor = showPlusOne,
                            showPlusOne = showPlusOne,
                            onPlay = onPlay,
                            onPause = onPause,
                            onReset = onReset,
                            onPlusOne = onPlusOne,
                            onClose = onClose,
                            onNavigateToApp = {
                                KronoNavigator.openTool(context, "countdown")
                            },
                            onToggleFocus = {
                                overlayScope.launch {
                                    overlayDataStore.updateConfig(
                                        overlayConfig.copy(focusModeEnabled = !overlayConfig.focusModeEnabled)
                                    )
                                }
                            },
                            onToggleKeepScreenOn = {
                                overlayScope.launch {
                                    overlayDataStore.updateConfig(
                                        overlayConfig.copy(keepScreenOn = !overlayConfig.keepScreenOn)
                                    )
                                }
                            },
                            onToggleAutoLaunch = {
                                overlayScope.launch {
                                    val enabled = !overlayConfig.autoLaunch
                                    overlayDataStore.updateConfig(
                                        overlayConfig.copy(
                                            autoLaunch = enabled,
                                            directLaunchToolId = if (enabled) "countdown" else overlayConfig.directLaunchToolId
                                        )
                                    )
                                }
                            },
                            onToggleBeep = {
                                overlayScope.launch {
                                    overlayDataStore.updateConfig(
                                        overlayConfig.copy(
                                            allSoundsEnabled = !overlayConfig.allSoundsEnabled
                                        )
                                    )
                                }
                            },
                            onDrag = { dx, dy -> onDrag(dx, dy) },
                            onDragEnd = { onDragEnd() },
                            onMenuVisibilityChange = ::setOverlayFocusable
                        )
                    }
                }
            }

            addOnLayoutChangeListener { _, l, t, r, b, _, _, _, _ ->
                overlayW = r - l
                overlayH = b - t
            }
        }

        runCatching {
            windowManager.addView(composeView, lp)
        }.onSuccess {
            lifecycleRegistry.currentState = Lifecycle.State.RESUMED
        }.onFailure { error ->
            Log.e("CountdownOverlayManager", "Error adding countdown overlay $id", error)
            onClose()
        }
    }

    fun update(state: CountdownState) {
        overlayState.value = state
    }

    fun hide() {
        composeView?.let { runCatching { windowManager.removeView(it) } }
        composeView = null
        if (lifecycleRegistry.currentState != Lifecycle.State.DESTROYED) {
            lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        }
        overlayScope.cancel()
        _vmStore.clear()
    }

    fun applyKeepScreenOn(enable: Boolean) {
        val p = params ?: return
        p.flags = if (enable) {
            p.flags or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        } else {
            p.flags and WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON.inv()
        }
        composeView?.let { view -> runCatching { windowManager.updateViewLayout(view, p) } }
    }

    fun getBounds(): Rect = Rect(posX, posY, posX + overlayW, posY + overlayH)
}

