package com.krono.app.service

import android.content.Context
import android.view.WindowManager
import com.krono.app.data.CountdownState
import com.krono.app.viewmodel.CountdownViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CountdownManager(
    private val context: Context,
    private val windowManager: WindowManager,
    private val feedbackManager: FeedbackManager,
    private val countdownViewModel: CountdownViewModel
) {
    private val scope = CoroutineScope(SupervisorJob())

    // id → active coroutine job
    private val activeTimers = mutableMapOf<String, Job>()

    // id → overlay manager instance
    private val activeOverlays = mutableMapOf<String, CountdownOverlayManager>()

    // ── Timer control ──────────────────────────────────────────────────────

    fun play(id: String) {
        val state = currentState(id) ?: return
        if (state.isRunning || state.isCompleted) return

        activeTimers[id]?.cancel()
        activeTimers[id] = scope.launch {
            var remaining = state.remainingSeconds
            while (remaining > 0) {
                delay(1_000)
                remaining--
                countdownViewModel.onTick(id, remaining)
                activeOverlays[id]?.update(currentState(id) ?: return@launch)
            }
            onCompleted(id)
        }
    }

    fun pause(id: String) {
        activeTimers[id]?.cancel()
        activeTimers.remove(id)
    }

    fun reset(id: String) {
        activeTimers[id]?.cancel()
        activeTimers.remove(id)
        activeOverlays[id]?.update(currentState(id) ?: return)
    }

    // ── Overlay control ────────────────────────────────────────────────────

    fun showOverlay(id: String) {
        val state = currentState(id) ?: return
        val index = activeOverlays.size   // next available slot

        val overlay = activeOverlays.getOrPut(id) {
            CountdownOverlayManager(
                context = context,
                windowManager = windowManager,
                index = index,
                onPlay  = { play(id) },
                onPause = { pause(id) },
                onReset = { reset(id); countdownViewModel.reset(context, id) },
                onClose = { hideOverlay(id); countdownViewModel.toggleOverlay(context, id) }
            )
        }
        overlay.show(state)
    }

    fun hideOverlay(id: String) {
        activeOverlays[id]?.hide()
        activeOverlays.remove(id)
    }

    fun destroy(id: String) {
        activeTimers[id]?.cancel()
        activeTimers.remove(id)
        hideOverlay(id)
    }

    fun destroyAll() {
        activeTimers.values.forEach { it.cancel() }
        activeTimers.clear()
        activeOverlays.values.forEach { it.hide() }
        activeOverlays.clear()
        scope.cancel()
    }

    // ── Internal ───────────────────────────────────────────────────────────

    private fun onCompleted(id: String) {
        activeTimers.remove(id)
        countdownViewModel.onCompleted(id)
        feedbackManager.onCountdownCompleted()   // vibrate + beep
        activeOverlays[id]?.update(currentState(id) ?: return)
    }

    private fun currentState(id: String): CountdownState? =
        countdownViewModel.countdowns.value.find { it.config.id == id }
}
