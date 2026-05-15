package com.krono.app.core.ui.settings

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.krono.app.R
import com.krono.app.core.data.OverlayConfig
import com.krono.app.core.data.OverlayDataStore
import com.krono.app.core.ui.theme.KronoThemeOption
import com.krono.app.core.ui.theme.KronoTokens
import com.krono.app.core.ui.theme.overlayColorsForTheme
import com.krono.app.core.ui.dialogs.ColorPickerDialog
import com.krono.app.core.ui.components.AppearanceSlider
import com.krono.app.core.ui.components.KronoDropdown
import com.krono.app.core.ui.components.ThemeSelector
import com.krono.app.core.ui.components.ToggleRow
import com.krono.app.core.ui.theme.KronoFontOption
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = KronoTokens.Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.lg)
    ) {
        Spacer(Modifier.height(KronoTokens.Spacing.sm))

        SettingsGroup(title = stringResource(R.string.settings_group_theme)) {
            ThemeSelector(
                selectedTheme = config.selectedTheme,
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

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                thickness = 0.5.dp
            )

            val fontOptions = KronoFontOption.entries
            KronoDropdown(
                value = config.overlayFontFamily,
                onValueChange = { value ->
                    scope.launch {
                        dataStore.updateConfig(config.copy(overlayFontFamily = value))
                    }
                },
                options = fontOptions.map { it.name },
                label = stringResource(R.string.settings_overlay_font_label),
                modifier = Modifier.padding(horizontal = KronoTokens.Spacing.md, vertical = KronoTokens.Spacing.sm),
                textMapping = { key -> fontOptions.firstOrNull { it.name == key }?.label ?: key }
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                thickness = 0.5.dp
            )

            val languageOptions = listOf("pt-BR")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = KronoTokens.Spacing.md, vertical = KronoTokens.Spacing.sm),
                verticalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.sm)
            ) {
                KronoDropdown(
                    value = config.appLanguage,
                    onValueChange = { locale ->
                        scope.launch { dataStore.updateConfig(config.copy(appLanguage = locale)) }
                    },
                    options = languageOptions,
                    label = stringResource(R.string.settings_language_label),
                    textMapping = { tag ->
                        when (tag) {
                            "pt-BR" -> langPtBr
                            else -> tag
                        }
                    }
                )
            }
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                thickness = 0.5.dp
            )
            ToggleRow(
                label = stringResource(R.string.behavior_play_pause_sound_label),
                subtitle = stringResource(R.string.behavior_play_pause_sound_subtitle),
                checked = config.playPauseSoundEnabled,
                onChange = {
                    scope.launch { dataStore.updateConfig(config.copy(playPauseSoundEnabled = it)) }
                }
            )
            GroupDivider()
            ToggleRow(
                label = stringResource(R.string.behavior_play_pause_vibration_label),
                subtitle = stringResource(R.string.behavior_play_pause_vibration_subtitle),
                checked = config.playPauseVibrationEnabled,
                onChange = {
                    scope.launch { dataStore.updateConfig(config.copy(playPauseVibrationEnabled = it)) }
                }
            )
            GroupDivider()
            AppearanceSlider(
                label = stringResource(R.string.settings_volume_play_pause),
                value = config.playPauseVolume,
                minLabel = "0%",
                maxLabel = "100%",
                range = 0f..1f,
                display = "${(config.playPauseVolume * 100).toInt()}%",
                onChange = { scope.launch { dataStore.updateConfig(config.copy(playPauseVolume = it)) } }
            )
            GroupDivider()
            AppearanceSlider(
                label = stringResource(R.string.settings_volume_tick),
                value = config.tickVolume,
                minLabel = "0%",
                maxLabel = "100%",
                range = 0f..1f,
                display = "${(config.tickVolume * 100).toInt()}%",
                onChange = { scope.launch { dataStore.updateConfig(config.copy(tickVolume = it)) } }
            )
            GroupDivider()
            AppearanceSlider(
                label = stringResource(R.string.settings_volume_focus_alert),
                value = config.focusAlertVolume,
                minLabel = "0%",
                maxLabel = "100%",
                range = 0f..1f,
                display = "${(config.focusAlertVolume * 100).toInt()}%",
                onChange = { scope.launch { dataStore.updateConfig(config.copy(focusAlertVolume = it)) } }
            )
            GroupDivider()
            AppearanceSlider(
                label = stringResource(R.string.settings_volume_break_alert),
                value = config.breakAlertVolume,
                minLabel = "0%",
                maxLabel = "100%",
                range = 0f..1f,
                display = "${(config.breakAlertVolume * 100).toInt()}%",
                onChange = { scope.launch { dataStore.updateConfig(config.copy(breakAlertVolume = it)) } }
            )
        }

        Spacer(Modifier.height(KronoTokens.Spacing.xxl))
    }
}

@Composable
private fun GroupDivider() {
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
        thickness = 0.5.dp
    )
}
