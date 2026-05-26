package com.krono.app.feature.countdown

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.krono.app.core.data.OverlayDataStore
import com.krono.app.core.ui.settings.ToolEmptySettingsPanel
import com.krono.app.core.ui.theme.KronoIcons

@Composable
fun CountdownSettings(dataStore: OverlayDataStore, modifier: Modifier = Modifier) {
    ToolEmptySettingsPanel(icon = KronoIcons.Feature.Countdown, modifier = modifier)
}
