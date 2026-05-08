package com.krono.app.core.ui.settings

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.krono.app.R
import com.krono.app.core.data.OverlayConfig
import com.krono.app.core.data.OverlayDataStore
import com.krono.app.core.ui.theme.KronoThemeOption
import com.krono.app.core.ui.theme.KronoTokens
import com.krono.app.core.ui.theme.overlayColorsForTheme
import com.krono.app.core.ui.dialogs.ColorPickerDialog
import com.krono.app.core.ui.components.FontSelector
import com.krono.app.core.ui.components.ThemeSelector
import kotlinx.coroutines.launch

@Composable
fun AppearancePanel(
    dataStore: OverlayDataStore,
    modifier: Modifier = Modifier
) {
    val config = dataStore.configFlow.collectAsState(initial = OverlayConfig()).value
    val scope = rememberCoroutineScope()
    val systemIsDark = isSystemInDarkTheme()

    var showBgPicker by remember { mutableStateOf(false) }
    var showTextPicker by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = KronoTokens.Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.lg)
    ) {
        Spacer(Modifier.height(KronoTokens.Spacing.sm))

        SettingsGroup(title = stringResource(R.string.settings_group_theme)) {
            ThemeSelector(
                selectedTheme = config.selectedTheme,
                onChange = { theme ->
                    scope.launch {
                        val option = KronoThemeOption.entries.find { it.name == theme }
                            ?: KronoThemeOption.AUTO
                        val (bgColor, txtColor) = overlayColorsForTheme(option, systemIsDark)
                        dataStore.updateConfig(
                            config.copy(
                                selectedTheme = theme,
                                backgroundColor = bgColor,
                                textColor = txtColor
                            )
                        )
                    }
                }
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                thickness = 0.5.dp
            )

            FontSelector(
                selectedFont = config.selectedFont,
                onChange = { font ->
                    scope.launch {
                        dataStore.updateConfig(config.copy(selectedFont = font))
                    }
                }
            )
        }

        SettingsGroup(title = stringResource(R.string.settings_group_colors)) {
            // Color swatches row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = KronoTokens.Spacing.lg,
                        vertical = KronoTokens.Spacing.md
                    ),
                horizontalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.lg)
            ) {
                ColorSwatchItem(
                    label = stringResource(R.string.label_background_color),
                    color = Color(config.backgroundColor).copy(alpha = config.bgOpacity),
                    hexText = "#%06X".format(config.backgroundColor and 0xFFFFFF).uppercase(),
                    modifier = Modifier.weight(1f),
                    onClick = { showBgPicker = true }
                )

                VerticalDivider(
                    modifier = Modifier.height(56.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                    thickness = 0.5.dp
                )

                ColorSwatchItem(
                    label = stringResource(R.string.label_text_color),
                    color = Color(config.textColor).copy(alpha = config.textOpacity),
                    hexText = "#%06X".format(config.textColor and 0xFFFFFF).uppercase(),
                    modifier = Modifier.weight(1f),
                    onClick = { showTextPicker = true }
                )
            }
        }

        Spacer(Modifier.height(KronoTokens.Spacing.xxl))
    }

    if (showBgPicker) {
        ColorPickerDialog(
            title = stringResource(R.string.label_background_color),
            initialColor = Color(config.backgroundColor),
            initialOpacity = config.bgOpacity,
            onPreview = { color, opacity ->
                scope.launch {
                    dataStore.updateConfig(config.copy(backgroundColor = color.toArgb(), bgOpacity = opacity))
                }
            },
            onConfirm = { color, opacity ->
                scope.launch {
                    dataStore.updateConfig(config.copy(backgroundColor = color.toArgb(), bgOpacity = opacity))
                }
                showBgPicker = false
            },
            onDismiss = { showBgPicker = false }
        )
    }

    if (showTextPicker) {
        ColorPickerDialog(
            title = stringResource(R.string.label_text_color),
            initialColor = Color(config.textColor),
            initialOpacity = config.textOpacity,
            onPreview = { color, opacity ->
                scope.launch {
                    dataStore.updateConfig(config.copy(textColor = color.toArgb(), textOpacity = opacity))
                }
            },
            onConfirm = { color, opacity ->
                scope.launch {
                    dataStore.updateConfig(config.copy(textColor = color.toArgb(), textOpacity = opacity))
                }
                showTextPicker = false
            },
            onDismiss = { showTextPicker = false }
        )
    }
}

@Composable
private fun ColorSwatchItem(
    label: String,
    color: Color,
    hexText: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = KronoTokens.Typography.statusLabel
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(KronoTokens.Spacing.sm))
        Surface(
            modifier = Modifier
                .size(KronoTokens.Component.colorSwatch),
            shape = CircleShape,
            color = color,
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            ),
            onClick = onClick
        ) {}
        Spacer(Modifier.height(KronoTokens.Spacing.xs))
        Text(
            text = hexText,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = KronoTokens.Typography.statusLabel
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        TextButton(
            onClick = onClick,
            contentPadding = PaddingValues(horizontal = KronoTokens.Spacing.sm, vertical = 2.dp)
        ) {
            Text(
                text = "Editar",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = KronoTokens.Typography.statusLabel
                )
            )
        }
    }
}
