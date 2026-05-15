package com.krono.app.feature.stopwatch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.krono.app.R
import com.krono.app.core.data.OverlayConfig
import com.krono.app.core.data.OverlayDataStore
import com.krono.app.core.ui.settings.SettingsGroup
import com.krono.app.core.ui.settings.TimeFormatSelector
import com.krono.app.core.ui.settings.OverlayToolSettingsSection
import com.krono.app.core.ui.theme.KronoTokens
import kotlinx.coroutines.launch

@Composable
fun StopwatchSettings(dataStore: OverlayDataStore, modifier: Modifier = Modifier) {
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
        SettingsGroup(title = stringResource(R.string.settings_stopwatch)) {
            TimeFormatSelector(
                selected = config.stopwatchFormat,
                onChange = { value ->
                    scope.launch { dataStore.updateConfig(config.copy(stopwatchFormat = value)) }
                }
            )
        }
        OverlayToolSettingsSection(
            showButtons = config.stopwatchOverlayShowButtons,
            showHours = config.stopwatchOverlayShowHours,
            showSeconds = config.stopwatchOverlayShowSeconds,
            scale = config.stopwatchOverlayScale,
            cornerRadius = config.stopwatchOverlayCornerRadius,
            customColor = config.stopwatchOverlayCustomColor,
            onShowButtonsChange = { v -> scope.launch { dataStore.updateConfig(config.copy(stopwatchOverlayShowButtons = v)) } },
            onShowHoursChange = { v -> scope.launch { dataStore.updateConfig(config.copy(stopwatchOverlayShowHours = v)) } },
            onShowSecondsChange = { v -> scope.launch { dataStore.updateConfig(config.copy(stopwatchOverlayShowSeconds = v)) } },
            onScaleChange = { v -> scope.launch { dataStore.updateConfig(config.copy(stopwatchOverlayScale = v)) } },
            onCornerRadiusChange = { v -> scope.launch { dataStore.updateConfig(config.copy(stopwatchOverlayCornerRadius = v)) } },
            onCustomColorChange = { v -> scope.launch { dataStore.updateConfig(config.copy(stopwatchOverlayCustomColor = v)) } }
        )
        Spacer(Modifier.height(24.dp))
    }
}
