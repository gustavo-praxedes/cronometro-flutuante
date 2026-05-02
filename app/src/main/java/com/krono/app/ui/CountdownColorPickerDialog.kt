package com.krono.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
                .fillMaxWidth(0.9f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Title + preview
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Cor do card",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(selected)
                            .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape)
                    )
                }

                // 32 pastel swatches — 8 columns × 4 rows
                LazyVerticalGrid(
                    columns = GridCells.Fixed(8),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(PASTEL_COLORS) { color ->
                        val isSelected = color == selected
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(color)
                                .then(
                                    if (isSelected)
                                        Modifier.border(2.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                    else
                                        Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                                )
                                .clickable { selected = color }
                        ) {
                            if (isSelected) {
                                Icon(
                                    KronoIcons.Action.Check,
                                    contentDescription = "Selecionado",
                                    tint = overlayTextColor(color),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                // Custom color option
                TextButton(
                    onClick = { showCustomPicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        KronoIcons.Action.Palette,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Cor personalizada")
                }

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Button(onClick = { onColorSelected(selected) }) {
                        Text("Confirmar")
                    }
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
