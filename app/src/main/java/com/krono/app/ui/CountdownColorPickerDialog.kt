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

private val PASTEL_COLORS = listOf(
    // Pinks & Reds
    0xFFFFB3BA, 0xFFFF8FA3, 0xFFE8A0BF, 0xFFF4A7B9,
    // Oranges
    0xFFFFDFBA, 0xFFFFCA99, 0xFFFFB347, 0xFFF5C8A0,
    // Yellows
    0xFFFFFFBA, 0xFFFFF176, 0xFFFFE680, 0xFFFADA7A,
    // Greens
    0xFFBAFFBA, 0xFFB5EAD7, 0xFF9DE8A0, 0xFFADE8B0,
    // Teals & Cyans
    0xFFBAFFFF, 0xFFB2EBF2, 0xFF80DEEA, 0xFF9FD8DF,
    // Blues
    0xFFBAD4FF, 0xFFB3C8F5, 0xFFA0C4FF, 0xFFBBCFF8,
    // Purples & Lavenders
    0xFFE0BAFF, 0xFFD4B0F0, 0xFFCFC1F5, 0xFFD8BFD8,
    // Browns & Warm Neutrals
    0xFFF5DEB3, 0xFFEDCBAA, 0xFFD2B48C, 0xFFC4A882,
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
                    items(PASTEL_COLORS) { color ->
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
