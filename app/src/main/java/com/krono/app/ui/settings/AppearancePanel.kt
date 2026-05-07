package com.krono.app.ui.settings

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
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
import java.util.Locale

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
            .padding(horizontal = KronoTokens.Spacing.xxl),
        verticalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.lg)
    ) {
        Spacer(modifier = Modifier.height(KronoTokens.Spacing.lg))

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

        FontSelector(
            selectedFont = config.selectedFont,
            onChange = { font ->
                scope.launch {
                    dataStore.updateConfig(config.copy(selectedFont = font))
                }
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.label_background_color),
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(KronoTokens.Spacing.xs))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(end = KronoTokens.Spacing.md)
                ) {
                    androidx.compose.foundation.Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        drawRect(Color(config.backgroundColor).copy(alpha = config.bgOpacity))
                    }
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.label_text_color),
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(KronoTokens.Spacing.xs))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    androidx.compose.foundation.Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        drawRect(Color(config.textColor).copy(alpha = config.textOpacity))
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.md)
        ) {
            TextButton(
                onClick = { showBgPicker = true },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.label_background_color),
                    style = MaterialTheme.typography.labelMedium
                )
            }

            TextButton(
                onClick = { showTextPicker = true },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.label_text_color),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(KronoTokens.Spacing.xxl))
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