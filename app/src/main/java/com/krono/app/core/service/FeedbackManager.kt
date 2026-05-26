package com.krono.app.core.service

import android.content.Context
import com.krono.app.core.audio.SoundTimingPolicy
import com.krono.app.core.data.OverlayConfig
import com.krono.app.core.util.playPomodoroNotificationSound
import com.krono.app.core.util.stopActiveTimerSounds
import com.krono.app.core.util.triggerCompletionVibration
import com.krono.app.core.util.triggerSecondFeedback
import com.krono.app.core.util.triggerPlayPauseFeedback

class FeedbackManager(private val context: Context) {

    fun triggerFeedback(config: OverlayConfig) {
        triggerPlayPauseFeedback(
            context = context,
            beepEnabled = config.allSoundsEnabled && config.playPauseSoundEnabled,
            vibrationEnabled = config.playPauseVibrationEnabled,
            volume = config.playPauseVolume,
            soundType = config.playPauseSoundType,
            startDelayMs = SoundTimingPolicy.profile(config.playPauseSoundType).startDelayMs,
            maxLifetimeMs = SoundTimingPolicy.profile(config.playPauseSoundType).maxLifetimeMs
        )
    }

    fun onCountdownCompleted(config: OverlayConfig) {
        triggerCompletionVibration(context)
        if (config.allSoundsEnabled) {
            playPomodoroNotificationSound(
                context = context,
                volume = 0.9f,
                soundType = "krono_alm_bell2",
                startDelayMs = SoundTimingPolicy.profile("krono_alm_bell2").startDelayMs,
                maxLifetimeMs = SoundTimingPolicy.profile("krono_alm_bell2").maxLifetimeMs
            )
        }
    }

    fun triggerSecondTick(config: OverlayConfig) {
        triggerSecondFeedback(
            context = context,
            vibrationEnabled = config.secondsVibrationEnabled,
            tickSoundEnabled = config.allSoundsEnabled && config.tickSoundEnabled,
            tickVolume = config.tickVolume,
            environmentSoundType = config.environmentSoundType,
            startDelayMs = SoundTimingPolicy.profile(config.environmentSoundType).startDelayMs,
            staleAfterMs = SoundTimingPolicy.profile(config.environmentSoundType).staleAfterMs
        )
    }

    fun stopTimerSounds(reason: String = "timer stopped") {
        stopActiveTimerSounds(reason)
    }

    fun release() {
        stopTimerSounds("service destroy")
    }
}

