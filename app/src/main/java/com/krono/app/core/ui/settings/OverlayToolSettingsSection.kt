package com.krono.app.core.ui.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import com.krono.app.R
import com.krono.app.core.ui.components.AppearanceSlider
import com.krono.app.core.ui.components.SettingsDivider
import com.krono.app.core.ui.components.ToggleRow
import com.krono.app.core.ui.dialogs.ColorPickerDialog
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoTokens

@Composable
fun OverlayToolSettingsSection(
    showButtons: Boolean,
    showHours: Boolean,
    showSeconds: Boolean,
    scale: Float,
    cornerRadius: Float,
    customColor: Int?,
    customTextColor: Int?,
    onShowButtonsChange: (Boolean) -> Unit,
    onShowHoursChange: (Boolean) -> Unit,
    onShowSecondsChange: (Boolean) -> Unit,
    onScaleChange: (Float) -> Unit,
    onCornerRadiusChange: (Float) -> Unit,
    onCustomColorChange: (Int?) -> Unit,
    onCustomTextColorChange: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showColorPicker by remember { mutableStateOf(false) }
    var showTextColorPicker by remember { mutableStateOf(false) }
    val overlayScaleMin = stringResource(R.string.settings_overlay_scale_min)
    val overlayScaleMax = stringResource(R.string.settings_overlay_scale_max)
    val overlayCornerMin = stringResource(R.string.settings_overlay_corner_min)
    val overlayCornerMax = stringResource(R.string.settings_overlay_corner_max)

    SettingsGroup(title = stringResource(R.string.settings_overlay)) {
        ToggleRow(
            label = stringResource(R.string.label_show_buttons),
            subtitle = stringResource(R.string.overlay_show_buttons_subtitle),
            leadingIcon = KronoIcons.Feature.Overlay,
            checked = showButtons,
            onChange = onShowButtonsChange
        )
        SettingsDivider()
        ToggleRow(
            label = stringResource(R.string.label_show_hours),
            subtitle = stringResource(R.string.overlay_show_hours_subtitle),
            leadingTextIcon = "HH",
            checked = showHours,
            onChange = {
                if (!it && !showSeconds) return@ToggleRow
                onShowHoursChange(it)
            }
        )
        SettingsDivider()
        ToggleRow(
            label = stringResource(R.string.label_show_seconds),
            subtitle = stringResource(R.string.overlay_show_seconds_subtitle),
            leadingTextIcon = "SS",
            checked = showSeconds,
            onChange = {
                if (!it && !showHours) return@ToggleRow
                onShowSecondsChange(it)
            }
        )
        SettingsDivider()
        AppearanceSlider(
            label = stringResource(R.string.label_scale),
            value = scale,
            minLabel = overlayScaleMin,
            maxLabel = overlayScaleMax,
            range = 0.5f..1.5f,
            display = "%.1fx".format(scale),
            onChange = onScaleChange,
            modifier = Modifier.padding(
                horizontal = KronoTokens.Settings.panelHorizontalInset,
                vertical = KronoTokens.Spacing.sm
            )
        )
        SettingsDivider()
        AppearanceSlider(
            label = stringResource(R.string.label_corner_radius),
            value = cornerRadius,
            minLabel = overlayCornerMin,
            maxLabel = overlayCornerMax,
            range = 0f..50f,
            display = "${cornerRadius.toInt()}dp",
            onChange = onCornerRadiusChange,
            modifier = Modifier.padding(
                horizontal = KronoTokens.Settings.panelHorizontalInset,
                vertical = KronoTokens.Spacing.sm
            )
        )
        SettingsDivider()
        ToggleRow(
            label = stringResource(R.string.settings_overlay_custom_color),
            subtitle = customColor?.let { "#%06X".format(it and 0xFFFFFF) } ?: stringResource(R.string.settings_overlay_custom_color_none),
            leadingIcon = KronoIcons.Action.FormatPaint,
            checked = customColor != null,
            onChange = { enabled ->
                if (enabled) showColorPicker = true else onCustomColorChange(null)
            }
        )
        if (customColor != null) {
            SettingsDivider()
            ToggleRow(
                label = stringResource(R.string.settings_overlay_custom_text_color),
                subtitle = customTextColor?.let { "#%06X".format(it and 0xFFFFFF) } ?: stringResource(R.string.settings_overlay_custom_color_none),
                leadingIcon = KronoIcons.Status.Doc,
                checked = customTextColor != null,
                onChange = { enabled ->
                    if (enabled) showTextColorPicker = true else onCustomTextColorChange(null)
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
    if (showTextColorPicker) {
        ColorPickerDialog(
            title = stringResource(R.string.settings_overlay_custom_text_color),
            initialColor = Color(customTextColor ?: Color.Black.toArgb()),
            initialOpacity = 1f,
            onPreview = { _, _ -> },
            onConfirm = { color, _ ->
                onCustomTextColorChange(color.toArgb())
                showTextColorPicker = false
            },
            onDismiss = { showTextColorPicker = false }
        )
    }
}
