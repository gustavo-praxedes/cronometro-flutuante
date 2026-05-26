package com.krono.app.core.util

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import com.krono.app.core.audio.EnvironmentSoundLoop
import com.krono.app.core.audio.KronoSoundCatalog
import com.krono.app.core.audio.SoundPlaybackMode
import com.krono.app.core.audio.SoundTimingPolicy
import com.krono.app.core.audio.SoundPreviewPlayer
import com.krono.app.core.audio.KronoSoundPool

@Volatile private var lastSecondVibrationAtMs: Long = 0L
@Volatile private var activeEnvironmentLoopSoundType: String = SOUND_NONE
private val feedbackHandler = Handler(Looper.getMainLooper())
private val activeSecondTickLock = Any()
private val activeSecondTickStreams = mutableSetOf<Int>()
private val secondTickStopCallbacks = mutableMapOf<Int, Runnable>()
private const val AUDIO_DELAY_NONE_MS = 0L
private const val PLAY_PAUSE_VIBRATION_MS = 80L
private const val SECOND_VIBRATION_MS = 45L
private const val VIBRATION_AMPLITUDE = 200
const val SOUND_NONE = "NONE"

data class NotificationSoundOption(
    val label: String,
    val uriString: String
)

fun triggerPlayPauseFeedback(
    context: Context,
    beepEnabled: Boolean,
    vibrationEnabled: Boolean,
    volume: Float,
    soundType: String,
    startDelayMs: Long = AUDIO_DELAY_NONE_MS,
    maxLifetimeMs: Long = 0L
) {
    if (beepEnabled && soundType != SOUND_NONE) {
        playPlayPauseBeep(context, volume, soundType, startDelayMs, maxLifetimeMs)
    }
    if (vibrationEnabled) {
        vibrateOnce(context, PLAY_PAUSE_VIBRATION_MS)
    }
}

fun triggerSecondVibration(context: Context, enabled: Boolean) {
    if (!enabled) return
    val now = SystemClock.elapsedRealtime()
    if (now - lastSecondVibrationAtMs < 700L) return
    lastSecondVibrationAtMs = now
    vibrateOnce(context, SECOND_VIBRATION_MS)
}

fun triggerSecondFeedback(
    context: Context,
    vibrationEnabled: Boolean,
    tickSoundEnabled: Boolean,
    tickVolume: Float,
    environmentSoundType: String = "krono_env_brownnoise",
    startDelayMs: Long = AUDIO_DELAY_NONE_MS,
    staleAfterMs: Long = 1_500L
) {
    triggerSecondVibration(context, vibrationEnabled)
    if (!tickSoundEnabled || environmentSoundType == SOUND_NONE) {
        stopActiveTimerSounds("disabled")
        return
    }
    val profile = SoundTimingPolicy.profile(environmentSoundType)

    if (profile.playbackMode == SoundPlaybackMode.SecondTick) {
        if (activeEnvironmentLoopSoundType != SOUND_NONE) {
            EnvironmentSoundLoop.stop("second tick")
            activeEnvironmentLoopSoundType = SOUND_NONE
        }

        // Use SoundPool for low latency ticks
        val resId = KronoSoundCatalog.environmentResId(environmentSoundType)
        val streamId = KronoSoundPool.play(context, resId, tickVolume)
        if (streamId > 0 && profile.maxDurationMs > 0L) {
            val stopCallback = Runnable {
                synchronized(activeSecondTickLock) {
                    activeSecondTickStreams.remove(streamId)
                    secondTickStopCallbacks.remove(streamId)
                }
                KronoSoundPool.stop(streamId)
            }
            synchronized(activeSecondTickLock) {
                activeSecondTickStreams.add(streamId)
                secondTickStopCallbacks[streamId] = stopCallback
            }
            feedbackHandler.postDelayed(stopCallback, profile.maxDurationMs)
        }
        return
    }

    activeEnvironmentLoopSoundType = environmentSoundType
    EnvironmentSoundLoop.heartbeat(
        context = context,
        resId = KronoSoundCatalog.environmentResId(environmentSoundType),
        volume = tickVolume,
        usage = AudioAttributes.USAGE_MEDIA,
        startDelayMs = startDelayMs,
        staleAfterMs = staleAfterMs,
        startOffsetMs = profile.startOffsetMs,
        endTrimMs = profile.endTrimMs,
        crossfadeMs = profile.crossfadeMs,
        nativeLoop = profile.nativeLoop
    )
}

fun triggerCompletionVibration(context: Context) {
    val vibrator = context.kronoVibrator() ?: run {
        Log.w("UserFeedback", "Vibrator service unavailable")
        return
    }
    if (!vibrator.hasVibrator()) {
        Log.w("UserFeedback", "Device reports no vibrator")
        return
    }
    val pattern = longArrayOf(0L, 200L, 100L, 200L, 100L, 400L)
    val amplitudes = intArrayOf(0, VIBRATION_AMPLITUDE, 0, VIBRATION_AMPLITUDE, 0, 255)
    runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, -1)
        }
    }.onFailure { error ->
        Log.e("UserFeedback", "Unable to play completion vibration", error)
    }
}

fun playPlayPauseBeep(
    context: Context,
    volume: Float,
    soundType: String,
    @Suppress("UNUSED_PARAMETER") startDelayMs: Long = AUDIO_DELAY_NONE_MS,
    @Suppress("UNUSED_PARAMETER") maxLifetimeMs: Long = 0L
) {
    if (soundType == SOUND_NONE) return
    val resId = KronoSoundCatalog.playPauseResId(soundType)
    KronoSoundPool.play(context, resId, volume)
}

fun previewPlayPauseSound(
    context: Context,
    volume: Float,
    soundType: String,
    startDelayMs: Long = AUDIO_DELAY_NONE_MS
) {
    if (soundType == SOUND_NONE) return
    SoundPreviewPlayer.play(
        context = context,
        resId = KronoSoundCatalog.playPauseResId(soundType),
        volume = volume,
        usage = AudioAttributes.USAGE_MEDIA,
        startDelayMs = startDelayMs
    )
}

fun playTickSound(context: Context, volume: Float) {
    playEnvironmentSound(context, volume, "krono_env_brownnoise")
}

fun playEnvironmentSound(
    context: Context,
    volume: Float,
    soundType: String,
    @Suppress("UNUSED_PARAMETER") startDelayMs: Long = AUDIO_DELAY_NONE_MS,
    @Suppress("UNUSED_PARAMETER") maxLifetimeMs: Long = 0L
) {
    if (soundType == SOUND_NONE) return
    val resId = KronoSoundCatalog.environmentResId(soundType)
    KronoSoundPool.play(context, resId, volume)
}

fun previewEnvironmentSound(
    context: Context,
    volume: Float,
    soundType: String,
    startDelayMs: Long = AUDIO_DELAY_NONE_MS
) {
    if (soundType == SOUND_NONE) return
    val profile = SoundTimingPolicy.profile(soundType)
    SoundPreviewPlayer.play(
        context = context,
        resId = KronoSoundCatalog.environmentResId(soundType),
        volume = volume,
        usage = AudioAttributes.USAGE_MEDIA,
        startDelayMs = startDelayMs,
        startOffsetMs = profile.startOffsetMs,
        previewMaxMs = profile.previewMaxMs
    )
}

fun playAppNotificationSound(
    context: Context,
    volume: Float,
    soundType: String,
    startDelayMs: Long = AUDIO_DELAY_NONE_MS,
    maxLifetimeMs: Long = 0L
) {
    if (soundType == SOUND_NONE) return
    playBundledSound(
        context = context,
        resId = KronoSoundCatalog.appNotificationResId(soundType),
        volume = volume,
        usage = AudioAttributes.USAGE_NOTIFICATION,
        maxLifetimeMs = maxLifetimeMs,
        startDelayMs = startDelayMs
    )
}

fun previewAppNotificationSound(
    context: Context,
    volume: Float,
    soundType: String,
    startDelayMs: Long = AUDIO_DELAY_NONE_MS
) {
    if (soundType == SOUND_NONE) return
    SoundPreviewPlayer.play(
        context = context,
        resId = KronoSoundCatalog.appNotificationResId(soundType),
        volume = volume,
        usage = AudioAttributes.USAGE_NOTIFICATION,
        startDelayMs = startDelayMs
    )
}

fun playPomodoroPhaseBeep(
    context: Context,
    @Suppress("UNUSED_PARAMETER") isFocusPhase: Boolean,
    volume: Float,
    soundType: String,
    startDelayMs: Long = AUDIO_DELAY_NONE_MS,
    maxLifetimeMs: Long = 0L
) {
    playPomodoroNotificationSound(context, volume, normalizeNotificationSound(soundType), startDelayMs, maxLifetimeMs)
}

fun playPomodoroNotificationSound(
    context: Context,
    volume: Float,
    soundType: String,
    startDelayMs: Long = AUDIO_DELAY_NONE_MS,
    maxLifetimeMs: Long = 0L
) {
    if (soundType == SOUND_NONE) return
    playBundledSound(
        context = context,
        resId = pomodoroAlertSoundResId(soundType),
        volume = volume,
        usage = AudioAttributes.USAGE_ALARM,
        maxLifetimeMs = maxLifetimeMs,
        startDelayMs = startDelayMs
    )
}

fun previewPomodoroNotificationSound(
    context: Context,
    volume: Float,
    soundType: String,
    startDelayMs: Long = AUDIO_DELAY_NONE_MS
) {
    if (soundType == SOUND_NONE) return
    SoundPreviewPlayer.play(
        context = context,
        resId = pomodoroAlertSoundResId(soundType),
        volume = volume,
        usage = AudioAttributes.USAGE_ALARM,
        startDelayMs = startDelayMs
    )
}

fun stopSoundPreview() {
    SoundPreviewPlayer.stop("preview dismissed")
}

fun stopActiveTimerSounds(reason: String = "timer stopped") {
    activeEnvironmentLoopSoundType = SOUND_NONE
    EnvironmentSoundLoop.stop(reason)
    val (streams, callbacks) = synchronized(activeSecondTickLock) {
        val pendingStreams = activeSecondTickStreams.toList()
        val pendingCallbacks = secondTickStopCallbacks.values.toList()
        activeSecondTickStreams.clear()
        secondTickStopCallbacks.clear()
        pendingStreams to pendingCallbacks
    }
    callbacks.forEach(feedbackHandler::removeCallbacks)
    streams.forEach(KronoSoundPool::stop)
}

fun loadNotificationSoundOptions(@Suppress("UNUSED_PARAMETER") context: Context): List<NotificationSoundOption> {
    return bundledPomodoroAlertOptions()
}

fun normalizeNotificationSound(soundType: String): String = when {
    soundType.isBlank() -> "krono_alm_alarmbeep"
    soundType == SOUND_NONE -> SOUND_NONE
    KronoSoundCatalog.pomodoroAlerts.any { it.id == soundType } -> soundType
    else -> "krono_alm_alarmbeep"
}

fun bundledPomodoroAlertOptions(): List<NotificationSoundOption> = listOf(
    NotificationSoundOption(label = SOUND_NONE, uriString = SOUND_NONE)
) + KronoSoundCatalog.pomodoroAlerts.map { sound ->
    NotificationSoundOption(label = sound.id, uriString = sound.id)
}

fun playPauseSoundOptions(): List<NotificationSoundOption> =
    listOf(NotificationSoundOption(label = SOUND_NONE, uriString = SOUND_NONE)) +
    KronoSoundCatalog.playPause.map { sound ->
        NotificationSoundOption(label = sound.id, uriString = sound.id)
    }

fun environmentSoundOptions(): List<NotificationSoundOption> =
    listOf(NotificationSoundOption(label = SOUND_NONE, uriString = SOUND_NONE)) +
    KronoSoundCatalog.environment.map { sound ->
        NotificationSoundOption(label = sound.id, uriString = sound.id)
    }

fun appNotificationSoundOptions(): List<NotificationSoundOption> =
    listOf(NotificationSoundOption(label = SOUND_NONE, uriString = SOUND_NONE)) +
    KronoSoundCatalog.notifications.map { sound ->
        NotificationSoundOption(label = sound.id, uriString = sound.id)
    }

private fun pomodoroAlertSoundResId(soundType: String): Int = when (normalizeNotificationSound(soundType)) {
    SOUND_NONE -> KronoSoundCatalog.pomodoroAlertResId("krono_alm_alarmbeep")
    else -> KronoSoundCatalog.pomodoroAlertResId(soundType)
}

private fun playPauseSoundResId(soundType: String): Int = KronoSoundCatalog.playPauseResId(soundType)

private fun playBundledSound(
    context: Context,
    resId: Int,
    volume: Float,
    @Suppress("UNUSED_PARAMETER") usage: Int,
    @Suppress("UNUSED_PARAMETER") maxLifetimeMs: Long,
    startDelayMs: Long,
    @Suppress("UNUSED_PARAMETER") startOffsetMs: Long = 0L,
    @Suppress("UNUSED_PARAMETER") endTrimMs: Long = 0L
) {
    val safeVolume = volume.coerceIn(0f, 1f)
    if (safeVolume <= 0f) return
    val play = {
        KronoSoundPool.play(context.applicationContext, resId, safeVolume)
    }
    if (startDelayMs > 0L) {
        Handler(Looper.getMainLooper()).postDelayed({ play() }, startDelayMs)
    } else {
        play()
    }
}

private fun vibrateOnce(context: Context, durationMs: Long) {
    val vibrator = context.kronoVibrator() ?: run {
        Log.w("UserFeedback", "Vibrator service unavailable")
        return
    }
    if (!vibrator.hasVibrator()) {
        Log.w("UserFeedback", "Device reports no vibrator")
        return
    }
    runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createOneShot(durationMs, VIBRATION_AMPLITUDE)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(durationMs)
        }
    }.onFailure { error ->
        Log.e("UserFeedback", "Unable to vibrate", error)
    }
}

private fun Context.kronoVibrator(): Vibrator? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        (getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }
