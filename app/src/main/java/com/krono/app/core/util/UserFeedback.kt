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
                KronoToolAudio.STOPWATCH -> ToneGenerator.TONE_PROP_ACK
                KronoToolAudio.COUNTDOWN -> ToneGenerator.TONE_SUP_CONFIRM
                KronoToolAudio.POMODORO -> ToneGenerator.TONE_SUP_PIP
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

fun playPomodoroPhaseBeep(context: Context, isFocusPhase: Boolean, volume: Float, soundType: String) {
    runCatching {
        val tone = ToneGenerator(
            AudioManager.STREAM_ALARM,
            (volume.coerceIn(0f, 1f) * 100).toInt().coerceIn(0, 100)
        )
        val toneType = when (soundType) {
            "FOCUS_B", "BREAK_B" -> ToneGenerator.TONE_PROP_BEEP
            "FOCUS_C", "BREAK_C" -> ToneGenerator.TONE_SUP_PIP
            "FOCUS_D", "BREAK_D" -> ToneGenerator.TONE_SUP_CONFIRM
            else -> if (isFocusPhase) ToneGenerator.TONE_PROP_ACK else ToneGenerator.TONE_SUP_RINGTONE
        }
        tone.startTone(toneType, 240)
        tone.release()
    }
}

fun playPomodoroTick(context: Context, volume: Float, soundType: String) {
    runCatching {
        val tone = ToneGenerator(
            AudioManager.STREAM_MUSIC,
            (volume.coerceIn(0f, 1f) * 100).toInt().coerceIn(0, 100)
        )
        val toneType = when (soundType) {
            "TICK_B" -> ToneGenerator.TONE_PROP_ACK
            "TICK_C" -> ToneGenerator.TONE_SUP_PIP
            "TICK_D" -> ToneGenerator.TONE_SUP_CONFIRM
            else -> ToneGenerator.TONE_PROP_BEEP
        }
        tone.startTone(toneType, 70)
        tone.release()
    }
}
