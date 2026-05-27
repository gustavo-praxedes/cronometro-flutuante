package com.krono.app.core.data

import android.content.Context
import android.graphics.Color as AndroidColor
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.krono.app.core.util.SOUND_NONE
import com.krono.app.core.util.normalizeNotificationSound
import com.krono.app.feature.pomodoro.PomodoroPresetCatalog
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
        val SHOW_MINUTES       = booleanPreferencesKey("show_minutes")
        val SHOW_SECONDS       = booleanPreferencesKey("show_seconds")
        val SHOW_MILLISECONDS  = booleanPreferencesKey("show_milliseconds")
        val SHOW_BUTTONS       = booleanPreferencesKey("show_buttons")
        val HIDE_OVERLAY_BUTTONS = booleanPreferencesKey("hide_overlay_buttons")
        val KEEP_SCREEN_ON     = booleanPreferencesKey("keep_screen_on")
        val AUTO_LAUNCH        = booleanPreferencesKey("auto_launch")
        val OPEN_OVERLAY_ON_PLAY = booleanPreferencesKey("open_overlay_on_play")
        val BEEP_ENABLED       = booleanPreferencesKey("beep_enabled")
        val LAST_X             = intPreferencesKey("last_x")
        val LAST_Y             = intPreferencesKey("last_y")
        val TOTAL_LIFETIME_MS  = longPreferencesKey("total_lifetime_ms")
        val CURRENT_CYCLE_MS   = longPreferencesKey("current_cycle_ms")
        val LAST_UPDATE_CHECK  = longPreferencesKey("last_update_check")
        val FOCUS_MODE_ENABLED = booleanPreferencesKey("focus_mode_enabled")
        val SELECTED_THEME     = stringPreferencesKey("selected_theme")
        val APP_LANGUAGE       = stringPreferencesKey("app_language")
        val SELECTED_FONT      = stringPreferencesKey("selected_font")
        val AVAILABLE_GOOGLE_FONTS = stringPreferencesKey("available_google_fonts")
        val DONATION_PENDING   = booleanPreferencesKey("donation_pending")
        val ACTIVE_TOOL_ID     = stringPreferencesKey("active_tool_id")
        val DIRECT_LAUNCH_TOOL_ID = stringPreferencesKey("direct_launch_tool_id")
        val APP_FONT_SIZE      = stringPreferencesKey("app_font_size")
        val STOPWATCH_FORMAT   = stringPreferencesKey("stopwatch_format")
        val COUNTDOWN_FORMAT   = stringPreferencesKey("countdown_format")
        val COUNTDOWN_SCREEN_BASE_SECONDS = longPreferencesKey("countdown_screen_base_seconds")
        val POMODORO_FORMAT    = stringPreferencesKey("pomodoro_format")
        val POMODORO_BEEP_FOCUS_BREAK = booleanPreferencesKey("pomodoro_beep_focus_break")
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
        val POMODORO_FOCUS_ALERT_SOUND_TYPE = stringPreferencesKey("pomodoro_focus_alert_sound_type")
        val POMODORO_BREAK_ALERT_SOUND_TYPE = stringPreferencesKey("pomodoro_break_alert_sound_type")
        val POMODORO_DND_DURING_FOCUS = booleanPreferencesKey("pomodoro_dnd_during_focus")
        val POMODORO_DAILY_GOAL_CYCLES = intPreferencesKey("pomodoro_daily_goal_cycles")
        val POMODORO_SESSION_HISTORY = stringPreferencesKey("pomodoro_session_history")
        val OVERLAY_FONT_FAMILY = stringPreferencesKey("overlay_font_family")
        val OVERLAY_CUSTOM_COLOR = intPreferencesKey("overlay_custom_color")
        val OVERLAY_CUSTOM_TEXT_COLOR = intPreferencesKey("overlay_custom_text_color")
        val ALL_SOUNDS_ENABLED = booleanPreferencesKey("all_sounds_enabled")
        val PLAY_PAUSE_SOUND_ENABLED = booleanPreferencesKey("play_pause_sound_enabled")
        val PLAY_PAUSE_VIBRATION_ENABLED = booleanPreferencesKey("play_pause_vibration_enabled")
        val SECONDS_VIBRATION_ENABLED = booleanPreferencesKey("seconds_vibration_enabled")
        val TICK_SOUND_ENABLED = booleanPreferencesKey("tick_sound_enabled")
        val ENVIRONMENT_SOUND_TYPE = stringPreferencesKey("environment_sound_type")
        val APP_NOTIFICATION_SOUND_TYPE = stringPreferencesKey("app_notification_sound_type")
        val APP_NOTIFICATION_VOLUME = floatPreferencesKey("app_notification_volume")
        val TICK_VOLUME = floatPreferencesKey("tick_volume")
        val PLAY_PAUSE_VOLUME = floatPreferencesKey("play_pause_volume")
        val PLAY_PAUSE_SOUND_TYPE = stringPreferencesKey("play_pause_sound_type")
        val FOCUS_ALERT_VOLUME = floatPreferencesKey("focus_alert_volume")
        val BREAK_ALERT_VOLUME = floatPreferencesKey("break_alert_volume")
        val SW_OVERLAY_SHOW_BUTTONS = booleanPreferencesKey("sw_overlay_show_buttons")
        val SW_OVERLAY_SCALE = floatPreferencesKey("sw_overlay_scale")
        val SW_OVERLAY_CORNER_RADIUS = floatPreferencesKey("sw_overlay_corner_radius")
        val SW_OVERLAY_CUSTOM_COLOR = intPreferencesKey("sw_overlay_custom_color")
        val SW_OVERLAY_CUSTOM_TEXT_COLOR = intPreferencesKey("sw_overlay_custom_text_color")
        val SW_OVERLAY_LAST_X = intPreferencesKey("sw_overlay_last_x")
        val SW_OVERLAY_LAST_Y = intPreferencesKey("sw_overlay_last_y")
        val CD_OVERLAY_SHOW_BUTTONS = booleanPreferencesKey("cd_overlay_show_buttons")
        val CD_OVERLAY_SCALE = floatPreferencesKey("cd_overlay_scale")
        val CD_OVERLAY_CORNER_RADIUS = floatPreferencesKey("cd_overlay_corner_radius")
        val CD_OVERLAY_CUSTOM_COLOR = intPreferencesKey("cd_overlay_custom_color")
        val CD_OVERLAY_CUSTOM_TEXT_COLOR = intPreferencesKey("cd_overlay_custom_text_color")
        val PO_OVERLAY_SHOW_BUTTONS = booleanPreferencesKey("po_overlay_show_buttons")
        val PO_OVERLAY_SCALE = floatPreferencesKey("po_overlay_scale")
        val PO_OVERLAY_CORNER_RADIUS = floatPreferencesKey("po_overlay_corner_radius")
        val PO_OVERLAY_CUSTOM_COLOR = intPreferencesKey("po_overlay_custom_color")
        val PO_OVERLAY_CUSTOM_TEXT_COLOR = intPreferencesKey("po_overlay_custom_text_color")
        val PO_OVERLAY_LAST_X = intPreferencesKey("po_overlay_last_x")
        val PO_OVERLAY_LAST_Y = intPreferencesKey("po_overlay_last_y")
    }

    val configFlow: Flow<OverlayConfig> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException || exception is ClassCastException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map(::preferencesToConfig)

    suspend fun updateConfig(config: OverlayConfig) {
        context.dataStore.edit { prefs ->
            writeConfig(prefs, config)
        }
    }

    suspend fun updateConfig(transform: (OverlayConfig) -> OverlayConfig) {
        context.dataStore.edit { prefs ->
            val current = preferencesToConfig(prefs)
            writeConfig(prefs, transform(current))
        }
    }

    suspend fun migratePomodoroPresetsIfNeeded() {
        context.dataStore.edit { prefs ->
            val currentSpec = prefs[POMODORO_PRESETS_SPEC].orEmpty()
            val legacyName = prefs[POMODORO_CUSTOM_PRESET_NAME] ?: "Meu Preset"
            val legacySpec = prefs[POMODORO_CUSTOM_PHASES_SPEC].orEmpty()
            val legacyCycles = prefs[POMODORO_CUSTOM_CYCLES] ?: 4
            val selectedPreset = PomodoroPresetCatalog.normalizeSelectedPresetId(
                prefs[POMODORO_PRESET] ?: PomodoroPresetCatalog.DEFAULT_ID
            )

            val needsMigration = currentSpec.isBlank() ||
                PomodoroPresetCatalog.isLegacyPresetStorage(currentSpec) ||
                PomodoroPresetCatalog.requiresCatalogMigration(currentSpec) ||
                selectedPreset != (prefs[POMODORO_PRESET] ?: PomodoroPresetCatalog.DEFAULT_ID) ||
                prefs.contains(POMODORO_CUSTOM_PRESET_NAME) ||
                prefs.contains(POMODORO_CUSTOM_PHASES_SPEC) ||
                prefs.contains(POMODORO_CUSTOM_FOCUS_MINUTES) ||
                prefs.contains(POMODORO_CUSTOM_BREAK_MINUTES) ||
                prefs.contains(POMODORO_CUSTOM_CYCLES)

            if (!needsMigration) return@edit

            val migratedPresets = PomodoroPresetCatalog.migrateLegacyPresets(
                raw = currentSpec,
                legacyCustomName = legacyName,
                legacyCustomSpec = legacySpec,
                legacyCustomCycles = legacyCycles
            )
            val safePresetId = migratedPresets.firstOrNull { it.id == selectedPreset }?.id
                ?: migratedPresets.firstOrNull()?.id
                ?: PomodoroPresetCatalog.DEFAULT_ID

            prefs[POMODORO_PRESETS_SPEC] = PomodoroPresetCatalog.encode(migratedPresets)
            prefs[POMODORO_PRESET] = safePresetId
            prefs.remove(POMODORO_CUSTOM_PRESET_NAME)
            prefs.remove(POMODORO_CUSTOM_PHASES_SPEC)
            prefs.remove(POMODORO_CUSTOM_FOCUS_MINUTES)
            prefs.remove(POMODORO_CUSTOM_BREAK_MINUTES)
            prefs.remove(POMODORO_CUSTOM_CYCLES)
        }
    }

    suspend fun savePosition(x: Int, y: Int) {
        context.dataStore.edit { prefs ->
            prefs[LAST_X] = x
            prefs[LAST_Y] = y
        }
    }

    suspend fun savePosition(toolId: String, x: Int, y: Int) {
        context.dataStore.edit { prefs ->
            when (toolId) {
                "pomodoro" -> {
                    prefs[PO_OVERLAY_LAST_X] = x
                    prefs[PO_OVERLAY_LAST_Y] = y
                }
                else -> {
                    prefs[SW_OVERLAY_LAST_X] = x
                    prefs[SW_OVERLAY_LAST_Y] = y
                    prefs[LAST_X] = x
                    prefs[LAST_Y] = y
                }
            }
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

    suspend fun recordPomodoroSession(presetId: String, completedCycles: Int) {
        context.dataStore.edit { prefs ->
            val entry = "${System.currentTimeMillis()}|${presetId}|${completedCycles.coerceAtLeast(1)}"
            val old = prefs[POMODORO_SESSION_HISTORY].orEmpty()
            prefs[POMODORO_SESSION_HISTORY] = (listOf(entry) + old.split(";").filter { it.isNotBlank() })
                .take(30)
                .joinToString(";")
        }
    }

    private fun preferencesToConfig(prefs: Preferences): OverlayConfig {
        val resolvedPlayPauseSoundType = prefs[PLAY_PAUSE_SOUND_TYPE]
            ?: if ((prefs[PLAY_PAUSE_SOUND_ENABLED] ?: prefs[BEEP_ENABLED] ?: false)) "krono_tip_complete" else SOUND_NONE
        val resolvedEnvironmentSoundTypeRaw = prefs[ENVIRONMENT_SOUND_TYPE]
            ?: if ((prefs[TICK_SOUND_ENABLED] ?: false)) "krono_env_brownnoise" else SOUND_NONE
        val resolvedEnvironmentSoundType = resolvedEnvironmentSoundTypeRaw
        return OverlayConfig(
            backgroundColor = prefs[BACKGROUND_COLOR] ?: AndroidColor.WHITE,
            textColor = prefs[TEXT_COLOR] ?: AndroidColor.BLACK,
            bgOpacity = prefs[BG_OPACITY] ?: 1.0f,
            textOpacity = prefs[TEXT_OPACITY] ?: 1.0f,
            scale = (prefs[SCALE] ?: prefs[SW_OVERLAY_SCALE] ?: prefs[CD_OVERLAY_SCALE] ?: prefs[PO_OVERLAY_SCALE] ?: 1.0f).coerceIn(1.0f, 2.0f),
            cornerRadius = prefs[CORNER_RADIUS] ?: prefs[SW_OVERLAY_CORNER_RADIUS] ?: prefs[CD_OVERLAY_CORNER_RADIUS] ?: prefs[PO_OVERLAY_CORNER_RADIUS] ?: 16f,
            showHours = prefs[SHOW_HOURS] ?: true,
            showMinutes = prefs[SHOW_MINUTES] ?: true,
            showSeconds = prefs[SHOW_SECONDS] ?: true,
            showMilliseconds = prefs[SHOW_MILLISECONDS] ?: false,
            showButtons = prefs[SHOW_BUTTONS] ?: true,
            hideOverlayButtons = prefs[HIDE_OVERLAY_BUTTONS] ?: run {
                val legacyGlobal = prefs[SHOW_BUTTONS]
                val legacyToolValues = listOfNotNull(
                    prefs[SW_OVERLAY_SHOW_BUTTONS],
                    prefs[CD_OVERLAY_SHOW_BUTTONS],
                    prefs[PO_OVERLAY_SHOW_BUTTONS]
                )
                when {
                    legacyGlobal != null -> !legacyGlobal
                    legacyToolValues.isNotEmpty() -> legacyToolValues.any { !it }
                    else -> false
                }
            },
            keepScreenOn = prefs[KEEP_SCREEN_ON] ?: false,
            autoLaunch = prefs[AUTO_LAUNCH] ?: false,
            openOverlayOnPlay = prefs[OPEN_OVERLAY_ON_PLAY] ?: false,
            isBeepEnabled = prefs[BEEP_ENABLED] ?: false,
            lastX = prefs[LAST_X] ?: -1,
            lastY = prefs[LAST_Y] ?: -1,
            totalLifetimeMs = prefs[TOTAL_LIFETIME_MS] ?: 0L,
            currentCycleMs = prefs[CURRENT_CYCLE_MS] ?: 0L,
            lastUpdateCheck = prefs[LAST_UPDATE_CHECK] ?: 0L,
            focusModeEnabled = prefs[FOCUS_MODE_ENABLED] ?: false,
            selectedTheme = prefs[SELECTED_THEME] ?: "AUTO",
            appLanguage = prefs[APP_LANGUAGE] ?: "pt-BR",
            selectedFont = normalizeAppFont(prefs[SELECTED_FONT]),
            availableGoogleFonts = prefs[AVAILABLE_GOOGLE_FONTS] ?: "",
            appFontSize = prefs[APP_FONT_SIZE] ?: "NORMAL",
            stopwatchFormat = prefs[STOPWATCH_FORMAT] ?: "HH_MM_SS",
            countdownFormat = prefs[COUNTDOWN_FORMAT] ?: "HH_MM_SS",
            countdownScreenBaseSeconds = prefs[COUNTDOWN_SCREEN_BASE_SECONDS] ?: 0L,
            pomodoroFormat = prefs[POMODORO_FORMAT] ?: "HH_MM_SS",
            pomodoroBeepFocusBreak = prefs[POMODORO_BEEP_FOCUS_BREAK] ?: false,
            pomodoroFocusAlertEnabled = prefs[POMODORO_FOCUS_ALERT_ENABLED] ?: true,
            pomodoroBreakAlertEnabled = prefs[POMODORO_BREAK_ALERT_ENABLED] ?: true,
            pomodoroAutoStartBreak = prefs[POMODORO_AUTO_START_BREAK] ?: true,
            pomodoroAutoStartFocus = prefs[POMODORO_AUTO_START_FOCUS] ?: true,
            pomodoroAutoNextCycle = prefs[POMODORO_AUTO_NEXT_CYCLE] ?: (prefs[POMODORO_AUTO_START_BREAK] ?: true),
            pomodoroPreset = PomodoroPresetCatalog.normalizeSelectedPresetId(
                prefs[POMODORO_PRESET] ?: PomodoroPresetCatalog.DEFAULT_ID
            ),
            pomodoroPresetsSpec = prefs[POMODORO_PRESETS_SPEC] ?: "",
            pomodoroFocusAlertSoundType = normalizeNotificationSound(prefs[POMODORO_FOCUS_ALERT_SOUND_TYPE] ?: "krono_alm_alarmbeep"),
            pomodoroBreakAlertSoundType = normalizeNotificationSound(prefs[POMODORO_BREAK_ALERT_SOUND_TYPE] ?: "krono_alm_beeps"),
            pomodoroDndDuringFocus = prefs[POMODORO_DND_DURING_FOCUS] ?: false,
            pomodoroDailyGoalCycles = prefs[POMODORO_DAILY_GOAL_CYCLES] ?: 4,
            pomodoroSessionHistory = prefs[POMODORO_SESSION_HISTORY] ?: "",
            overlayFontFamily = prefs[OVERLAY_FONT_FAMILY] ?: "CHIVO_MONO",
            overlayCustomColor = prefs[OVERLAY_CUSTOM_COLOR]
                ?: prefs[SW_OVERLAY_CUSTOM_COLOR]
                ?: prefs[CD_OVERLAY_CUSTOM_COLOR]
                ?: prefs[PO_OVERLAY_CUSTOM_COLOR],
            overlayCustomTextColor = prefs[OVERLAY_CUSTOM_TEXT_COLOR]
                ?: prefs[SW_OVERLAY_CUSTOM_TEXT_COLOR]
                ?: prefs[CD_OVERLAY_CUSTOM_TEXT_COLOR]
                ?: prefs[PO_OVERLAY_CUSTOM_TEXT_COLOR],
            allSoundsEnabled = prefs[ALL_SOUNDS_ENABLED] ?: true,
            playPauseSoundEnabled = resolvedPlayPauseSoundType != SOUND_NONE,
            playPauseVibrationEnabled = prefs[PLAY_PAUSE_VIBRATION_ENABLED] ?: false,
            secondsVibrationEnabled = prefs[SECONDS_VIBRATION_ENABLED] ?: false,
            tickSoundEnabled = resolvedEnvironmentSoundType != SOUND_NONE,
            environmentSoundType = resolvedEnvironmentSoundType,
            appNotificationSoundType = prefs[APP_NOTIFICATION_SOUND_TYPE] ?: SOUND_NONE,
            appNotificationVolume = prefs[APP_NOTIFICATION_VOLUME] ?: 0.8f,
            tickVolume = prefs[TICK_VOLUME] ?: 0.35f,
            playPauseVolume = prefs[PLAY_PAUSE_VOLUME] ?: 0.8f,
            playPauseSoundType = resolvedPlayPauseSoundType,
            focusAlertVolume = prefs[FOCUS_ALERT_VOLUME] ?: 0.9f,
            breakAlertVolume = prefs[BREAK_ALERT_VOLUME] ?: 0.9f,
            stopwatchOverlayShowButtons = prefs[SW_OVERLAY_SHOW_BUTTONS] ?: (prefs[SHOW_BUTTONS] ?: true),
            stopwatchOverlayScale = prefs[SW_OVERLAY_SCALE] ?: (prefs[SCALE] ?: 1.0f),
            stopwatchOverlayCornerRadius = prefs[SW_OVERLAY_CORNER_RADIUS] ?: (prefs[CORNER_RADIUS] ?: 16f),
            stopwatchOverlayCustomColor = prefs[SW_OVERLAY_CUSTOM_COLOR],
            stopwatchOverlayCustomTextColor = prefs[SW_OVERLAY_CUSTOM_TEXT_COLOR],
            stopwatchOverlayLastX = prefs[SW_OVERLAY_LAST_X] ?: (prefs[LAST_X] ?: -1),
            stopwatchOverlayLastY = prefs[SW_OVERLAY_LAST_Y] ?: (prefs[LAST_Y] ?: -1),
            countdownOverlayShowButtons = prefs[CD_OVERLAY_SHOW_BUTTONS] ?: (prefs[SHOW_BUTTONS] ?: true),
            countdownOverlayScale = prefs[CD_OVERLAY_SCALE] ?: (prefs[SCALE] ?: 1.0f),
            countdownOverlayCornerRadius = prefs[CD_OVERLAY_CORNER_RADIUS] ?: (prefs[CORNER_RADIUS] ?: 16f),
            countdownOverlayCustomColor = prefs[CD_OVERLAY_CUSTOM_COLOR],
            countdownOverlayCustomTextColor = prefs[CD_OVERLAY_CUSTOM_TEXT_COLOR],
            pomodoroOverlayShowButtons = prefs[PO_OVERLAY_SHOW_BUTTONS] ?: (prefs[SHOW_BUTTONS] ?: true),
            pomodoroOverlayScale = prefs[PO_OVERLAY_SCALE] ?: (prefs[SCALE] ?: 1.0f),
            pomodoroOverlayCornerRadius = prefs[PO_OVERLAY_CORNER_RADIUS] ?: (prefs[CORNER_RADIUS] ?: 16f),
            pomodoroOverlayCustomColor = prefs[PO_OVERLAY_CUSTOM_COLOR],
            pomodoroOverlayCustomTextColor = prefs[PO_OVERLAY_CUSTOM_TEXT_COLOR],
            pomodoroOverlayLastX = prefs[PO_OVERLAY_LAST_X] ?: -1,
            pomodoroOverlayLastY = prefs[PO_OVERLAY_LAST_Y] ?: -1,
            donationPending = prefs[DONATION_PENDING] ?: false,
            activeToolId = prefs[ACTIVE_TOOL_ID] ?: "stopwatch",
            directLaunchToolId = prefs[DIRECT_LAUNCH_TOOL_ID] ?: (prefs[ACTIVE_TOOL_ID] ?: "stopwatch")
        )
    }

    private fun writeConfig(prefs: MutablePreferences, config: OverlayConfig) {
        prefs[BACKGROUND_COLOR] = config.backgroundColor
        prefs[TEXT_COLOR] = config.textColor
        prefs[BG_OPACITY] = config.bgOpacity
        prefs[TEXT_OPACITY] = config.textOpacity
        prefs[SCALE] = config.scale.coerceIn(1.0f, 2.0f)
        prefs[CORNER_RADIUS] = config.cornerRadius
        prefs[SHOW_HOURS] = config.showHours
        prefs[SHOW_MINUTES] = config.showMinutes
        prefs[SHOW_SECONDS] = config.showSeconds
        prefs[SHOW_MILLISECONDS] = config.showMilliseconds
        prefs[SHOW_BUTTONS] = config.showButtons
        prefs[HIDE_OVERLAY_BUTTONS] = config.hideOverlayButtons
        prefs[KEEP_SCREEN_ON] = config.keepScreenOn
        prefs[AUTO_LAUNCH] = config.autoLaunch
        prefs[OPEN_OVERLAY_ON_PLAY] = config.openOverlayOnPlay
        prefs[BEEP_ENABLED] = config.isBeepEnabled
        prefs[LAST_X] = config.lastX
        prefs[LAST_Y] = config.lastY
        prefs[TOTAL_LIFETIME_MS] = config.totalLifetimeMs
        prefs[CURRENT_CYCLE_MS] = config.currentCycleMs
        prefs[FOCUS_MODE_ENABLED] = config.focusModeEnabled
        prefs[SELECTED_THEME] = config.selectedTheme
        prefs[APP_LANGUAGE] = config.appLanguage
        prefs[SELECTED_FONT] = normalizeAppFont(config.selectedFont)
        prefs[AVAILABLE_GOOGLE_FONTS] = normalizeAvailableGoogleFonts(config.availableGoogleFonts)
        prefs[APP_FONT_SIZE] = config.appFontSize
        prefs[STOPWATCH_FORMAT] = config.stopwatchFormat
        prefs[COUNTDOWN_FORMAT] = config.countdownFormat
        prefs[COUNTDOWN_SCREEN_BASE_SECONDS] = config.countdownScreenBaseSeconds
        prefs[POMODORO_FORMAT] = config.pomodoroFormat
        prefs[POMODORO_BEEP_FOCUS_BREAK] = config.pomodoroBeepFocusBreak
        prefs[POMODORO_FOCUS_ALERT_ENABLED] = config.pomodoroFocusAlertEnabled
        prefs[POMODORO_BREAK_ALERT_ENABLED] = config.pomodoroBreakAlertEnabled
        prefs[POMODORO_AUTO_START_BREAK] = config.pomodoroAutoStartBreak
        prefs[POMODORO_AUTO_START_FOCUS] = config.pomodoroAutoStartFocus
        prefs[POMODORO_AUTO_NEXT_CYCLE] = config.pomodoroAutoNextCycle
        prefs[POMODORO_PRESET] = config.pomodoroPreset
        prefs[POMODORO_PRESETS_SPEC] = config.pomodoroPresetsSpec
        prefs[POMODORO_FOCUS_ALERT_SOUND_TYPE] = config.pomodoroFocusAlertSoundType
        prefs[POMODORO_BREAK_ALERT_SOUND_TYPE] = config.pomodoroBreakAlertSoundType
        prefs[POMODORO_DND_DURING_FOCUS] = config.pomodoroDndDuringFocus
        prefs[POMODORO_DAILY_GOAL_CYCLES] = config.pomodoroDailyGoalCycles
        prefs[POMODORO_SESSION_HISTORY] = config.pomodoroSessionHistory
        prefs[OVERLAY_FONT_FAMILY] = config.overlayFontFamily
        if (config.overlayCustomColor == null) prefs.remove(OVERLAY_CUSTOM_COLOR) else prefs[OVERLAY_CUSTOM_COLOR] = config.overlayCustomColor
        if (config.overlayCustomTextColor == null) prefs.remove(OVERLAY_CUSTOM_TEXT_COLOR) else prefs[OVERLAY_CUSTOM_TEXT_COLOR] = config.overlayCustomTextColor
        prefs[ALL_SOUNDS_ENABLED] = config.allSoundsEnabled
        prefs[PLAY_PAUSE_SOUND_ENABLED] = config.playPauseSoundEnabled
        prefs[PLAY_PAUSE_VIBRATION_ENABLED] = config.playPauseVibrationEnabled
        prefs[SECONDS_VIBRATION_ENABLED] = config.secondsVibrationEnabled
        prefs[TICK_SOUND_ENABLED] = config.environmentSoundType != SOUND_NONE
        prefs[ENVIRONMENT_SOUND_TYPE] = config.environmentSoundType
        prefs[APP_NOTIFICATION_SOUND_TYPE] = config.appNotificationSoundType
        prefs[APP_NOTIFICATION_VOLUME] = config.appNotificationVolume
        prefs[TICK_VOLUME] = config.tickVolume
        prefs[PLAY_PAUSE_VOLUME] = config.playPauseVolume
        prefs[PLAY_PAUSE_SOUND_TYPE] = config.playPauseSoundType
        prefs[FOCUS_ALERT_VOLUME] = config.focusAlertVolume
        prefs[BREAK_ALERT_VOLUME] = config.breakAlertVolume
        prefs[SW_OVERLAY_SHOW_BUTTONS] = config.stopwatchOverlayShowButtons
        prefs[SW_OVERLAY_SCALE] = config.stopwatchOverlayScale
        prefs[SW_OVERLAY_CORNER_RADIUS] = config.stopwatchOverlayCornerRadius
        if (config.stopwatchOverlayCustomColor == null) prefs.remove(SW_OVERLAY_CUSTOM_COLOR) else prefs[SW_OVERLAY_CUSTOM_COLOR] = config.stopwatchOverlayCustomColor
        if (config.stopwatchOverlayCustomTextColor == null) prefs.remove(SW_OVERLAY_CUSTOM_TEXT_COLOR) else prefs[SW_OVERLAY_CUSTOM_TEXT_COLOR] = config.stopwatchOverlayCustomTextColor
        prefs[SW_OVERLAY_LAST_X] = config.stopwatchOverlayLastX
        prefs[SW_OVERLAY_LAST_Y] = config.stopwatchOverlayLastY
        prefs[CD_OVERLAY_SHOW_BUTTONS] = config.countdownOverlayShowButtons
        prefs[CD_OVERLAY_SCALE] = config.countdownOverlayScale
        prefs[CD_OVERLAY_CORNER_RADIUS] = config.countdownOverlayCornerRadius
        if (config.countdownOverlayCustomColor == null) prefs.remove(CD_OVERLAY_CUSTOM_COLOR) else prefs[CD_OVERLAY_CUSTOM_COLOR] = config.countdownOverlayCustomColor
        if (config.countdownOverlayCustomTextColor == null) prefs.remove(CD_OVERLAY_CUSTOM_TEXT_COLOR) else prefs[CD_OVERLAY_CUSTOM_TEXT_COLOR] = config.countdownOverlayCustomTextColor
        prefs[PO_OVERLAY_SHOW_BUTTONS] = config.pomodoroOverlayShowButtons
        prefs[PO_OVERLAY_SCALE] = config.pomodoroOverlayScale
        prefs[PO_OVERLAY_CORNER_RADIUS] = config.pomodoroOverlayCornerRadius
        if (config.pomodoroOverlayCustomColor == null) prefs.remove(PO_OVERLAY_CUSTOM_COLOR) else prefs[PO_OVERLAY_CUSTOM_COLOR] = config.pomodoroOverlayCustomColor
        if (config.pomodoroOverlayCustomTextColor == null) prefs.remove(PO_OVERLAY_CUSTOM_TEXT_COLOR) else prefs[PO_OVERLAY_CUSTOM_TEXT_COLOR] = config.pomodoroOverlayCustomTextColor
        prefs[PO_OVERLAY_LAST_X] = config.pomodoroOverlayLastX
        prefs[PO_OVERLAY_LAST_Y] = config.pomodoroOverlayLastY
        prefs[DONATION_PENDING] = config.donationPending
        prefs[ACTIVE_TOOL_ID] = config.activeToolId
        prefs[DIRECT_LAUNCH_TOOL_ID] = config.directLaunchToolId
    }

    private fun normalizeAppFont(value: String?): String =
        when (value) {
            "CHIVO" -> "CHIVO"
            "CHIVO_LIGHT" -> "CHIVO_LIGHT"
            "JETBRAINS_MONO",
            "FIRA_CODE",
            "ANONYMOUS_PRO",
            "ROBOTO_MONO",
            "COMMIT_MONO",
            "AZERET_MONO",
            "CHIVO_MONO" -> value
            else -> "CHIVO"
        }

    private fun normalizeAvailableGoogleFonts(value: String): String =
        value.split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .joinToString(",")
}

