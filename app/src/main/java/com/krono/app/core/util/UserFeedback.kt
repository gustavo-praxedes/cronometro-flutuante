package com.krono.app.core.util

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

enum class KronoToolAudio {
    STOPWATCH, COUNTDOWN, POMODORO
}

fun triggerPlayPauseFeedback(
    context: Context,
    beepEnabled: Boolean,
    vibrationEnabled: Boolean,
    volume: Float,
    tool: KronoToolAudio
) {
    if (beepEnabled) {
        runCatching {
            val tone = ToneGenerator(
                AudioManager.STREAM_NOTIFICATION,
                (volume.coerceIn(0f, 1f) * 100).toInt().coerceIn(0, 100)
            )
            val toneType = when (tool) {
                KronoToolAudio.STOPWATCH -> ToneGenerator.TONE_PROP_BEEP
                KronoToolAudio.COUNTDOWN -> ToneGenerator.TONE_PROP_BEEP2
                KronoToolAudio.POMODORO -> ToneGenerator.TONE_CDMA_ONE_MIN_BEEP
            }
            tone.startTone(toneType, 120)
            tone.release()
        }
    }
    if (vibrationEnabled) {
        val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        vibrator?.vibrate(VibrationEffect.createOneShot(50L, VibrationEffect.DEFAULT_AMPLITUDE))
    }
}

fun playPomodoroPhaseBeep(context: Context, isFocusPhase: Boolean, volume: Float) {
    runCatching {
        val tone = ToneGenerator(
            AudioManager.STREAM_ALARM,
            (volume.coerceIn(0f, 1f) * 100).toInt().coerceIn(0, 100)
        )
        val toneType = if (isFocusPhase) {
            ToneGenerator.TONE_CDMA_ABBR_ALERT
        } else {
            ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD
        }
        tone.startTone(toneType, 220)
        tone.release()
    }
}

fun playPomodoroTick(context: Context, volume: Float) {
    runCatching {
        val tone = ToneGenerator(
            AudioManager.STREAM_MUSIC,
            (volume.coerceIn(0f, 1f) * 100).toInt().coerceIn(0, 100)
        )
        tone.startTone(ToneGenerator.TONE_PROP_BEEP2, 65)
        tone.release()
    }
}
