package com.krono.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.krono.app.data.CountdownConfig

@Composable
fun CountdownConfigDialog(
    initial: CountdownConfig?,           // null = create mode
    onDismiss: () -> Unit,
    onConfirm: (CountdownConfig) -> Unit
) {
    var description by remember { mutableStateOf(initial?.description ?: "") }
    var totalSeconds by remember { mutableLongStateOf(initial?.totalSeconds ?: 300L) }
    var bgColor by remember {
        mutableStateOf(Color(initial?.backgroundColor ?: 0xFF1E1E2E.toInt()))
    }
    var showColorPicker by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ── Title ──────────────────────────────────────────────────
                Text(
                    text = if (initial == null) "Novo cronômetro" else "Editar cronômetro",
                    style = MaterialTheme.typography.titleLarge
                )

                // ── Description ────────────────────────────────────────────
                OutlinedTextField(
                    value = description,
                    onValueChange = { if (it.length <= 40) description = it },
                    label = { Text("Descrição") },
                    placeholder = { Text("Ex: Tempo de foco, Pausa...") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // ── Wheel picker ───────────────────────────────────────────
                Column {
                    Text(
                        text = "Tempo (hh : mm : ss)",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TimeWheelPicker(
                        totalSeconds = totalSeconds,
                        onValueChange = { totalSeconds = it }
                    )
                }

                // ── Color picker row ───────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cor do card",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { showColorPicker = true }) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(bgColor)
                            )
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "Escolher cor",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // ── Actions ────────────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Button(
                        onClick = {
                            val config = (initial ?: CountdownConfig(
                                description = description,
                                totalSeconds = totalSeconds,
                                backgroundColor = bgColor.toArgb()
                            )).copy(
                                description = description,
                                totalSeconds = totalSeconds,
                                backgroundColor = bgColor.toArgb()
                            )
                            onConfirm(config)
                        },
                        enabled = totalSeconds > 0L
                    ) {
                        Text(if (initial == null) "Criar" else "Salvar")
                    }
                }
            }
        }
    }

    if (showColorPicker) {
        ColorPickerDialog(
            title = "Escolher Cor",
            initialColor = bgColor,
            initialOpacity = 1f,
            onConfirm = { color, _ -> 
                bgColor = color
                showColorPicker = false 
            },
            onDismiss = { showColorPicker = false }
        )
    }
}
