package com.krono.app.feature.countdown

import android.content.Context
import android.os.SystemClock
import android.view.WindowManager
import com.krono.app.core.service.FeedbackManager
import com.krono.app.core.service.NotificationHelper
import com.krono.app.core.util.PermissionUtils
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
    private val countdownViewModel: CountdownViewModel,
    private val currentConfigProvider: () -> com.krono.app.core.data.OverlayConfig
) {
    private val scope = CoroutineScope(SupervisorJob())

    private val activeTimers = mutableMapOf<String, Job>()
    private val remainingMsMap = mutableMapOf<String, Long>()
    private val activeOverlays = mutableMapOf<String, CountdownOverlayManager>()

    private fun peers(): List<CountdownOverlayManager> = activeOverlays.values.toList()

    fun play(id: String) {
        if (activeTimers[id]?.isActive == true) return
        val state = vmState(id) ?: return
        if (state.isCompleted) return

        remainingMsMap[id] = state.remainingMs

        activeTimers[id] = scope.launch {
            val deadlineElapsedMs = SystemClock.elapsedRealtime() + state.remainingMs.coerceAtLeast(0L)
            var lastReportedSecond = state.remainingSeconds

            while (true) {
                val currentRemainingMs = (deadlineElapsedMs - SystemClock.elapsedRealtime()).coerceAtLeast(0L)
                val currentRemainingSeconds = remainingSecondsFromMs(currentRemainingMs)

                if (currentRemainingSeconds < lastReportedSecond) {
                    if (lastReportedSecond > 0L) {
                        feedbackManager.triggerSecondTick(currentConfigProvider())
                    }
                    lastReportedSecond = currentRemainingSeconds
                }

                remainingMsMap[id] = currentRemainingMs
                countdownViewModel.onTick(id, currentRemainingMs)
                syncOverlay(id)

                if (currentRemainingMs <= 0L) {
                    complete(id)
                    break
                }

                val delayToNextSecond = delayUntilNextSecondBoundary(currentRemainingMs)
                val delayToNextFrame = if (currentConfigProvider().showMilliseconds) 50L else delayToNextSecond
                delay(minOf(delayToNextSecond, delayToNextFrame).coerceAtLeast(1L))
            }
        }
    }

    fun pause(id: String) {
        activeTimers[id]?.cancel()
        activeTimers.remove(id)
        feedbackManager.stopTimerSounds("countdown pause")
        syncOverlay(id)
        vmState(id)?.let { if (it.isOverlayVisible) notificationHelper.postCountdownNotification(it) }
    }

    fun reset(id: String) {
        activeTimers[id]?.cancel()
        activeTimers.remove(id)
        feedbackManager.stopTimerSounds("countdown reset")
        val total = vmState(id)?.config?.totalSeconds ?: return
        remainingMsMap[id] = total * 1000L
        syncOverlay(id)
        notificationHelper.cancelCountdownNotification(id)
    }

    fun addOneMinute(id: String) {
        val state = vmState(id) ?: return
        val base = remainingMsMap[id] ?: state.remainingMs
        val next = (base + 60_000L).coerceAtMost((99L * 3600L + 59L * 60L + 59L) * 1000L)
        remainingMsMap[id] = next
        countdownViewModel.setRemainingMs(id, next, clearCompleted = true)
        syncOverlay(id)
        vmState(id)?.let { if (it.isOverlayVisible) notificationHelper.postCountdownNotification(it) }
    }

    fun setRemaining(id: String, seconds: Long) {
        val next = seconds.coerceAtLeast(0L) * 1000L
        remainingMsMap[id] = next
        countdownViewModel.setRemainingSeconds(id, seconds, clearCompleted = true)
        syncOverlay(id)
        vmState(id)?.let { if (it.isOverlayVisible) notificationHelper.postCountdownNotification(it) }
    }

    fun showOverlay(id: String) {
        if (!PermissionUtils.hasEssentialPermissions(context)) {
            PermissionUtils.requestEssentialPermissions(context)
            countdownViewModel.forceHideOverlay(id)
            return
        }
        val state = vmState(id) ?: return
        val overlay = activeOverlays.getOrPut(id) {
            CountdownOverlayManager(
                context = context,
                windowManager = windowManager,
                id = id,
                getPeers = ::peers,
                onPlay = {
                    feedbackManager.triggerFeedback(currentConfigProvider())
                    play(id)
                    countdownViewModel.play(context, id, feedbackAlreadyHandled = true)
                },
                onPause = {
                    pause(id)
                    countdownViewModel.pause(context, id, feedbackAlreadyHandled = true)
                    feedbackManager.triggerFeedback(currentConfigProvider())
                },
                onReset = { reset(id); countdownViewModel.reset(context, id) },
                onPlusOne = { addOneMinute(id) },
                onClose = {
                    hideOverlay(id)
                    countdownViewModel.forceHideOverlay(id)
                }
            )
        }
        overlay.show(state)
        overlay.applyKeepScreenOn(currentConfigProvider().keepScreenOn)
    }

    fun hideOverlay(id: String) {
        activeOverlays[id]?.hide()
        activeOverlays.remove(id)
        notificationHelper.cancelCountdownNotification(id)
    }

    fun applyKeepScreenOn(enable: Boolean) {
        activeOverlays.values.forEach { it.applyKeepScreenOn(enable) }
    }

    fun hasRunningTimer(): Boolean =
        countdownViewModel.countdowns.value.any { it.isRunning }

    fun hasVisibleOverlay(): Boolean =
        activeOverlays.isNotEmpty()

    fun syncOverlay(id: String) {
        val state = vmState(id) ?: return
        activeOverlays[id]?.update(state)
    }

    fun destroy(id: String) {
        activeTimers[id]?.cancel()
        activeTimers.remove(id)
        feedbackManager.stopTimerSounds("countdown destroy")
        remainingMsMap.remove(id)
        hideOverlay(id)
    }

    fun destroyAll() {
        activeTimers.values.forEach { it.cancel() }
        activeTimers.clear()
        feedbackManager.stopTimerSounds("countdown destroy all")
        remainingMsMap.clear()
        activeOverlays.values.forEach { it.hide() }
        activeOverlays.clear()
        scope.cancel()
    }

    private fun complete(id: String) {
        activeTimers.remove(id)
        feedbackManager.stopTimerSounds("countdown complete")
        val resetToConfigured = id == CountdownViewModel.SCREEN_OVERLAY_ID
        val finalRemaining = if (resetToConfigured) {
            vmState(id)?.config?.totalSeconds ?: 0L
        } else {
            0L
        }
        remainingMsMap[id] = finalRemaining * 1000L
        countdownViewModel.onCompleted(id, finalRemaining)
        feedbackManager.onCountdownCompleted(currentConfigProvider())
        syncOverlay(id)
        vmState(id)?.let { notificationHelper.postCountdownNotification(it) }
    }

    private fun vmState(id: String): CountdownState? =
        countdownViewModel.countdowns.value.find { it.config.id == id }

    private fun remainingSecondsFromMs(ms: Long): Long =
        ((ms.coerceAtLeast(0L) + 999L) / 1000L)

    private fun delayUntilNextSecondBoundary(remainingMs: Long): Long {
        val safeRemainingMs = remainingMs.coerceAtLeast(0L)
        if (safeRemainingMs <= 0L) return 1L
        val remainder = safeRemainingMs % 1000L
        return if (remainder == 0L) 1000L else remainder
    }
}
