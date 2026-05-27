package com.krono.app.core.audio

import com.krono.app.core.util.SOUND_NONE

enum class SoundPlaybackMode {
    AmbientLoop,
    SecondTick
}

data class SoundTimingProfile(
    val playbackMode: SoundPlaybackMode = SoundPlaybackMode.AmbientLoop,
    val startOffsetMs: Long = 0L,
    val endTrimMs: Long = 0L,
    val crossfadeMs: Long = 5_000L,
    val nativeLoop: Boolean = false,
    val staleAfterMs: Long = 1_500L,
    val maxDurationMs: Long = 950L,
    val alignmentOffsetMs: Long = 0L,
    val previewMaxMs: Long = 5_000L,
    val startDelayMs: Long = 0L,
    val maxLifetimeMs: Long = 0L,
)

object SoundTimingPolicy {
    private val DEFAULT = SoundTimingProfile()

    private val AMBIENT_DEFAULT = SoundTimingProfile(
        playbackMode = SoundPlaybackMode.AmbientLoop,
        crossfadeMs = 5_000L,
        staleAfterMs = 1_500L,
        previewMaxMs = 5_000L
    )

    private val SECOND_TICK_DEFAULT = SoundTimingProfile(
        playbackMode = SoundPlaybackMode.SecondTick,
        maxDurationMs = 950L,
        previewMaxMs = 3_000L
    )

    private val PROFILES = mapOf(
        "krono_env_fastticking" to SECOND_TICK_DEFAULT.copy(startOffsetMs = 105L, maxDurationMs = 900L, alignmentOffsetMs = 0L),
        "krono_env_metronome" to SECOND_TICK_DEFAULT.copy(startOffsetMs = 140L, maxDurationMs = 850L, alignmentOffsetMs = 0L),
        "krono_env_ticking" to SECOND_TICK_DEFAULT.copy(startOffsetMs = 535L, maxDurationMs = 850L, alignmentOffsetMs = 0L),
    )

    fun profile(soundType: String): SoundTimingProfile = when {
        soundType.isBlank() || soundType == SOUND_NONE -> DEFAULT
        soundType in PROFILES -> PROFILES[soundType]!!
        KronoSoundCatalog.isKnownEnvironment(soundType) -> AMBIENT_DEFAULT
        else -> DEFAULT
    }

    fun staleAfterMs(soundType: String): Long = profile(soundType).staleAfterMs
}
