package com.krono.app.service

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Rect
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
    val id: String,
    private val getPeers: () -> List<CountdownOverlayManager>,
    private val onPlay: () -> Unit,
    private val onPause: () -> Unit,
    private val onReset: () -> Unit,
    private val onClose: () -> Unit
) : LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    // ── Lifecycle ──────────────────────────────────────────────────────────
    private val lifecycleRegistry = LifecycleRegistry(this)
    override val lifecycle: Lifecycle get() = lifecycleRegistry
    private val _viewModelStore = ViewModelStore()
    override val viewModelStore: ViewModelStore get() = _viewModelStore
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    // ── State ──────────────────────────────────────────────────────────────
    private var composeView: ComposeView? = null
    private val overlayState = mutableStateOf<CountdownState?>(null)

    var posX: Int = 0; private set
    var posY: Int = 0; private set
    var overlayW: Int = 0; private set
    var overlayH: Int = 0; private set
    private var params: WindowManager.LayoutParams? = null

    private val dataStore = OverlayDataStore(context)

    // ── Smart initial Y ────────────────────────────────────────────────────

    private fun findFreeY(): Int {
        val dm = context.resources.displayMetrics
        val density = dm.density
        val screenH = dm.heightPixels
        val slotH = (190 * density).toInt()
        val marginX = (16 * density).toInt()
        val startY = (200 * density).toInt()
        val estW = (260 * density).toInt()

        var candidate = startY
        while (candidate + slotH < screenH) {
            val rect = Rect(marginX, candidate, marginX + estW, candidate + slotH)
            val blocked = getPeers().filter { it.id != id }.any { peer ->
                Rect.intersects(rect, peer.getBounds())
            }
            if (!blocked) return candidate
            candidate += slotH
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

    // ── Drag + snap + push ─────────────────────────────────────────────────

    private fun attachDrag(view: View) {
        val dm = context.resources.displayMetrics
        val screenW = dm.widthPixels
        val screenH = dm.heightPixels
        val density = dm.density
        val snap = (24 * density).toInt()

        var initRawX = 0f; var initRawY = 0f
        var initPX = 0; var initPY = 0

        view.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initRawX = event.rawX; initRawY = event.rawY
                    initPX = params?.x ?: 0; initPY = params?.y ?: 0
                    false  // allow click events to propagate to Compose
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = (event.rawX - initRawX).toInt()
                    val dy = (event.rawY - initRawY).toInt()
                    if (Math.abs(dx) < 8 && Math.abs(dy) < 8) return@setOnTouchListener false
                    val newX = (initPX + dx).coerceIn(0, screenW - overlayW)
                    val newY = (initPY + dy).coerceIn(0, screenH - overlayH)
                    moveTo(newX, newY)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    val cx = params?.x ?: 0
                    val snappedX = when {
                        cx < snap -> 0
                        cx > screenW - overlayW - snap -> screenW - overlayW
                        else -> cx
                    }
                    moveTo(snappedX, params?.y ?: 0)
                    pushPeers()
                    false
                }
                else -> false
            }
        }
    }

    fun moveTo(x: Int, y: Int) {
        posX = x; posY = y
        params?.let { p ->
            p.x = x; p.y = y
            composeView?.let { v -> runCatching { windowManager.updateViewLayout(v, p) } }
        }
    }

    /** After dropping, push any peer that now overlaps this overlay downward */
    private fun pushPeers() {
        val myRect = getBounds()
        getPeers().filter { it.id != id }.forEach { peer ->
            if (Rect.intersects(myRect, peer.getBounds())) {
                peer.moveTo(peer.posX, posY + overlayH + 16)
            }
        }
    }

    // ── Public API ─────────────────────────────────────────────────────────

    fun show(state: CountdownState) {
        if (composeView != null) { update(state); return }

        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED

        overlayState.value = state
        posX = 16
        posY = findFreeY()
        val lp = buildParams(posX, posY)
        params = lp

        composeView = ComposeView(context).apply {
            setViewTreeLifecycleOwner(this@CountdownOverlayManager)
            setViewTreeViewModelStoreOwner(this@CountdownOverlayManager)
            setViewTreeSavedStateRegistryOwner(this@CountdownOverlayManager)

            setContent {
                val config by dataStore.configFlow.collectAsState(initial = OverlayConfig())
                KronoTheme(selectedTheme = config.selectedTheme) {
                    overlayState.value?.let { s ->
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

            addOnLayoutChangeListener { _, l, t, r, b, _, _, _, _ ->
                overlayW = r - l
                overlayH = b - t
            }
        }

        attachDrag(composeView!!)
        windowManager.addView(composeView, lp)
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
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
        _viewModelStore.clear()
    }

    fun getBounds(): Rect = Rect(posX, posY, posX + overlayW, posY + overlayH)
}
