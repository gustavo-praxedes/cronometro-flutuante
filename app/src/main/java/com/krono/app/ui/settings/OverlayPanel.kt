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
import com.krono.app.ui.AppearanceSlider
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun OverlayPanel(
    dataStore: OverlayDataStore,
    modifier: Modifier = Modifier
) {
    val config = dataStore.configFlow.collectAsState(initial = OverlayConfig()).value
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = KronoTokens.Spacing.xxl),
        verticalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.lg)
    ) {
        Spacer(modifier = Modifier.height(KronoTokens.Spacing.lg))

        AppearanceSlider(
            label = stringResource(R.string.label_scale),
            value = config.scale,
            minLabel = "0.5x",
            maxLabel = "1.5x",
            range = 0.5f..1.5f,
            display = String.format(Locale.US, "%.1fx", config.scale),
            onChange = {
                scope.launch { dataStore.updateConfig(config.copy(scale = it)) }
            }
        )

        Spacer(modifier = Modifier.height(KronoTokens.Spacing.md))

        AppearanceSlider(
            label = stringResource(R.string.label_corner_radius),
            value = config.cornerRadius,
            minLabel = "0dp",
            maxLabel = "50dp",
            range = 0f..50f,
            display = "${config.cornerRadius.toInt()}dp",
            onChange = {
                scope.launch { dataStore.updateConfig(config.copy(cornerRadius = it)) }
            }
        )

        Spacer(modifier = Modifier.height(KronoTokens.Spacing.xxl))
    }
}