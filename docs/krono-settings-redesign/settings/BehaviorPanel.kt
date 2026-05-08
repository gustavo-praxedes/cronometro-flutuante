package com.krono.app.core.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.krono.app.R
import com.krono.app.core.data.OverlayConfig
import com.krono.app.core.data.OverlayDataStore
import com.krono.app.core.ui.theme.KronoTokens
import com.krono.app.core.ui.components.ToggleRow
import com.krono.app.feature.stopwatch.TimeLimitField
import kotlinx.coroutines.launch

@Composable
fun BehaviorPanel(
    dataStore: OverlayDataStore,
    isServiceRunning: () -> Boolean,
    onStartFocusMode: () -> Unit,
    modifier: Modifier = Modifier
) {
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

        SettingsGroup(title = stringResource(R.string.settings_group_general)) {
            ToggleRow(
                label = stringResource(R.string.label_auto_launch),
                checked = config.autoLaunch,
                onChange = {
                    scope.launch { dataStore.updateConfig(config.copy(autoLaunch = it)) }
                }
            )

            GroupDivider()

            ToggleRow(
                label = stringResource(R.string.label_show_hours),
                checked = config.showHours,
                onChange = {
                    if (!it && !config.showSeconds) return@ToggleRow
                    scope.launch { dataStore.updateConfig(config.copy(showHours = it)) }
                }
            )

            GroupDivider()

            ToggleRow(
                label = stringResource(R.string.label_show_seconds),
                checked = config.showSeconds,
                onChange = {
                    if (!it && !config.showHours) return@ToggleRow
                    scope.launch { dataStore.updateConfig(config.copy(showSeconds = it)) }
                }
            )

            GroupDivider()

            ToggleRow(
                label = stringResource(R.string.label_show_buttons),
                checked = config.showButtons,
                onChange = {
                    scope.launch { dataStore.updateConfig(config.copy(showButtons = it)) }
                }
            )

            GroupDivider()

            ToggleRow(
                label = stringResource(R.string.label_wake_lock),
                checked = config.keepScreenOn,
                onChange = {
                    scope.launch { dataStore.updateConfig(config.copy(keepScreenOn = it)) }
                }
            )

            GroupDivider()

            ToggleRow(
                label = stringResource(R.string.label_focus_mode),
                checked = config.focusModeEnabled,
                onChange = { isEnabled ->
                    scope.launch { dataStore.updateConfig(config.copy(focusModeEnabled = isEnabled)) }
                    if (isEnabled && isServiceRunning()) onStartFocusMode()
                }
            )

            GroupDivider()

            ToggleRow(
                label = stringResource(R.string.label_beep_enabled),
                checked = config.isBeepEnabled,
                onChange = {
                    scope.launch { dataStore.updateConfig(config.copy(isBeepEnabled = it)) }
                }
            )

            GroupDivider()

            ToggleRow(
                label = stringResource(R.string.label_vibration_enabled),
                checked = config.isVibrationEnabled,
                onChange = {
                    scope.launch { dataStore.updateConfig(config.copy(isVibrationEnabled = it)) }
                }
            )
        }

        SettingsGroup(title = stringResource(R.string.settings_group_time_limit)) {
            TimeLimitField(
                timeLimitSeconds = config.timeLimitSeconds,
                onConfirm = { seconds ->
                    scope.launch { dataStore.updateConfig(config.copy(timeLimitSeconds = seconds)) }
                }
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
