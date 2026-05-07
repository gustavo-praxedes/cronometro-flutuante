package com.krono.app.feature.stopwatch

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.krono.app.R
import com.krono.app.core.data.OverlayConfig
import com.krono.app.core.data.OverlayDataStore
import com.krono.app.core.ui.components.ToggleRow
import kotlinx.coroutines.launch

@Composable
fun StopwatchSettings(
    dataStore: OverlayDataStore,
    isServiceRunning: () -> Boolean,
    onStartFocusMode: () -> Unit
) {
    val config = dataStore.configFlow.collectAsState(initial = OverlayConfig()).value
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ToggleRow(stringResource(R.string.label_auto_launch), config.autoLaunch) {
            scope.launch { dataStore.updateConfig(config.copy(autoLaunch = it)) }
        }
        ToggleRow(stringResource(R.string.label_show_hours), config.showHours) {
            if (!it && !config.showSeconds) return@ToggleRow
            scope.launch { dataStore.updateConfig(config.copy(showHours = it)) }
        }
        ToggleRow(stringResource(R.string.label_show_seconds), config.showSeconds) {
            if (!it && !config.showHours) return@ToggleRow
            scope.launch { dataStore.updateConfig(config.copy(showSeconds = it)) }
        }
        ToggleRow(stringResource(R.string.label_show_buttons), config.showButtons) {
            scope.launch { dataStore.updateConfig(config.copy(showButtons = it)) }
        }
        ToggleRow(stringResource(R.string.label_wake_lock), config.keepScreenOn) {
            scope.launch { dataStore.updateConfig(config.copy(keepScreenOn = it)) }
        }
        ToggleRow(stringResource(R.string.label_focus_mode), config.focusModeEnabled) { isEnabled ->
            scope.launch { dataStore.updateConfig(config.copy(focusModeEnabled = isEnabled)) }
            if (isEnabled && isServiceRunning()) onStartFocusMode()
        }
        ToggleRow(stringResource(R.string.label_beep_enabled), config.isBeepEnabled) {
            scope.launch { dataStore.updateConfig(config.copy(isBeepEnabled = it)) }
        }
        ToggleRow(stringResource(R.string.label_vibration_enabled), config.isVibrationEnabled) {
            scope.launch { dataStore.updateConfig(config.copy(isVibrationEnabled = it)) }
        }

        Spacer(Modifier.height(16.dp))

        TimeLimitField(
            timeLimitSeconds = config.timeLimitSeconds,
            onConfirm = { seconds ->
                scope.launch { dataStore.updateConfig(config.copy(timeLimitSeconds = seconds)) }
            }
        )
    }
}
