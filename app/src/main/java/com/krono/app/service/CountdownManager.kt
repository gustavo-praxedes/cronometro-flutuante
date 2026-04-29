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
    private val notificationHelper: NotificationHelper,
    private val countdownViewModel: CountdownViewModel
) {
    private val scope = CoroutineScope(SupervisorJob())
    private val activeTimers = mutableMapOf<String, Job>()

    // id → overlay. Passed as lambda so each overlay can query current peers.
    private val activeOverlays = mutableMapOf<String, CountdownOverlayManager>()
    private fun peers(): List<CountdownOverlayManager> = activeOverlays.values.toList()

    // ── Timer ──────────────────────────────────────────────────────────────

    fun play(id: String) {
        val state = current(id) ?: return
        if (state.isRunning || state.isCompleted) return

        activeTimers[id]?.cancel()
        activeTimers[id] = scope.launch {
            var remaining = state.remainingSeconds
            while (remaining > 0) {
                delay(1_000)
                remaining--
                countdownViewModel.onTick(id, remaining)
                // Sync overlay UI
                current(id)?.let { s ->
                    activeOverlays[id]?.update(s)
                    if (s.isOverlayVisible) notificationHelper.postCountdownNotification(s)
                }
            }
            onCompleted(id)
        }
    }

    fun pause(id: String) {
        activeTimers[id]?.cancel()
        activeTimers.remove(id)
        current(id)?.let { s ->
            activeOverlays[id]?.update(s)
            notificationHelper.postCountdownNotification(s)
        }
    }

    fun reset(id: String) {
        activeTimers[id]?.cancel()
        activeTimers.remove(id)
        current(id)?.let { s ->
            activeOverlays[id]?.update(s)
            notificationHelper.cancelCountdownNotification(id)
        }
    }

    // ── Overlay ────────────────────────────────────────────────────────────

    fun showOverlay(id: String) {
        val state = current(id) ?: return
        val overlay = activeOverlays.getOrPut(id) {
            CountdownOverlayManager(
                context = context,
                windowManager = windowManager,
                id = id,
                getPeers = ::peers,
                onPlay  = { play(id); countdownViewModel.play(context, id) },
                onPause = { pause(id); countdownViewModel.pause(context, id) },
                onReset = { reset(id); countdownViewModel.reset(context, id) },
                onClose = {
                    hideOverlay(id)
                    countdownViewModel.forceHideOverlay(context, id)
                }
            )
        }
        overlay.show(state)
        notificationHelper.postCountdownNotification(state)
    }

    fun hideOverlay(id: String) {
        activeOverlays[id]?.hide()
        activeOverlays.remove(id)
        notificationHelper.cancelCountdownNotification(id)
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

    // ── Helpers ────────────────────────────────────────────────────────────

    private fun onCompleted(id: String) {
        activeTimers.remove(id)
        countdownViewModel.onCompleted(id)
        feedbackManager.onCountdownCompleted()
        current(id)?.let { s ->
            activeOverlays[id]?.update(s)
            notificationHelper.postCountdownNotification(s)
        }
    }

    private fun current(id: String): CountdownState? =
        countdownViewModel.countdowns.value.find { it.config.id == id }
}
