package com.krono.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.krono.app.ui.theme.KronoIcons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

import androidx.compose.material3.IconButton
import com.krono.app.ui.theme.KronoTokens
import com.krono.app.ui.theme.adaptiveDialogWidth
import androidx.compose.foundation.layout.wrapContentHeight

private val COLOR_PALETTE = listOf(
    // Neutrais (Branco, Preto, Cinzas)
    0xFFFFFFFF, 0xFFF5F5F5, 0xFFCCCCCC, 0xFF808080,
    0xFF444444, 0xFF222222, 0xFF121212, 0xFF000000,
    // Cores Quentes & Vibrantes
    0xFFFF5252, 0xFFFF4081, 0xFFFF9800, 0xFFFFC107,
    0xFFCD7F32, 0xFF795548, 0xFFF08080, 0xFFFFB3BA,
    // Cores Frias & Natureza
    0xFF4CAF50, 0xFF009688, 0xFF00BCD4, 0xFF03A9F4,
    0xFF448AFF, 0xFF3F51B5, 0xFF607D8B, 0xFFBAFFBA,
    // Tons Artísticos & Diversos
    0xFF7C4DFF, 0xFF536DFE, 0xFFE0BAFF, 0xFF98FF98,
    0xFFE6E6FA, 0xFF708090, 0xFFD2B48C, 0xFFFFD700
).map { Color(it.toInt()) }


@Composable
fun CountdownColorPickerDialog(
    currentColor: Color,
    onDismiss: () -> Unit,
    onColorSelected: (Color) -> Unit
) {
    var selected by remember { mutableStateOf(currentColor) }
    var showCustomPicker by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .adaptiveDialogWidth()
                .wrapContentHeight()
                .padding(vertical = KronoTokens.Spacing.lg),
            shape = KronoTokens.Shape.dialog,
            tonalElevation = KronoTokens.Elevation.dialog,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(KronoTokens.Spacing.dialogPadding),
                verticalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.sectionGap)
            ) {
                // Title + Close (Standardized Box layout)
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text       = "Cor do card",
                        fontSize   = KronoTokens.Typography.dialogTitle,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.onSurface
                    )

                    IconButton(
                        onClick  = onDismiss,
                        modifier = Modifier
                            .size(KronoTokens.Icon.close)
                            .align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            imageVector = KronoIcons.Navigation.Close,
                            contentDescription = "Fechar",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(KronoTokens.Icon.listItem)
                        )
                    }
                }

                // 32 pastel swatches — 8 columns × 4 rows
                LazyVerticalGrid(
                    columns = GridCells.Fixed(8),
                    horizontalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.sm),
                    verticalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.sm),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(COLOR_PALETTE) { color ->
                        val isSelected = color == selected
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(KronoTokens.Component.colorSwatch)
                                .clip(CircleShape)
                                .background(color)
                                .then(
                                    if (isSelected)
                                        Modifier.border(KronoTokens.Stroke.circularIndicator, MaterialTheme.colorScheme.primary, CircleShape)
                                    else
                                        Modifier.border(KronoTokens.Stroke.cardBorder, MaterialTheme.colorScheme.outlineVariant.copy(alpha = KronoTokens.Alpha.low), CircleShape)
                                )
                                .clickable { selected = color }
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = KronoIcons.Action.Check,
                                    contentDescription = "Selecionado",
                                    tint = overlayTextColor(color),
                                    modifier = Modifier.size(KronoTokens.Icon.dialogHeader)
                                )
                            }
                        }
                    }
                }

                // Custom color option (Small & Symmetrical spacing)
                TextButton(
                    onClick  = { showCustomPicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape    = KronoTokens.Shape.buttonSmall,
                    contentPadding = PaddingValues(KronoTokens.Spacing.sm)
                ) {
                    Icon(
                        imageVector = KronoIcons.Action.Palette,
                        contentDescription = null,
                        modifier = Modifier.size(KronoTokens.Icon.small)
                    )
                    Spacer(Modifier.size(KronoTokens.Button.iconSpacing))
                    Text(
                        text     = "Cor personalizada",
                        fontSize = KronoTokens.Typography.statusLabel
                    )
                }

                // Action Confirmar (Full width like others)
                Button(
                    onClick  = { onColorSelected(selected) },
                    shape    = KronoTokens.Shape.button,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(KronoTokens.Button.height),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor   = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        "Confirmar",
                        fontSize   = KronoTokens.Typography.buttonLabel,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    if (showCustomPicker) {
        ColorPickerDialog(
            title = "Cor personalizada",
            initialColor = selected,
            initialOpacity = selected.alpha,
            onDismiss = { showCustomPicker = false },
            onConfirm = { color, opacity ->
                selected = color.copy(alpha = opacity)
                showCustomPicker = false
            }
        )
    }
}
