package com.krono.app.core.ui.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.krono.app.R
import com.krono.app.core.audio.EnvironmentSoundLoop
import com.krono.app.core.audio.KronoSoundCatalog
import com.krono.app.core.audio.SoundTimingPolicy
import com.krono.app.core.data.OverlayConfig
import com.krono.app.core.data.OverlayDataStore
import com.krono.app.core.ui.components.AppearanceSlider
import com.krono.app.core.ui.components.SettingsDivider
import com.krono.app.core.ui.dialogs.ColorPickerDialog
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoTokens
import com.krono.app.core.ui.components.FontSelector
import com.krono.app.core.ui.components.KronoDropdown
import com.krono.app.core.ui.components.ThemeSelector
import com.krono.app.core.ui.components.ToggleRow
import com.krono.app.core.util.SOUND_NONE
import com.krono.app.core.util.appNotificationSoundOptions
import com.krono.app.core.util.environmentSoundOptions
import com.krono.app.core.util.playPauseSoundOptions
import com.krono.app.core.util.previewAppNotificationSound
import com.krono.app.core.util.previewEnvironmentSound
import com.krono.app.core.util.previewPlayPauseSound
import com.krono.app.core.util.stopSoundPreview
import kotlinx.coroutines.launch

@Composable
fun AppearancePanel(
    dataStore: OverlayDataStore,
    modifier: Modifier = Modifier
) {
    val config = dataStore.configFlow.collectAsState<OverlayConfig, OverlayConfig?>(initial = null).value ?: return
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val langPtBr = stringResource(R.string.settings_language_pt_br)
    val volumeMinLabel = stringResource(R.string.settings_volume_min)
    val volumeMaxLabel = stringResource(R.string.settings_volume_max)
    val overlayScaleMin = stringResource(R.string.settings_overlay_scale_min)
    val overlayScaleMax = stringResource(R.string.settings_overlay_scale_max)
    val overlayCornerMin = stringResource(R.string.settings_overlay_corner_min)
    val overlayCornerMax = stringResource(R.string.settings_overlay_corner_max)
    var showOverlayColorPicker by remember { mutableStateOf(false) }
    var showOverlayTextColorPicker by remember { mutableStateOf(false) }

    SettingsPanelLayout(modifier = modifier) {
        SettingsGroup(title = stringResource(R.string.settings_group_theme)) {
            ThemeSelector(
                selectedTheme = config.selectedTheme,
                leadingIcon = KronoIcons.Action.Palette,
                onChange = { theme ->
                    scope.launch {
                        dataStore.updateConfig(
                            config.copy(
                                selectedTheme = theme
                            )
                        )
                    }
                }
            )
            SettingsDivider()

            AppFontSizeSelector(
                selected = config.appFontSize,
                leadingIcon = KronoIcons.Action.FormatSize,
                onChange = { value ->
                    scope.launch { dataStore.updateConfig(config.copy(appFontSize = value)) }
                }
            )
            SettingsDivider()

            val languageOptions = listOf("pt-BR")
            KronoDropdown(
                value = config.appLanguage,
                onValueChange = { locale ->
                    scope.launch { dataStore.updateConfig(config.copy(appLanguage = locale)) }
                },
                options = languageOptions,
                label = stringResource(R.string.settings_language_label),
                leadingIcon = KronoIcons.Action.Glyphs,
                textMapping = { tag ->
                    when (tag) {
                        "pt-BR" -> langPtBr
                        else -> tag
                    }
                }
            )
        }

        SettingsGroup(title = stringResource(R.string.settings_group_vibration)) {
            ToggleRow(
                label = stringResource(R.string.behavior_play_pause_vibration_label),
                subtitle = stringResource(R.string.behavior_play_pause_vibration_subtitle),
                checked = config.playPauseVibrationEnabled,
                leadingIcon = KronoIcons.Action.MobileVibrate,
                onChange = { enabled ->
                    scope.launch { dataStore.updateConfig { it.copy(playPauseVibrationEnabled = enabled) } }
                }
            )
            SettingsDivider()
            ToggleRow(
                label = stringResource(R.string.settings_seconds_vibration_label),
                subtitle = stringResource(R.string.settings_seconds_vibration_subtitle),
                checked = config.secondsVibrationEnabled,
                leadingIcon = KronoIcons.Action.MobileVibrate,
                onChange = { enabled ->
                    scope.launch { dataStore.updateConfig { it.copy(secondsVibrationEnabled = enabled) } }
                }
            )
        }

        SettingsGroup(title = stringResource(R.string.settings_group_sounds)) {
            val noneSoundLabel = stringResource(R.string.sound_none)
            val playPauseSounds = remember(noneSoundLabel) {
                playPauseSoundOptions().map {
                    val label = when (it.uriString) {
                        SOUND_NONE -> noneSoundLabel
                        else -> KronoSoundCatalog.playPauseLabelResId(it.uriString)?.let { res -> context.getString(res) } ?: it.uriString
                    }
                    it.copy(label = label)
                }
            }
            val environmentSounds = remember(noneSoundLabel) {
                environmentSoundOptions().map {
                    val label = when (it.uriString) {
                        SOUND_NONE -> noneSoundLabel
                        else -> KronoSoundCatalog.environmentLabelResId(it.uriString)?.let { res -> context.getString(res) } ?: it.uriString
                    }
                    it.copy(label = label)
                }
            }
            val notificationSounds = remember(noneSoundLabel) {
                appNotificationSoundOptions().map {
                    val label = when (it.uriString) {
                        SOUND_NONE -> noneSoundLabel
                        else -> KronoSoundCatalog.appNotificationLabelResId(it.uriString)?.let { res -> context.getString(res) } ?: it.uriString
                    }
                    it.copy(label = label)
                }
            }
            KronoDropdown(
                value = config.playPauseSoundType,
                onValueChange = { value ->
                    scope.launch {
                        dataStore.updateConfig(
                            config.copy(
                                playPauseSoundType = value,
                                playPauseSoundEnabled = value != SOUND_NONE
                            )
                        )
                    }
                },
                options = playPauseSounds.map { it.uriString },
                label = stringResource(R.string.settings_play_pause_sound_type),
                leadingIcon = KronoIcons.Action.NotificationSound,
                optionLeadingIcon = KronoIcons.Action.Volume,
                optionLeadingContentDescription = stringResource(R.string.settings_sound_preview),
                optionLeadingIconVisible = { it != SOUND_NONE },
                onOptionLeadingClick = { sound ->
                    if (sound != SOUND_NONE) previewPlayPauseSound(context, config.playPauseVolume, sound, SoundTimingPolicy.profile(sound).startDelayMs)
                },
                onDismiss = { stopSoundPreview() },
                textMapping = { value ->
                    playPauseSounds.firstOrNull { it.uriString == value }?.label
                        ?: playPauseSounds.first().label
                }
            )
            SettingsDivider()
            AppearanceSlider(
                label = stringResource(R.string.settings_volume_play_pause),
                value = config.playPauseVolume,
                minLabel = volumeMinLabel,
                maxLabel = volumeMaxLabel,
                range = 0f..1f,
                display = "${(config.playPauseVolume * 100).toInt()}%",
                onChange = { scope.launch { dataStore.updateConfig(config.copy(playPauseVolume = it)) } },
                enabled = config.playPauseSoundType != SOUND_NONE,
                modifier = Modifier.padding(
                    horizontal = KronoTokens.Settings.panelHorizontalInset,
                    vertical = KronoTokens.Spacing.sm
                )
            )
            SettingsDivider()
            KronoDropdown(
                value = config.environmentSoundType,
                onValueChange = { value ->
                    if (value == SOUND_NONE) EnvironmentSoundLoop.stop("settings NONE")
                    scope.launch {
                        dataStore.updateConfig(
                            config.copy(
                                environmentSoundType = value,
                                tickSoundEnabled = value != SOUND_NONE
                            )
                        )
                    }
                },
                options = environmentSounds.map { it.uriString },
                label = stringResource(R.string.settings_environment_sound_label),
                leadingIcon = KronoIcons.Feature.Timer,
                optionLeadingIcon = KronoIcons.Action.Volume,
                optionLeadingContentDescription = stringResource(R.string.settings_sound_preview),
                optionLeadingIconVisible = { it != SOUND_NONE },
                onOptionLeadingClick = { sound ->
                    if (sound != SOUND_NONE) previewEnvironmentSound(context, config.tickVolume, sound, SoundTimingPolicy.profile(sound).startDelayMs)
                },
                onDismiss = { stopSoundPreview() },
                textMapping = { value ->
                    environmentSounds.firstOrNull { it.uriString == value }?.label
                        ?: environmentSounds.first().label
                }
            )
            SettingsDivider()
            AppearanceSlider(
                label = stringResource(R.string.settings_volume_environment),
                value = config.tickVolume,
                minLabel = volumeMinLabel,
                maxLabel = volumeMaxLabel,
                range = 0f..1f,
                display = "${(config.tickVolume * 100).toInt()}%",
                onChange = { value -> scope.launch { dataStore.updateConfig(config.copy(tickVolume = value)) } },
                enabled = config.environmentSoundType != SOUND_NONE,
                modifier = Modifier.padding(
                    horizontal = KronoTokens.Settings.panelHorizontalInset,
                    vertical = KronoTokens.Spacing.sm
                )
            )
            SettingsDivider()
            KronoDropdown(
                value = config.appNotificationSoundType,
                onValueChange = { value ->
                    scope.launch { dataStore.updateConfig(config.copy(appNotificationSoundType = value)) }
                },
                options = notificationSounds.map { it.uriString },
                label = stringResource(R.string.settings_app_notification_sound_label),
                leadingIcon = KronoIcons.Action.NotificationSound,
                optionLeadingIcon = KronoIcons.Action.Volume,
                optionLeadingContentDescription = stringResource(R.string.settings_sound_preview),
                optionLeadingIconVisible = { it != SOUND_NONE },
                onOptionLeadingClick = { sound ->
                    if (sound != SOUND_NONE) previewAppNotificationSound(context, config.playPauseVolume, sound, SoundTimingPolicy.profile(sound).startDelayMs)
                },
                onDismiss = { stopSoundPreview() },
                textMapping = { value ->
                    notificationSounds.firstOrNull { it.uriString == value }?.label
                        ?: notificationSounds.first().label
                }
            )
        }

        SettingsGroup(title = stringResource(R.string.settings_widget_group)) {
            FontSelector(
                selectedFont = config.overlayFontFamily,
                onChange = { value ->
                    scope.launch { dataStore.updateConfig(config.copy(overlayFontFamily = value)) }
                },
                leadingIcon = KronoIcons.Action.TypeSpecimen
            )
            SettingsDivider()
            AppearanceSlider(
                label = stringResource(R.string.label_scale),
                value = config.scale,
                minLabel = overlayScaleMin,
                maxLabel = overlayScaleMax,
                range = 1.0f..2.0f,
                display = "%.1fx".format(config.scale),
                onChange = { value -> scope.launch { dataStore.updateConfig(config.copy(scale = value.coerceIn(1.0f, 2.0f))) } },
                modifier = Modifier.padding(
                    horizontal = KronoTokens.Settings.panelHorizontalInset,
                    vertical = KronoTokens.Spacing.sm
                )
            )
            SettingsDivider()
            AppearanceSlider(
                label = stringResource(R.string.label_corner_radius),
                value = config.cornerRadius,
                minLabel = overlayCornerMin,
                maxLabel = overlayCornerMax,
                range = 0f..50f,
                display = "${config.cornerRadius.toInt()}dp",
                onChange = { value -> scope.launch { dataStore.updateConfig(config.copy(cornerRadius = value)) } },
                modifier = Modifier.padding(
                    horizontal = KronoTokens.Settings.panelHorizontalInset,
                    vertical = KronoTokens.Spacing.sm
                )
            )
            SettingsDivider()
            ToggleRow(
                label = stringResource(R.string.label_hide_overlay_buttons),
                subtitle = stringResource(R.string.overlay_hide_buttons_subtitle),
                leadingIcon = KronoIcons.Action.KeyboardFull,
                checked = config.hideOverlayButtons,
                onChange = { value ->
                    scope.launch {
                        dataStore.updateConfig(
                            config.copy(
                                hideOverlayButtons = value,
                                showButtons = !value
                            )
                        )
                    }
                }
            )
            SettingsDivider()
            ToggleRow(
                label = stringResource(R.string.settings_overlay_custom_color),
                subtitle = config.overlayCustomColor?.let { "#%06X".format(it and 0xFFFFFF) }
                    ?: stringResource(R.string.settings_overlay_custom_color_none),
                leadingIcon = KronoIcons.Action.FormatPaint,
                checked = config.overlayCustomColor != null,
                onChange = { enabled ->
                    if (enabled) showOverlayColorPicker = true
                    else scope.launch { dataStore.updateConfig(config.copy(overlayCustomColor = null)) }
                }
            )
            SettingsDivider()
            ToggleRow(
                label = stringResource(R.string.settings_overlay_custom_text_color),
                subtitle = config.overlayCustomTextColor?.let { "#%06X".format(it and 0xFFFFFF) }
                    ?: stringResource(R.string.settings_overlay_custom_color_none),
                leadingIcon = KronoIcons.Status.Doc,
                checked = config.overlayCustomTextColor != null,
                onChange = { enabled ->
                    if (enabled) showOverlayTextColorPicker = true
                    else scope.launch { dataStore.updateConfig(config.copy(overlayCustomTextColor = null)) }
                }
            )
            SettingsDivider()
            ToggleRow(
                label = stringResource(R.string.label_show_hours),
                subtitle = stringResource(R.string.overlay_show_hours_subtitle),
                leadingTextIcon = "HH",
                checked = config.showHours,
                onChange = { enabled ->
                    scope.launch {
                        dataStore.updateConfig {
                            it.withUpdatedTimeField(TimeField.HH, enabled)
                        }
                    }
                }
            )
            SettingsDivider()
            ToggleRow(
                label = stringResource(R.string.label_show_minutes),
                subtitle = stringResource(R.string.overlay_show_minutes_subtitle),
                leadingTextIcon = "MM",
                checked = config.showMinutes,
                onChange = { enabled ->
                    scope.launch {
                        dataStore.updateConfig {
                            it.withUpdatedTimeField(TimeField.MM, enabled)
                        }
                    }
                }
            )
            SettingsDivider()
            ToggleRow(
                label = stringResource(R.string.label_show_seconds),
                subtitle = stringResource(R.string.overlay_show_seconds_subtitle),
                leadingTextIcon = "SS",
                checked = config.showSeconds,
                onChange = { enabled ->
                    scope.launch {
                        dataStore.updateConfig {
                            it.withUpdatedTimeField(TimeField.SS, enabled)
                        }
                    }
                }
            )
            SettingsDivider()
            ToggleRow(
                label = stringResource(R.string.label_show_milliseconds),
                subtitle = stringResource(R.string.overlay_show_milliseconds_subtitle),
                leadingTextIcon = "MS",
                checked = config.showMilliseconds,
                onChange = { enabled ->
                    scope.launch {
                        dataStore.updateConfig {
                            it.withUpdatedTimeField(TimeField.MS, enabled)
                        }
                    }
                }
            )
        }
    }

    if (showOverlayColorPicker) {
        ColorPickerDialog(
            title = stringResource(R.string.settings_overlay_custom_color),
            initialColor = Color(config.overlayCustomColor ?: Color.White.toArgb()),
            initialOpacity = 1f,
            onPreview = { _, _ -> },
            onConfirm = { color, _ ->
                scope.launch { dataStore.updateConfig(config.copy(overlayCustomColor = color.toArgb())) }
                showOverlayColorPicker = false
            },
            onDismiss = { showOverlayColorPicker = false }
        )
    }

    if (showOverlayTextColorPicker) {
        ColorPickerDialog(
            title = stringResource(R.string.settings_overlay_custom_text_color),
            initialColor = Color(config.overlayCustomTextColor ?: Color.Black.toArgb()),
            initialOpacity = 1f,
            onPreview = { _, _ -> },
            onConfirm = { color, _ ->
                scope.launch { dataStore.updateConfig(config.copy(overlayCustomTextColor = color.toArgb())) }
                showOverlayTextColorPicker = false
            },
            onDismiss = { showOverlayTextColorPicker = false }
        )
    }
}

private enum class TimeField { HH, MM, SS, MS }
private val OrderedTimeFields = listOf(TimeField.HH, TimeField.MM, TimeField.SS, TimeField.MS)

private fun OverlayConfig.withUpdatedTimeField(field: TimeField, enabled: Boolean): OverlayConfig {
    val active = activeTimeFields().toMutableSet()

    if (enabled) {
        active += field
        val normalized = fillTimeFieldGaps(active)
        return copyWithTimeFields(normalized)
    } else {
        if (active.size == 1) return this

        val updated = active.toMutableSet().apply { remove(field) }
        if (!isContinuousTimeFieldBlock(updated)) return this
        return copyWithTimeFields(updated)
    }
}

private fun OverlayConfig.activeTimeFields(): Set<TimeField> = buildSet {
    if (showHours) add(TimeField.HH)
    if (showMinutes) add(TimeField.MM)
    if (showSeconds) add(TimeField.SS)
    if (showMilliseconds) add(TimeField.MS)
}

private fun OverlayConfig.copyWithTimeFields(active: Set<TimeField>): OverlayConfig = copy(
    showHours = TimeField.HH in active,
    showMinutes = TimeField.MM in active,
    showSeconds = TimeField.SS in active,
    showMilliseconds = TimeField.MS in active
)

private fun fillTimeFieldGaps(active: Set<TimeField>): Set<TimeField> {
    if (active.isEmpty()) return setOf(TimeField.SS)
    val indexes = active.map(OrderedTimeFields::indexOf).sorted()
    val start = indexes.first()
    val end = indexes.last()
    return OrderedTimeFields.subList(start, end + 1).toSet()
}

private fun isContinuousTimeFieldBlock(active: Set<TimeField>): Boolean {
    if (active.isEmpty()) return false
    val indexes = active.map(OrderedTimeFields::indexOf).sorted()
    return indexes.zipWithNext().all { (left, right) -> right == left + 1 }
}
