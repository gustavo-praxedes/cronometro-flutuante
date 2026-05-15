package com.krono.app.core.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.krono.app.R
import com.krono.app.core.data.OverlayConfig
import com.krono.app.core.data.OverlayDataStore
import com.krono.app.core.ui.theme.KronoTokens
import com.krono.app.core.ui.components.AppearanceSlider
import com.krono.app.core.ui.components.FontSelector
import com.krono.app.core.ui.components.ToggleRow
import kotlinx.coroutines.launch
import java.util.Locale
import androidx.compose.ui.unit.dp

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
            .padding(horizontal = KronoTokens.Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.lg)
    ) {
        Spacer(Modifier.height(KronoTokens.Spacing.sm))

        SettingsGroup(title = stringResource(R.string.settings_overlay)) {
            Column(modifier = Modifier.padding(KronoTokens.Spacing.lg)) {
                ToggleRow(
                    label = stringResource(R.string.label_show_buttons),
                    subtitle = stringResource(R.string.overlay_show_buttons_subtitle),
                    checked = config.showButtons,
                    onChange = {
                        scope.launch { dataStore.updateConfig(config.copy(showButtons = it)) }
                    }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = KronoTokens.Spacing.md),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                    thickness = 0.5.dp
                )

                ToggleRow(
                    label = stringResource(R.string.label_show_hours),
                    subtitle = stringResource(R.string.overlay_show_hours_subtitle),
                    checked = config.showHours,
                    onChange = {
                        if (!it && !config.showSeconds) return@ToggleRow
                        scope.launch { dataStore.updateConfig(config.copy(showHours = it)) }
                    }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = KronoTokens.Spacing.md),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                    thickness = 0.5.dp
                )

                ToggleRow(
                    label = stringResource(R.string.label_show_seconds),
                    subtitle = stringResource(R.string.overlay_show_seconds_subtitle),
                    checked = config.showSeconds,
                    onChange = {
                        if (!it && !config.showHours) return@ToggleRow
                        scope.launch { dataStore.updateConfig(config.copy(showSeconds = it)) }
                    }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = KronoTokens.Spacing.md),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                    thickness = 0.5.dp
                )

                AppearanceSlider(
                    label = stringResource(R.string.label_scale),
                    value = config.scale,
                    minLabel = "0.5×",
                    maxLabel = "1.5×",
                    range = 0.5f..1.5f,
                    display = String.format(Locale.US, "%.1f×", config.scale),
                    onChange = {
                        scope.launch { dataStore.updateConfig(config.copy(scale = it)) }
                    }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = KronoTokens.Spacing.md),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                    thickness = 0.5.dp
                )

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

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = KronoTokens.Spacing.md),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                    thickness = 0.5.dp
                )

                FontSelector(
                    selectedFont = config.selectedFont,
                    onChange = { font ->
                        scope.launch { dataStore.updateConfig(config.copy(selectedFont = font)) }
                    }
                )
            }
        }

        Spacer(Modifier.height(KronoTokens.Spacing.xxl))
    }
}

