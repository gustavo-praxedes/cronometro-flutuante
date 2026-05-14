package com.krono.app.feature.countdown

import android.content.Context
import android.view.WindowManager
import com.krono.app.core.service.FeedbackManager
import com.krono.app.core.service.NotificationHelper
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

    // id → running coroutine
    private val activeTimers = mutableMapOf<String, Job>()

    // id → remaining seconds (manager-owned, avoids race with ViewModel ticks)
    private val remainingMap = mutableMapOf<String, Long>()

    // id → overlay instance
    private val activeOverlays = mutableMapOf<String, CountdownOverlayManager>()
    private fun peers(): List<CountdownOverlayManager> = activeOverlays.values.toList()

    // ── Timer ──────────────────────────────────────────────────────────────

    fun play(id: String) {
        if (activeTimers[id]?.isActive == true) return
        val state = vmState(id) ?: return
        if (state.isCompleted) return

        remainingMap.putIfAbsent(id, state.remainingSeconds)

        activeTimers[id] = scope.launch {
            while (true) {
                delay(1_000L)
                val current = remainingMap[id] ?: (vmState(id)?.remainingSeconds ?: 0L)
                val next = (current - 1).coerceAtLeast(0L)
                remainingMap[id] = next
                countdownViewModel.onTick(id, next)
                syncOverlay(id)
                if (next <= 0L) {
                    complete(id)
                    break
                }
            }
        }
    }

    fun pause(id: String) {
        activeTimers[id]?.cancel()
        activeTimers.remove(id)
        syncOverlay(id)
        vmState(id)?.let { if (it.isOverlayVisible) notificationHelper.postCountdownNotification(it) }
    }

    fun reset(id: String) {
        activeTimers[id]?.cancel()
        activeTimers.remove(id)
        val total = vmState(id)?.config?.totalSeconds ?: return
        remainingMap[id] = total
        syncOverlay(id)
        notificationHelper.cancelCountdownNotification(id)
    }

    fun addOneMinute(id: String) {
        val state = vmState(id) ?: return
        val base = remainingMap[id] ?: state.remainingSeconds
        val next = (base + 60L).coerceAtMost(99L * 3600L + 59L * 60L + 59L)
        remainingMap[id] = next
        countdownViewModel.setRemainingSeconds(id, next, clearCompleted = true)
        syncOverlay(id)
        vmState(id)?.let { if (it.isOverlayVisible) notificationHelper.postCountdownNotification(it) }
    }

    // ── Overlay ────────────────────────────────────────────────────────────

    fun showOverlay(id: String) {
        val state = vmState(id) ?: return
        val overlay = activeOverlays.getOrPut(id) {
            CountdownOverlayManager(
                context = context,
                windowManager = windowManager,
                id = id,
                getPeers = ::peers,
                onPlay  = { play(id); countdownViewModel.play(context, id) },
                onPause = { pause(id); countdownViewModel.pause(context, id) },
                onReset = { reset(id); countdownViewModel.reset(context, id) },
                onPlusOne = { addOneMinute(id) },
                onClose = {
                    hideOverlay(id)
                    countdownViewModel.forceHideOverlay(id)
                }
            )
        }
        overlay.show(state)
    }

    fun hideOverlay(id: String) {
        activeOverlays[id]?.hide()
        activeOverlays.remove(id)
        notificationHelper.cancelCountdownNotification(id)
    }

    /**
     * Called when ViewModel state changes externally (e.g. live preview from wheel).
     * Pushes latest state to overlay without touching the timer.
     */
    fun syncOverlay(id: String) {
        val state = vmState(id) ?: return
        activeOverlays[id]?.update(state)
    }

    fun destroy(id: String) {
        activeTimers[id]?.cancel()
        activeTimers.remove(id)
        remainingMap.remove(id)
        hideOverlay(id)
    }

    fun destroyAll() {
        activeTimers.values.forEach { it.cancel() }
        activeTimers.clear()
        remainingMap.clear()
        activeOverlays.values.forEach { it.hide() }
        activeOverlays.clear()
        scope.cancel()
    }

    // ── Internal ───────────────────────────────────────────────────────────

    private fun complete(id: String) {
        activeTimers.remove(id)
        remainingMap[id] = 0L
        countdownViewModel.onCompleted(id)
        feedbackManager.onCountdownCompleted()
        syncOverlay(id)
        vmState(id)?.let { notificationHelper.postCountdownNotification(it) }
    }

    private fun vmState(id: String): CountdownState? =
        countdownViewModel.countdowns.value.find { it.config.id == id }
}
