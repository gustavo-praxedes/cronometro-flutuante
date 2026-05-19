package com.krono.app.core.data

import android.content.Context
import android.graphics.Color as AndroidColor
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore by preferencesDataStore(name = "overlay_settings")

class OverlayDataStore(private val context: Context) {

    private companion object Keys {
        val BACKGROUND_COLOR   = intPreferencesKey("background_color")
        val TEXT_COLOR         = intPreferencesKey("text_color")
        val BG_OPACITY         = floatPreferencesKey("bg_opacity")
        val TEXT_OPACITY       = floatPreferencesKey("text_opacity")
        val SCALE              = floatPreferencesKey("scale")
        val CORNER_RADIUS      = floatPreferencesKey("corner_radius")
        val SHOW_HOURS         = booleanPreferencesKey("show_hours")
        val SHOW_SECONDS       = booleanPreferencesKey("show_seconds")
        val SHOW_BUTTONS       = booleanPreferencesKey("show_buttons")
        val KEEP_SCREEN_ON     = booleanPreferencesKey("keep_screen_on")
        val AUTO_LAUNCH        = booleanPreferencesKey("auto_launch")
        val TIME_LIMIT_SECONDS = longPreferencesKey("time_limit_seconds")
        val BEEP_ENABLED       = booleanPreferencesKey("beep_enabled")
        val VIBRATION_ENABLED  = booleanPreferencesKey("vibration_enabled")
        val LAST_X             = intPreferencesKey("last_x")
        val LAST_Y             = intPreferencesKey("last_y")
        val TOTAL_LIFETIME_MS  = longPreferencesKey("total_lifetime_ms")
        val CURRENT_CYCLE_MS   = longPreferencesKey("current_cycle_ms")
        val LAST_UPDATE_CHECK  = longPreferencesKey("last_update_check")
        val FOCUS_MODE_ENABLED = booleanPreferencesKey("focus_mode_enabled")
        val SELECTED_THEME     = stringPreferencesKey("selected_theme")
        val APP_LANGUAGE       = stringPreferencesKey("app_language")
        val SELECTED_FONT      = stringPreferencesKey("selected_font")
        val DONATION_PENDING   = booleanPreferencesKey("donation_pending")
        val ACTIVE_TOOL_ID     = stringPreferencesKey("active_tool_id")
        val APP_FONT_SIZE      = stringPreferencesKey("app_font_size")
        val STOPWATCH_FORMAT   = stringPreferencesKey("stopwatch_format")
        val COUNTDOWN_FORMAT   = stringPreferencesKey("countdown_format")
        val COUNTDOWN_SCREEN_BASE_SECONDS = longPreferencesKey("countdown_screen_base_seconds")
        val POMODORO_FORMAT    = stringPreferencesKey("pomodoro_format")
        val POMODORO_BEEP_FOCUS_BREAK = booleanPreferencesKey("pomodoro_beep_focus_break")
        val POMODORO_TICKING_SOUND = booleanPreferencesKey("pomodoro_ticking_sound")
        val POMODORO_FOCUS_ALERT_ENABLED = booleanPreferencesKey("pomodoro_focus_alert_enabled")
        val POMODORO_BREAK_ALERT_ENABLED = booleanPreferencesKey("pomodoro_break_alert_enabled")
        val POMODORO_AUTO_START_BREAK = booleanPreferencesKey("pomodoro_auto_start_break")
        val POMODORO_AUTO_START_FOCUS = booleanPreferencesKey("pomodoro_auto_start_focus")
        val POMODORO_AUTO_NEXT_CYCLE = booleanPreferencesKey("pomodoro_auto_next_cycle")
        val POMODORO_PRESET = stringPreferencesKey("pomodoro_preset")
        val POMODORO_PRESETS_SPEC = stringPreferencesKey("pomodoro_presets_spec")
        val POMODORO_CUSTOM_PRESET_NAME = stringPreferencesKey("pomodoro_custom_preset_name")
        val POMODORO_CUSTOM_PHASES_SPEC = stringPreferencesKey("pomodoro_custom_phases_spec")
        val POMODORO_CUSTOM_FOCUS_MINUTES = intPreferencesKey("pomodoro_custom_focus_minutes")
        val POMODORO_CUSTOM_BREAK_MINUTES = intPreferencesKey("pomodoro_custom_break_minutes")
        val POMODORO_CUSTOM_CYCLES = intPreferencesKey("pomodoro_custom_cycles")
        val POMODORO_TICK_SOUND_TYPE = stringPreferencesKey("pomodoro_tick_sound_type")
        val POMODORO_FOCUS_ALERT_SOUND_TYPE = stringPreferencesKey("pomodoro_focus_alert_sound_type")
        val POMODORO_BREAK_ALERT_SOUND_TYPE = stringPreferencesKey("pomodoro_break_alert_sound_type")
        val OVERLAY_FONT_FAMILY = stringPreferencesKey("overlay_font_family")
        val PLAY_PAUSE_SOUND_ENABLED = booleanPreferencesKey("play_pause_sound_enabled")
        val PLAY_PAUSE_VIBRATION_ENABLED = booleanPreferencesKey("play_pause_vibration_enabled")
        val PLAY_PAUSE_VOLUME = floatPreferencesKey("play_pause_volume")
        val TICK_VOLUME = floatPreferencesKey("tick_volume")
        val FOCUS_ALERT_VOLUME = floatPreferencesKey("focus_alert_volume")
        val BREAK_ALERT_VOLUME = floatPreferencesKey("break_alert_volume")
        val SW_OVERLAY_SHOW_BUTTONS = booleanPreferencesKey("sw_overlay_show_buttons")
        val SW_OVERLAY_SHOW_HOURS = booleanPreferencesKey("sw_overlay_show_hours")
        val SW_OVERLAY_SHOW_SECONDS = booleanPreferencesKey("sw_overlay_show_seconds")
        val SW_OVERLAY_SCALE = floatPreferencesKey("sw_overlay_scale")
        val SW_OVERLAY_CORNER_RADIUS = floatPreferencesKey("sw_overlay_corner_radius")
        val SW_OVERLAY_CUSTOM_COLOR = intPreferencesKey("sw_overlay_custom_color")
        val SW_OVERLAY_CUSTOM_TEXT_COLOR = intPreferencesKey("sw_overlay_custom_text_color")
        val CD_OVERLAY_SHOW_BUTTONS = booleanPreferencesKey("cd_overlay_show_buttons")
        val CD_OVERLAY_SHOW_HOURS = booleanPreferencesKey("cd_overlay_show_hours")
        val CD_OVERLAY_SHOW_SECONDS = booleanPreferencesKey("cd_overlay_show_seconds")
        val CD_OVERLAY_SCALE = floatPreferencesKey("cd_overlay_scale")
        val CD_OVERLAY_CORNER_RADIUS = floatPreferencesKey("cd_overlay_corner_radius")
        val CD_OVERLAY_CUSTOM_COLOR = intPreferencesKey("cd_overlay_custom_color")
        val CD_OVERLAY_CUSTOM_TEXT_COLOR = intPreferencesKey("cd_overlay_custom_text_color")
        val PO_OVERLAY_SHOW_BUTTONS = booleanPreferencesKey("po_overlay_show_buttons")
        val PO_OVERLAY_SHOW_HOURS = booleanPreferencesKey("po_overlay_show_hours")
        val PO_OVERLAY_SHOW_SECONDS = booleanPreferencesKey("po_overlay_show_seconds")
        val PO_OVERLAY_SCALE = floatPreferencesKey("po_overlay_scale")
        val PO_OVERLAY_CORNER_RADIUS = floatPreferencesKey("po_overlay_corner_radius")
        val PO_OVERLAY_CUSTOM_COLOR = intPreferencesKey("po_overlay_custom_color")
        val PO_OVERLAY_CUSTOM_TEXT_COLOR = intPreferencesKey("po_overlay_custom_text_color")
    }

    val configFlow: Flow<OverlayConfig> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException || exception is ClassCastException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            OverlayConfig(
                backgroundColor    = prefs[BACKGROUND_COLOR]   ?: AndroidColor.WHITE,
                textColor          = prefs[TEXT_COLOR]         ?: AndroidColor.BLACK,
                bgOpacity          = prefs[BG_OPACITY]         ?: 1.0f,
                textOpacity        = prefs[TEXT_OPACITY]       ?: 1.0f,
                scale              = prefs[SCALE]              ?: 1.0f,
                cornerRadius       = prefs[CORNER_RADIUS]      ?: 16f,
                showHours          = prefs[SHOW_HOURS]         ?: true,
                showSeconds        = prefs[SHOW_SECONDS]       ?: true,
                showButtons        = prefs[SHOW_BUTTONS]       ?: true,
                keepScreenOn       = prefs[KEEP_SCREEN_ON]     ?: false,
                autoLaunch         = prefs[AUTO_LAUNCH]        ?: false,
                timeLimitSeconds   = prefs[TIME_LIMIT_SECONDS] ?: 0L,
                isBeepEnabled      = prefs[BEEP_ENABLED]       ?: false,
                isVibrationEnabled = prefs[VIBRATION_ENABLED]  ?: false,
                lastX              = prefs[LAST_X]             ?: -1,
                lastY              = prefs[LAST_Y]             ?: -1,
                totalLifetimeMs    = prefs[TOTAL_LIFETIME_MS]  ?: 0L,
                currentCycleMs     = prefs[CURRENT_CYCLE_MS]   ?: 0L,
                lastUpdateCheck    = prefs[LAST_UPDATE_CHECK]  ?: 0L,
                focusModeEnabled   = prefs[FOCUS_MODE_ENABLED] ?: false,
                selectedTheme      = prefs[SELECTED_THEME]     ?: "AUTO",
                appLanguage        = prefs[APP_LANGUAGE]       ?: "pt-BR",
                selectedFont       = prefs[SELECTED_FONT]      ?: "SYSTEM_DEFAULT",
                appFontSize        = prefs[APP_FONT_SIZE]      ?: "NORMAL",
                stopwatchFormat    = prefs[STOPWATCH_FORMAT]   ?: "HH_MM_SS",
                countdownFormat    = prefs[COUNTDOWN_FORMAT]   ?: "HH_MM_SS",
                countdownScreenBaseSeconds = prefs[COUNTDOWN_SCREEN_BASE_SECONDS] ?: 0L,
                pomodoroFormat     = prefs[POMODORO_FORMAT]    ?: "HH_MM_SS",
                pomodoroBeepFocusBreak = prefs[POMODORO_BEEP_FOCUS_BREAK] ?: false,
                pomodoroTickingSound = prefs[POMODORO_TICKING_SOUND] ?: false,
                pomodoroFocusAlertEnabled = prefs[POMODORO_FOCUS_ALERT_ENABLED] ?: true,
                pomodoroBreakAlertEnabled = prefs[POMODORO_BREAK_ALERT_ENABLED] ?: true,
                pomodoroAutoStartBreak = prefs[POMODORO_AUTO_START_BREAK] ?: true,
                pomodoroAutoStartFocus = prefs[POMODORO_AUTO_START_FOCUS] ?: true,
                pomodoroAutoNextCycle = prefs[POMODORO_AUTO_NEXT_CYCLE] ?: (prefs[POMODORO_AUTO_START_BREAK] ?: true),
                pomodoroPreset     = prefs[POMODORO_PRESET] ?: "CLASSICO",
                pomodoroPresetsSpec = prefs[POMODORO_PRESETS_SPEC] ?: "",
                pomodoroCustomPresetName = prefs[POMODORO_CUSTOM_PRESET_NAME] ?: "Meu Preset",
                pomodoroCustomPhasesSpec = prefs[POMODORO_CUSTOM_PHASES_SPEC] ?: "Etapa 1|25|4294198070|FOCUS_A;Etapa 2|5|4280391411|BREAK_A",
                pomodoroCustomFocusMinutes = prefs[POMODORO_CUSTOM_FOCUS_MINUTES] ?: 25,
                pomodoroCustomBreakMinutes = prefs[POMODORO_CUSTOM_BREAK_MINUTES] ?: 5,
                pomodoroCustomCycles = prefs[POMODORO_CUSTOM_CYCLES] ?: 4,
                pomodoroTickSoundType = prefs[POMODORO_TICK_SOUND_TYPE] ?: "TICK_A",
                pomodoroFocusAlertSoundType = prefs[POMODORO_FOCUS_ALERT_SOUND_TYPE] ?: "FOCUS_A",
                pomodoroBreakAlertSoundType = prefs[POMODORO_BREAK_ALERT_SOUND_TYPE] ?: "BREAK_A",
                overlayFontFamily = prefs[OVERLAY_FONT_FAMILY] ?: (prefs[SELECTED_FONT] ?: "CHIVO_MONO"),
                playPauseSoundEnabled = prefs[PLAY_PAUSE_SOUND_ENABLED] ?: (prefs[BEEP_ENABLED] ?: false),
                playPauseVibrationEnabled = prefs[PLAY_PAUSE_VIBRATION_ENABLED] ?: (prefs[VIBRATION_ENABLED] ?: false),
                playPauseVolume = prefs[PLAY_PAUSE_VOLUME] ?: 0.8f,
                tickVolume = prefs[TICK_VOLUME] ?: 0.35f,
                focusAlertVolume = prefs[FOCUS_ALERT_VOLUME] ?: 0.9f,
                breakAlertVolume = prefs[BREAK_ALERT_VOLUME] ?: 0.9f,
                stopwatchOverlayShowButtons = prefs[SW_OVERLAY_SHOW_BUTTONS] ?: (prefs[SHOW_BUTTONS] ?: true),
                stopwatchOverlayShowHours = prefs[SW_OVERLAY_SHOW_HOURS] ?: (prefs[SHOW_HOURS] ?: true),
                stopwatchOverlayShowSeconds = prefs[SW_OVERLAY_SHOW_SECONDS] ?: (prefs[SHOW_SECONDS] ?: true),
                stopwatchOverlayScale = prefs[SW_OVERLAY_SCALE] ?: (prefs[SCALE] ?: 1.0f),
                stopwatchOverlayCornerRadius = prefs[SW_OVERLAY_CORNER_RADIUS] ?: (prefs[CORNER_RADIUS] ?: 16f),
                stopwatchOverlayCustomColor = prefs[SW_OVERLAY_CUSTOM_COLOR],
                stopwatchOverlayCustomTextColor = prefs[SW_OVERLAY_CUSTOM_TEXT_COLOR],
                countdownOverlayShowButtons = prefs[CD_OVERLAY_SHOW_BUTTONS] ?: (prefs[SHOW_BUTTONS] ?: true),
                countdownOverlayShowHours = prefs[CD_OVERLAY_SHOW_HOURS] ?: (prefs[SHOW_HOURS] ?: true),
                countdownOverlayShowSeconds = prefs[CD_OVERLAY_SHOW_SECONDS] ?: (prefs[SHOW_SECONDS] ?: true),
                countdownOverlayScale = prefs[CD_OVERLAY_SCALE] ?: (prefs[SCALE] ?: 1.0f),
                countdownOverlayCornerRadius = prefs[CD_OVERLAY_CORNER_RADIUS] ?: (prefs[CORNER_RADIUS] ?: 16f),
                countdownOverlayCustomColor = prefs[CD_OVERLAY_CUSTOM_COLOR],
                countdownOverlayCustomTextColor = prefs[CD_OVERLAY_CUSTOM_TEXT_COLOR],
                pomodoroOverlayShowButtons = prefs[PO_OVERLAY_SHOW_BUTTONS] ?: (prefs[SHOW_BUTTONS] ?: true),
                pomodoroOverlayShowHours = prefs[PO_OVERLAY_SHOW_HOURS] ?: (prefs[SHOW_HOURS] ?: true),
                pomodoroOverlayShowSeconds = prefs[PO_OVERLAY_SHOW_SECONDS] ?: (prefs[SHOW_SECONDS] ?: true),
                pomodoroOverlayScale = prefs[PO_OVERLAY_SCALE] ?: (prefs[SCALE] ?: 1.0f),
                pomodoroOverlayCornerRadius = prefs[PO_OVERLAY_CORNER_RADIUS] ?: (prefs[CORNER_RADIUS] ?: 16f),
                pomodoroOverlayCustomColor = prefs[PO_OVERLAY_CUSTOM_COLOR],
                pomodoroOverlayCustomTextColor = prefs[PO_OVERLAY_CUSTOM_TEXT_COLOR],
                donationPending    = prefs[DONATION_PENDING]   ?: false,
                activeToolId       = prefs[ACTIVE_TOOL_ID]     ?: "stopwatch"
            )
        }

    suspend fun updateConfig(config: OverlayConfig) {
        context.dataStore.edit { prefs ->
            prefs[BACKGROUND_COLOR]   = config.backgroundColor
            prefs[TEXT_COLOR]         = config.textColor
            prefs[BG_OPACITY]         = config.bgOpacity
            prefs[TEXT_OPACITY]       = config.textOpacity
            prefs[SCALE]              = config.scale
            prefs[CORNER_RADIUS]      = config.cornerRadius
            prefs[SHOW_HOURS]         = config.showHours
            prefs[SHOW_SECONDS]       = config.showSeconds
            prefs[SHOW_BUTTONS]       = config.showButtons
            prefs[KEEP_SCREEN_ON]     = config.keepScreenOn
            prefs[AUTO_LAUNCH]        = config.autoLaunch
            prefs[TIME_LIMIT_SECONDS] = config.timeLimitSeconds
            prefs[BEEP_ENABLED]       = config.isBeepEnabled
            prefs[VIBRATION_ENABLED]  = config.isVibrationEnabled
            prefs[LAST_X]             = config.lastX
            prefs[LAST_Y]             = config.lastY
            prefs[TOTAL_LIFETIME_MS]  = config.totalLifetimeMs
            prefs[CURRENT_CYCLE_MS]   = config.currentCycleMs
            prefs[FOCUS_MODE_ENABLED] = config.focusModeEnabled
            prefs[SELECTED_THEME]     = config.selectedTheme
            prefs[APP_LANGUAGE]       = config.appLanguage
            prefs[SELECTED_FONT]      = config.selectedFont
            prefs[APP_FONT_SIZE]      = config.appFontSize
            prefs[STOPWATCH_FORMAT]   = config.stopwatchFormat
            prefs[COUNTDOWN_FORMAT]   = config.countdownFormat
            prefs[COUNTDOWN_SCREEN_BASE_SECONDS] = config.countdownScreenBaseSeconds
            prefs[POMODORO_FORMAT]    = config.pomodoroFormat
            prefs[POMODORO_BEEP_FOCUS_BREAK] = config.pomodoroBeepFocusBreak
            prefs[POMODORO_TICKING_SOUND] = config.pomodoroTickingSound
            prefs[POMODORO_FOCUS_ALERT_ENABLED] = config.pomodoroFocusAlertEnabled
            prefs[POMODORO_BREAK_ALERT_ENABLED] = config.pomodoroBreakAlertEnabled
            prefs[POMODORO_AUTO_START_BREAK] = config.pomodoroAutoStartBreak
            prefs[POMODORO_AUTO_START_FOCUS] = config.pomodoroAutoStartFocus
            prefs[POMODORO_AUTO_NEXT_CYCLE] = config.pomodoroAutoNextCycle
            prefs[POMODORO_PRESET] = config.pomodoroPreset
            prefs[POMODORO_PRESETS_SPEC] = config.pomodoroPresetsSpec
            prefs[POMODORO_CUSTOM_PRESET_NAME] = config.pomodoroCustomPresetName
            prefs[POMODORO_CUSTOM_PHASES_SPEC] = config.pomodoroCustomPhasesSpec
            prefs[POMODORO_CUSTOM_FOCUS_MINUTES] = config.pomodoroCustomFocusMinutes
            prefs[POMODORO_CUSTOM_BREAK_MINUTES] = config.pomodoroCustomBreakMinutes
            prefs[POMODORO_CUSTOM_CYCLES] = config.pomodoroCustomCycles
            prefs[POMODORO_TICK_SOUND_TYPE] = config.pomodoroTickSoundType
            prefs[POMODORO_FOCUS_ALERT_SOUND_TYPE] = config.pomodoroFocusAlertSoundType
            prefs[POMODORO_BREAK_ALERT_SOUND_TYPE] = config.pomodoroBreakAlertSoundType
            prefs[OVERLAY_FONT_FAMILY] = config.overlayFontFamily
            prefs[PLAY_PAUSE_SOUND_ENABLED] = config.playPauseSoundEnabled
            prefs[PLAY_PAUSE_VIBRATION_ENABLED] = config.playPauseVibrationEnabled
            prefs[PLAY_PAUSE_VOLUME] = config.playPauseVolume
            prefs[TICK_VOLUME] = config.tickVolume
            prefs[FOCUS_ALERT_VOLUME] = config.focusAlertVolume
            prefs[BREAK_ALERT_VOLUME] = config.breakAlertVolume
            prefs[SW_OVERLAY_SHOW_BUTTONS] = config.stopwatchOverlayShowButtons
            prefs[SW_OVERLAY_SHOW_HOURS] = config.stopwatchOverlayShowHours
            prefs[SW_OVERLAY_SHOW_SECONDS] = config.stopwatchOverlayShowSeconds
            prefs[SW_OVERLAY_SCALE] = config.stopwatchOverlayScale
            prefs[SW_OVERLAY_CORNER_RADIUS] = config.stopwatchOverlayCornerRadius
            if (config.stopwatchOverlayCustomColor == null) prefs.remove(SW_OVERLAY_CUSTOM_COLOR) else prefs[SW_OVERLAY_CUSTOM_COLOR] = config.stopwatchOverlayCustomColor
            if (config.stopwatchOverlayCustomTextColor == null) prefs.remove(SW_OVERLAY_CUSTOM_TEXT_COLOR) else prefs[SW_OVERLAY_CUSTOM_TEXT_COLOR] = config.stopwatchOverlayCustomTextColor
            prefs[CD_OVERLAY_SHOW_BUTTONS] = config.countdownOverlayShowButtons
            prefs[CD_OVERLAY_SHOW_HOURS] = config.countdownOverlayShowHours
            prefs[CD_OVERLAY_SHOW_SECONDS] = config.countdownOverlayShowSeconds
            prefs[CD_OVERLAY_SCALE] = config.countdownOverlayScale
            prefs[CD_OVERLAY_CORNER_RADIUS] = config.countdownOverlayCornerRadius
            if (config.countdownOverlayCustomColor == null) prefs.remove(CD_OVERLAY_CUSTOM_COLOR) else prefs[CD_OVERLAY_CUSTOM_COLOR] = config.countdownOverlayCustomColor
            if (config.countdownOverlayCustomTextColor == null) prefs.remove(CD_OVERLAY_CUSTOM_TEXT_COLOR) else prefs[CD_OVERLAY_CUSTOM_TEXT_COLOR] = config.countdownOverlayCustomTextColor
            prefs[PO_OVERLAY_SHOW_BUTTONS] = config.pomodoroOverlayShowButtons
            prefs[PO_OVERLAY_SHOW_HOURS] = config.pomodoroOverlayShowHours
            prefs[PO_OVERLAY_SHOW_SECONDS] = config.pomodoroOverlayShowSeconds
            prefs[PO_OVERLAY_SCALE] = config.pomodoroOverlayScale
            prefs[PO_OVERLAY_CORNER_RADIUS] = config.pomodoroOverlayCornerRadius
            if (config.pomodoroOverlayCustomColor == null) prefs.remove(PO_OVERLAY_CUSTOM_COLOR) else prefs[PO_OVERLAY_CUSTOM_COLOR] = config.pomodoroOverlayCustomColor
            if (config.pomodoroOverlayCustomTextColor == null) prefs.remove(PO_OVERLAY_CUSTOM_TEXT_COLOR) else prefs[PO_OVERLAY_CUSTOM_TEXT_COLOR] = config.pomodoroOverlayCustomTextColor
            prefs[DONATION_PENDING]   = config.donationPending
            prefs[ACTIVE_TOOL_ID]     = config.activeToolId
        }
    }

    suspend fun savePosition(x: Int, y: Int) {
        context.dataStore.edit { prefs ->
            prefs[LAST_X] = x
            prefs[LAST_Y] = y
        }
    }

    suspend fun accumulateTime(sessionMs: Long) {
        context.dataStore.edit { prefs ->
            val prevLifetime = prefs[TOTAL_LIFETIME_MS] ?: 0L
            val prevCycle    = prefs[CURRENT_CYCLE_MS]  ?: 0L
            val newCycle     = prevCycle + sessionMs
            
            prefs[TOTAL_LIFETIME_MS] = prevLifetime + sessionMs
            prefs[CURRENT_CYCLE_MS]  = newCycle
            
            // Ativando a flag ao atingir 24h acumuladas (86.400.000 ms)
            if (newCycle >= 50 * 3600 * 1000L) { // ORIGINAL 50 HORAS
            //if (newCycle >= 5 * 1000L) { // TESTE 5 SEGUNDOS - MANTER ESTA LINHA NO CÓDIGO
                prefs[DONATION_PENDING] = true
            }
        }
    }

    suspend fun resetDonationCycle() {
        context.dataStore.edit { prefs ->
            prefs[CURRENT_CYCLE_MS] = 0L
            prefs[DONATION_PENDING] = false
        }
    }

    suspend fun saveLastUpdateCheck(timestamp: Long) {
        context.dataStore.edit { prefs ->
            prefs[LAST_UPDATE_CHECK] = timestamp
        }
    }
}

