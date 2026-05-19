package com.krono.app.core.ui.settings

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.krono.app.R
import com.krono.app.core.data.OverlayConfig
import com.krono.app.core.data.OverlayDataStore
import com.krono.app.core.ui.components.AppearanceSlider
import com.krono.app.core.ui.components.SettingsDivider
import com.krono.app.core.ui.theme.KronoThemeOption
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoTokens
import com.krono.app.core.ui.theme.overlayColorsForTheme
import com.krono.app.core.ui.components.FontSelector
import com.krono.app.core.ui.components.KronoDropdown
import com.krono.app.core.ui.components.ThemeSelector
import com.krono.app.core.ui.components.ToggleRow
import kotlinx.coroutines.launch

@Composable
fun AppearancePanel(
    dataStore: OverlayDataStore,
    modifier: Modifier = Modifier
) {
    val config = dataStore.configFlow.collectAsState(initial = OverlayConfig()).value
    val scope = rememberCoroutineScope()
    val systemIsDark = isSystemInDarkTheme()

    val langPtBr = stringResource(R.string.settings_language_pt_br)
    val volumeMinLabel = stringResource(R.string.settings_volume_min)
    val volumeMaxLabel = stringResource(R.string.settings_volume_max)

    SettingsPanelLayout(modifier = modifier) {
        SettingsGroup(title = stringResource(R.string.settings_group_theme)) {
            ThemeSelector(
                selectedTheme = config.selectedTheme,
                leadingIcon = KronoIcons.Action.Palette,
                onChange = { theme ->
                    scope.launch {
                        val option = KronoThemeOption.entries.find { it.name == theme }
                            ?: KronoThemeOption.AUTO
                        val (bgColor, txtColor) = overlayColorsForTheme(option, systemIsDark)
                        dataStore.updateConfig(
                            config.copy(
                                selectedTheme = theme,
                                backgroundColor = bgColor,
                                textColor = txtColor
                            )
                        )
                    }
                }
            )
            SettingsDivider()

            FontSelector(
                selectedFont = config.overlayFontFamily,
                onChange = { value ->
                    scope.launch { dataStore.updateConfig(config.copy(overlayFontFamily = value)) }
                },
                leadingIcon = KronoIcons.Action.TypeSpecimen
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

        SettingsGroup(title = stringResource(R.string.settings_group_sounds_vibration)) {
            ToggleRow(
                label = stringResource(R.string.behavior_play_pause_vibration_label),
                subtitle = stringResource(R.string.behavior_play_pause_vibration_subtitle),
                checked = config.playPauseVibrationEnabled,
                leadingIcon = KronoIcons.Action.MobileVibrate,
                onChange = { scope.launch { dataStore.updateConfig(config.copy(playPauseVibrationEnabled = it)) } }
            )
            SettingsDivider()
            ToggleRow(
                label = stringResource(R.string.behavior_play_pause_sound_label),
                subtitle = stringResource(R.string.behavior_play_pause_sound_subtitle),
                checked = config.playPauseSoundEnabled,
                leadingIcon = KronoIcons.Action.NotificationSound,
                onChange = { scope.launch { dataStore.updateConfig(config.copy(playPauseSoundEnabled = it)) } }
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
                modifier = Modifier.padding(
                    horizontal = KronoTokens.Settings.panelHorizontalInset,
                    vertical = KronoTokens.Spacing.sm
                )
            )
        }
    }
}
