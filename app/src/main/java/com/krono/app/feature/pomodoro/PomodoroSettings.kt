package com.krono.app.feature.pomodoro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.krono.app.R
import com.krono.app.core.data.OverlayConfig
import com.krono.app.core.data.OverlayDataStore
import com.krono.app.core.ui.components.KronoDropdown
import com.krono.app.core.ui.components.ToggleRow
import com.krono.app.core.ui.settings.SettingsGroup
import com.krono.app.core.ui.settings.TimeFormatSelector
import com.krono.app.core.ui.settings.OverlayToolSettingsSection
import com.krono.app.core.ui.theme.KronoTokens
import kotlinx.coroutines.launch

@Composable
fun PomodoroSettings(dataStore: OverlayDataStore, modifier: Modifier = Modifier) {
    val config = dataStore.configFlow.collectAsState(initial = OverlayConfig()).value
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = KronoTokens.Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.lg)
    ) {
        Spacer(Modifier.height(KronoTokens.Spacing.sm))
        SettingsGroup(title = stringResource(R.string.pomodoro_title)) {
            val presetOptions = listOf(
                "CLASSICO" to stringResource(R.string.pomodoro_preset_classic),
                "CURTO" to stringResource(R.string.pomodoro_preset_short),
                "LONGO" to stringResource(R.string.pomodoro_preset_long)
            )
            KronoDropdown(
                value = config.pomodoroPreset,
                onValueChange = { value ->
                    scope.launch { dataStore.updateConfig(config.copy(pomodoroPreset = value)) }
                },
                options = presetOptions.map { it.first },
                label = stringResource(R.string.pomodoro_preset_label),
                modifier = Modifier.padding(horizontal = KronoTokens.Spacing.md, vertical = KronoTokens.Spacing.sm),
                textMapping = { key -> presetOptions.firstOrNull { it.first == key }?.second ?: key }
            )
            GroupDivider()
            TimeFormatSelector(
                selected = config.pomodoroFormat,
                onChange = { value ->
                    scope.launch { dataStore.updateConfig(config.copy(pomodoroFormat = value)) }
                }
            )
            GroupDivider()
            ToggleRow(
                label = stringResource(R.string.pomodoro_auto_break_label),
                subtitle = stringResource(R.string.pomodoro_auto_break_subtitle),
                checked = config.pomodoroAutoStartBreak,
                onChange = { enabled ->
                    scope.launch { dataStore.updateConfig(config.copy(pomodoroAutoStartBreak = enabled)) }
                }
            )
            GroupDivider()
            ToggleRow(
                label = stringResource(R.string.pomodoro_auto_focus_label),
                subtitle = stringResource(R.string.pomodoro_auto_focus_subtitle),
                checked = config.pomodoroAutoStartFocus,
                onChange = { enabled ->
                    scope.launch { dataStore.updateConfig(config.copy(pomodoroAutoStartFocus = enabled)) }
                }
            )
        }

        SettingsGroup(title = stringResource(R.string.pomodoro_group_sounds)) {
            ToggleRow(
                label = stringResource(R.string.pomodoro_beep_label),
                subtitle = stringResource(R.string.pomodoro_beep_subtitle),
                checked = config.pomodoroBeepFocusBreak,
                onChange = { enabled ->
                    scope.launch { dataStore.updateConfig(config.copy(pomodoroBeepFocusBreak = enabled)) }
                }
            )
            GroupDivider()
            ToggleRow(
                label = stringResource(R.string.pomodoro_tick_label),
                subtitle = stringResource(R.string.pomodoro_tick_subtitle),
                checked = config.pomodoroTickingSound,
                onChange = { enabled ->
                    scope.launch { dataStore.updateConfig(config.copy(pomodoroTickingSound = enabled)) }
                }
            )
        }
        OverlayToolSettingsSection(
            showButtons = config.pomodoroOverlayShowButtons,
            showHours = config.pomodoroOverlayShowHours,
            showSeconds = config.pomodoroOverlayShowSeconds,
            scale = config.pomodoroOverlayScale,
            cornerRadius = config.pomodoroOverlayCornerRadius,
            customColor = config.pomodoroOverlayCustomColor,
            onShowButtonsChange = { v -> scope.launch { dataStore.updateConfig(config.copy(pomodoroOverlayShowButtons = v)) } },
            onShowHoursChange = { v -> scope.launch { dataStore.updateConfig(config.copy(pomodoroOverlayShowHours = v)) } },
            onShowSecondsChange = { v -> scope.launch { dataStore.updateConfig(config.copy(pomodoroOverlayShowSeconds = v)) } },
            onScaleChange = { v -> scope.launch { dataStore.updateConfig(config.copy(pomodoroOverlayScale = v)) } },
            onCornerRadiusChange = { v -> scope.launch { dataStore.updateConfig(config.copy(pomodoroOverlayCornerRadius = v)) } },
            onCustomColorChange = { v -> scope.launch { dataStore.updateConfig(config.copy(pomodoroOverlayCustomColor = v)) } }
        )
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
