package com.krono.app.feature.pomodoro

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.krono.app.R
import com.krono.app.core.data.OverlayConfig
import com.krono.app.core.ui.components.AppearanceSlider
import com.krono.app.core.ui.components.SettingsDivider
import com.krono.app.core.ui.components.ToggleRow
import com.krono.app.core.ui.settings.SettingsGroup
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoTokens

@Composable
internal fun PomodoroBehaviorSettingsSection(
    config: OverlayConfig,
    onAutoNextCycleChange: (Boolean) -> Unit,
    onDndChange: (Boolean) -> Unit,
    onDailyGoalChange: (Int) -> Unit
) {
    SettingsGroup(title = stringResource(R.string.settings_group_behavior)) {
        ToggleRow(
            label = stringResource(R.string.pomodoro_auto_next_cycle_label),
            subtitle = stringResource(R.string.pomodoro_auto_next_cycle_subtitle),
            leadingIcon = KronoIcons.Action.Autorenew,
            checked = config.pomodoroAutoNextCycle,
            onChange = onAutoNextCycleChange
        )
        SettingsDivider()
        ToggleRow(
            label = stringResource(R.string.pomodoro_dnd_label),
            subtitle = stringResource(R.string.pomodoro_dnd_subtitle),
            leadingIcon = KronoIcons.Action.Focus,
            checked = config.pomodoroDndDuringFocus,
            onChange = onDndChange
        )
        SettingsDivider()
        AppearanceSlider(
            label = stringResource(R.string.pomodoro_daily_goal_label),
            value = config.pomodoroDailyGoalCycles.toFloat(),
            minLabel = stringResource(R.string.settings_value_one),
            maxLabel = stringResource(R.string.settings_value_twenty_four),
            range = 1f..24f,
            display = config.pomodoroDailyGoalCycles.toString(),
            onChange = { value -> onDailyGoalChange(value.toInt().coerceIn(1, 24)) },
            modifier = Modifier.padding(
                horizontal = KronoTokens.Settings.panelHorizontalInset,
                vertical = KronoTokens.Spacing.sm
            )
        )
    }
}
