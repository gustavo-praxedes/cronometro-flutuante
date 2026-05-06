package com.krono.app.ui.settings

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
import com.krono.app.ui.ToggleRow
import com.krono.app.ui.TimeLimitField
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
            .padding(horizontal = KronoTokens.Spacing.xxl),
        verticalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.xs + 2.dp)
    ) {
        Spacer(modifier = Modifier.height(KronoTokens.Spacing.lg))

        ToggleRow(
            label = stringResource(R.string.label_auto_launch),
            checked = config.autoLaunch,
            onChange = {
                scope.launch { dataStore.updateConfig(config.copy(autoLaunch = it)) }
            }
        )

        ToggleRow(
            label = stringResource(R.string.label_show_hours),
            checked = config.showHours,
            onChange = {
                if (!it && !config.showSeconds) return@ToggleRow
                scope.launch { dataStore.updateConfig(config.copy(showHours = it)) }
            }
        )

        ToggleRow(
            label = stringResource(R.string.label_show_seconds),
            checked = config.showSeconds,
            onChange = {
                if (!it && !config.showHours) return@ToggleRow
                scope.launch { dataStore.updateConfig(config.copy(showSeconds = it)) }
            }
        )

        ToggleRow(
            label = stringResource(R.string.label_show_buttons),
            checked = config.showButtons,
            onChange = {
                scope.launch { dataStore.updateConfig(config.copy(showButtons = it)) }
            }
        )

        ToggleRow(
            label = stringResource(R.string.label_wake_lock),
            checked = config.keepScreenOn,
            onChange = {
                scope.launch { dataStore.updateConfig(config.copy(keepScreenOn = it)) }
            }
        )

        ToggleRow(
            label = stringResource(R.string.label_focus_mode),
            checked = config.focusModeEnabled,
            onChange = { isEnabled ->
                scope.launch { dataStore.updateConfig(config.copy(focusModeEnabled = isEnabled)) }
                if (isEnabled && isServiceRunning()) onStartFocusMode()
            }
        )

        ToggleRow(
            label = stringResource(R.string.label_beep_enabled),
            checked = config.isBeepEnabled,
            onChange = {
                scope.launch { dataStore.updateConfig(config.copy(isBeepEnabled = it)) }
            }
        )

        ToggleRow(
            label = stringResource(R.string.label_vibration_enabled),
            checked = config.isVibrationEnabled,
            onChange = {
                scope.launch { dataStore.updateConfig(config.copy(isVibrationEnabled = it)) }
            }
        )

        Spacer(modifier = Modifier.height(KronoTokens.Spacing.lg))

        TimeLimitField(
            timeLimitSeconds = config.timeLimitSeconds,
            onConfirm = { seconds ->
                scope.launch { dataStore.updateConfig(config.copy(timeLimitSeconds = seconds)) }
            }
        )

        Spacer(modifier = Modifier.height(KronoTokens.Spacing.xxl))
    }
}