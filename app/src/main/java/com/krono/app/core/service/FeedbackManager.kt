package com.krono.app.core.service

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.krono.app.core.data.OverlayConfig

class FeedbackManager(context: Context) {

    private var toneGenerator: ToneGenerator? = try {
        ToneGenerator(AudioManager.STREAM_ALARM, 80)
    } catch (_: Exception) {
        null
    }

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager)
            .defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    fun triggerFeedback(config: OverlayConfig) {
        if (config.playPauseSoundEnabled) {
            val volume = (config.playPauseVolume.coerceIn(0f, 1f) * 100).toInt().coerceIn(0, 100)
            toneGenerator?.release()
            toneGenerator = try { ToneGenerator(AudioManager.STREAM_ALARM, volume) } catch (_: Exception) { null }
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
        }
        if (config.playPauseVibrationEnabled) {
            vibrator?.vibrate(
                VibrationEffect.createOneShot(50L, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        }
    }

    fun onCountdownCompleted() {
        // Vibração longa (padrão: 3 pulsos)
        val pattern = longArrayOf(0, 200, 100, 200, 100, 400)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(pattern, -1)
        }
        // Beep de conclusão (tom mais alto que o beep padrão)
        beep(frequency = 880, durationMs = 600)
    }

    private fun beep(frequency: Int, durationMs: Int) {
        // ToneGenerator.TONE_PROP_BEEP é fixo. Para frequências customizadas, seria necessário algo mais complexo.
        // Mantendo simples conforme o patch sugerido, mas usando o ToneGenerator existente.
        toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, durationMs)
    }

    fun release() {
        toneGenerator?.release()
        toneGenerator = null
    }
}

