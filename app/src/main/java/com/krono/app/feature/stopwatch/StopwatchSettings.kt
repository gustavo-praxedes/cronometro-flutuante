package com.krono.app.feature.stopwatch

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.krono.app.core.data.OverlayDataStore
import com.krono.app.core.ui.settings.ToolEmptySettingsPanel
import com.krono.app.core.ui.theme.KronoIcons

@Composable
fun StopwatchSettings(dataStore: OverlayDataStore, modifier: Modifier = Modifier) {
    ToolEmptySettingsPanel(
        icon = KronoIcons.Feature.Timer,
        titleRes = com.krono.app.R.string.stopwatch_settings_empty_title,
        subtitleRes = com.krono.app.R.string.stopwatch_settings_empty_subtitle,
        modifier = modifier
    )
}
