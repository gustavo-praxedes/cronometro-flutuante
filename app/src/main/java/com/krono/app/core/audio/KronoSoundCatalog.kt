package com.krono.app.core.audio

import androidx.annotation.StringRes
import com.krono.app.R

data class KronoSound(
    val id: String,
    @param:StringRes val labelResId: Int,
    val rawResId: Int
)

object KronoSoundCatalog {
    val playPause: List<KronoSound> = listOf(
        KronoSound("krono_tip_complete", R.string.sound_tip_01, R.raw.krono_tip_complete),
        KronoSound("krono_tip_volumetip", R.string.sound_tip_02, R.raw.krono_tip_volumetip)
    )

    val pomodoroAlerts: List<KronoSound> = listOf(
        KronoSound("krono_alm_alarmbeep", R.string.sound_alarm_01, R.raw.krono_alm_alarmbeep),
        KronoSound("krono_alm_beeps", R.string.sound_alarm_02, R.raw.krono_alm_beeps),
        KronoSound("krono_alm_bell1", R.string.sound_alarm_03, R.raw.krono_alm_bell1),
        KronoSound("krono_alm_bell2", R.string.sound_alarm_04, R.raw.krono_alm_bell2),
        KronoSound("krono_alm_bicyclebell", R.string.sound_alarm_05, R.raw.krono_alm_bicyclebell),
        KronoSound("krono_alm_birdcall", R.string.sound_alarm_06, R.raw.krono_alm_birdcall),
        KronoSound("krono_alm_carhorn", R.string.sound_alarm_07, R.raw.krono_alm_carhorn),
        KronoSound("krono_alm_clownhorn", R.string.sound_alarm_08, R.raw.krono_alm_clownhorn),
        KronoSound("krono_alm_cockcrow", R.string.sound_alarm_09, R.raw.krono_alm_cockcrow),
        KronoSound("krono_alm_cuckoo", R.string.sound_alarm_10, R.raw.krono_alm_cuckoo),
        KronoSound("krono_alm_drumcymbalcrash", R.string.sound_alarm_11, R.raw.krono_alm_drumcymbalcrash),
        KronoSound("krono_alm_fanfare", R.string.sound_alarm_12, R.raw.krono_alm_fanfare),
        KronoSound("krono_alm_music1", R.string.sound_alarm_13, R.raw.krono_alm_music1),
        KronoSound("krono_alm_music2", R.string.sound_alarm_14, R.raw.krono_alm_music2),
        KronoSound("krono_alm_music3", R.string.sound_alarm_15, R.raw.krono_alm_music3),
        KronoSound("krono_alm_partyhorn", R.string.sound_alarm_16, R.raw.krono_alm_partyhorn),
        KronoSound("krono_alm_percussion", R.string.sound_alarm_17, R.raw.krono_alm_percussion),
        KronoSound("krono_alm_pianomusic", R.string.sound_alarm_18, R.raw.krono_alm_pianomusic),
        KronoSound("krono_alm_ringtone", R.string.sound_alarm_19, R.raw.krono_alm_ringtone),
        KronoSound("krono_alm_satellite", R.string.sound_alarm_20, R.raw.krono_alm_satellite),
        KronoSound("krono_alm_schoolbell", R.string.sound_alarm_21, R.raw.krono_alm_schoolbell),
        KronoSound("krono_alm_timer", R.string.sound_alarm_22, R.raw.krono_alm_timer),
        KronoSound("krono_alm_toynoisemakerhonk", R.string.sound_alarm_23, R.raw.krono_alm_toynoisemakerhonk),
        KronoSound("krono_alm_victory", R.string.sound_alarm_24, R.raw.krono_alm_victory),
        KronoSound("krono_alm_whistle", R.string.sound_alarm_25, R.raw.krono_alm_whistle),
        KronoSound("krono_alm_windchimes", R.string.sound_alarm_26, R.raw.krono_alm_windchimes),
        KronoSound("krono_alm_windup", R.string.sound_alarm_27, R.raw.krono_alm_windup)
    )

    val environment: List<KronoSound> = listOf(
        KronoSound("krono_env_brownnoise", R.string.sound_environment_01, R.raw.krono_env_brownnoise),
        KronoSound("krono_env_classroom", R.string.sound_environment_02, R.raw.krono_env_classroom),
        KronoSound("krono_env_cofficeshop", R.string.sound_environment_03, R.raw.krono_env_cofficeshop),
        KronoSound("krono_env_fastticking", R.string.sound_environment_04, R.raw.krono_env_fastticking),
        KronoSound("krono_env_fireburning", R.string.sound_environment_05, R.raw.krono_env_fireburning),
        KronoSound("krono_env_frogs", R.string.sound_environment_06, R.raw.krono_env_frogs),
        KronoSound("krono_env_library", R.string.sound_environment_07, R.raw.krono_env_library),
        KronoSound("krono_env_metronome", R.string.sound_environment_08, R.raw.krono_env_metronome),
        KronoSound("krono_env_oceanshore", R.string.sound_environment_10, R.raw.krono_env_oceanshore),
        KronoSound("krono_env_rain", R.string.sound_environment_11, R.raw.krono_env_rain),
        KronoSound("krono_env_stream", R.string.sound_environment_12, R.raw.krono_env_stream),
        KronoSound("krono_env_ticking", R.string.sound_environment_13, R.raw.krono_env_ticking),
        KronoSound("krono_env_wilderness", R.string.sound_environment_14, R.raw.krono_env_wilderness),
        KronoSound("krono_env_windthroughtrees", R.string.sound_environment_15, R.raw.krono_env_windthroughtrees),
        KronoSound("krono_env_windwithcrickets", R.string.sound_environment_09, R.raw.krono_env_windwithcrickets)
    )

    val notifications: List<KronoSound> = listOf(
        KronoSound("krono_ntf_notification", R.string.sound_notification_01, R.raw.krono_ntf_notification)
    )

    fun playPauseResId(id: String): Int = playPause.firstOrNull { it.id == id }?.rawResId
        ?: playPause.first().rawResId

    fun pomodoroAlertResId(id: String): Int = pomodoroAlerts.firstOrNull { it.id == id }?.rawResId
        ?: pomodoroAlerts.first().rawResId

    fun environmentResId(id: String): Int = environment.firstOrNull { it.id == id }?.rawResId
        ?: environment.first().rawResId

    fun appNotificationResId(id: String): Int = notifications.firstOrNull { it.id == id }?.rawResId
        ?: notifications.first().rawResId

    fun playPauseLabelResId(id: String): Int? = playPause.firstOrNull { it.id == id }?.labelResId
    fun pomodoroAlertLabelResId(id: String): Int? = pomodoroAlerts.firstOrNull { it.id == id }?.labelResId
    fun environmentLabelResId(id: String): Int? = environment.firstOrNull { it.id == id }?.labelResId
    fun appNotificationLabelResId(id: String): Int? = notifications.firstOrNull { it.id == id }?.labelResId

    fun isKnownEnvironment(id: String): Boolean = environment.any { it.id == id }
}
