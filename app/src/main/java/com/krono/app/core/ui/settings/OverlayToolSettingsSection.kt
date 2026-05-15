package com.krono.app.core.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.krono.app.R
import com.krono.app.core.ui.components.AppearanceSlider
import com.krono.app.core.ui.components.ToggleRow
import com.krono.app.core.ui.dialogs.ColorPickerDialog
import com.krono.app.core.ui.theme.KronoTokens

@Composable
fun OverlayToolSettingsSection(
    showButtons: Boolean,
    showHours: Boolean,
    showSeconds: Boolean,
    scale: Float,
    cornerRadius: Float,
    customColor: Int?,
    onShowButtonsChange: (Boolean) -> Unit,
    onShowHoursChange: (Boolean) -> Unit,
    onShowSecondsChange: (Boolean) -> Unit,
    onScaleChange: (Float) -> Unit,
    onCornerRadiusChange: (Float) -> Unit,
    onCustomColorChange: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showColorPicker by remember { mutableStateOf(false) }

    SettingsGroup(title = stringResource(R.string.settings_overlay)) {
        Column(modifier = modifier.padding(KronoTokens.Spacing.lg)) {
            ToggleRow(
                label = stringResource(R.string.label_show_buttons),
                subtitle = stringResource(R.string.overlay_show_buttons_subtitle),
                checked = showButtons,
                onChange = onShowButtonsChange
            )
            GroupDivider()
            ToggleRow(
                label = stringResource(R.string.label_show_hours),
                subtitle = stringResource(R.string.overlay_show_hours_subtitle),
                checked = showHours,
                onChange = {
                    if (!it && !showSeconds) return@ToggleRow
                    onShowHoursChange(it)
                }
            )
            GroupDivider()
            ToggleRow(
                label = stringResource(R.string.label_show_seconds),
                subtitle = stringResource(R.string.overlay_show_seconds_subtitle),
                checked = showSeconds,
                onChange = {
                    if (!it && !showHours) return@ToggleRow
                    onShowSecondsChange(it)
                }
            )
            GroupDivider()
            AppearanceSlider(
                label = stringResource(R.string.label_scale),
                value = scale,
                minLabel = "0.5x",
                maxLabel = "1.5x",
                range = 0.5f..1.5f,
                display = "%.1fx".format(scale),
                onChange = onScaleChange
            )
            GroupDivider()
            AppearanceSlider(
                label = stringResource(R.string.label_corner_radius),
                value = cornerRadius,
                minLabel = "0dp",
                maxLabel = "50dp",
                range = 0f..50f,
                display = "${cornerRadius.toInt()}dp",
                onChange = onCornerRadiusChange
            )
            GroupDivider()
            ToggleRow(
                label = stringResource(R.string.settings_overlay_custom_color),
                subtitle = customColor?.let { "#%06X".format(it and 0xFFFFFF) } ?: stringResource(R.string.settings_overlay_custom_color_none),
                checked = customColor != null,
                onChange = { enabled ->
                    if (enabled) showColorPicker = true else onCustomColorChange(null)
                }
            )
        }
    }

    if (showColorPicker) {
        ColorPickerDialog(
            title = stringResource(R.string.settings_overlay_custom_color),
            initialColor = Color(customColor ?: Color.White.toArgb()),
            initialOpacity = 1f,
            onPreview = { _, _ -> },
            onConfirm = { color, _ ->
                onCustomColorChange(color.toArgb())
                showColorPicker = false
            },
            onDismiss = { showColorPicker = false }
        )
    }
}

@Composable
private fun GroupDivider() {
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
        thickness = 0.5.dp
    )
}
