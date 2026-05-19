package com.krono.app.feature.countdown

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
fun CountdownSettings(dataStore: OverlayDataStore, modifier: Modifier = Modifier) {
    val config = dataStore.configFlow.collectAsState(initial = OverlayConfig()).value
    val scope = rememberCoroutineScope()

    SettingsPanelLayout(modifier = modifier) {
        OverlayToolSettingsSection(
            showButtons = config.countdownOverlayShowButtons,
            showHours = config.countdownOverlayShowHours,
            showSeconds = config.countdownOverlayShowSeconds,
            scale = config.countdownOverlayScale,
            cornerRadius = config.countdownOverlayCornerRadius,
            customColor = config.countdownOverlayCustomColor,
            customTextColor = config.countdownOverlayCustomTextColor,
            onShowButtonsChange = { v -> scope.launch { dataStore.updateConfig(config.copy(countdownOverlayShowButtons = v)) } },
            onShowHoursChange = { v -> scope.launch { dataStore.updateConfig(config.copy(countdownOverlayShowHours = v)) } },
            onShowSecondsChange = { v -> scope.launch { dataStore.updateConfig(config.copy(countdownOverlayShowSeconds = v)) } },
            onScaleChange = { v -> scope.launch { dataStore.updateConfig(config.copy(countdownOverlayScale = v)) } },
            onCornerRadiusChange = { v -> scope.launch { dataStore.updateConfig(config.copy(countdownOverlayCornerRadius = v)) } },
            onCustomColorChange = { v -> scope.launch { dataStore.updateConfig(config.copy(countdownOverlayCustomColor = v)) } },
            onCustomTextColorChange = { v -> scope.launch { dataStore.updateConfig(config.copy(countdownOverlayCustomTextColor = v)) } }
        )
    }
}
