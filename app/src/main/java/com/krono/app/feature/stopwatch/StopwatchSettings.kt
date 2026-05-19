package com.krono.app.feature.stopwatch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.krono.app.core.data.OverlayConfig
import com.krono.app.core.data.OverlayDataStore
import com.krono.app.core.ui.settings.OverlayToolSettingsSection
import com.krono.app.core.ui.settings.SettingsPanelLayout
import kotlinx.coroutines.launch

@Composable
fun StopwatchSettings(dataStore: OverlayDataStore, modifier: Modifier = Modifier) {
    val config = dataStore.configFlow.collectAsState(initial = OverlayConfig()).value
    val scope = rememberCoroutineScope()

    SettingsPanelLayout(modifier = modifier) {
        OverlayToolSettingsSection(
            showButtons = config.stopwatchOverlayShowButtons,
            showHours = config.stopwatchOverlayShowHours,
            showSeconds = config.stopwatchOverlayShowSeconds,
            scale = config.stopwatchOverlayScale,
            cornerRadius = config.stopwatchOverlayCornerRadius,
            customColor = config.stopwatchOverlayCustomColor,
            customTextColor = config.stopwatchOverlayCustomTextColor,
            onShowButtonsChange = { v -> scope.launch { dataStore.updateConfig(config.copy(stopwatchOverlayShowButtons = v)) } },
            onShowHoursChange = { v -> scope.launch { dataStore.updateConfig(config.copy(stopwatchOverlayShowHours = v)) } },
            onShowSecondsChange = { v -> scope.launch { dataStore.updateConfig(config.copy(stopwatchOverlayShowSeconds = v)) } },
            onScaleChange = { v -> scope.launch { dataStore.updateConfig(config.copy(stopwatchOverlayScale = v)) } },
            onCornerRadiusChange = { v -> scope.launch { dataStore.updateConfig(config.copy(stopwatchOverlayCornerRadius = v)) } },
            onCustomColorChange = { v -> scope.launch { dataStore.updateConfig(config.copy(stopwatchOverlayCustomColor = v)) } },
            onCustomTextColorChange = { v -> scope.launch { dataStore.updateConfig(config.copy(stopwatchOverlayCustomTextColor = v)) } }
        )
    }
}
