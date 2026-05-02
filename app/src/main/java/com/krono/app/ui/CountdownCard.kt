package com.krono.app.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import com.krono.app.ui.theme.KronoIcons
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krono.app.data.CountdownState
import com.krono.app.data.TimeUtils

@Composable
fun CountdownCard(
    state: CountdownState,
    onEdit: () -> Unit,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onToggleOverlay: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val bgColor = Color(state.config.backgroundColor)
    val textColor = overlayTextColor(bgColor)

    val containerColor by animateColorAsState(
        targetValue = if (state.isCompleted) Color(0xFFB00020) else bgColor,
        animationSpec = spring(stiffness = 200f),
        label = "card_bg"
    )

    Card(
        onClick = onEdit,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {

            // ── Row 1: description + badge + ⋮ ───────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = state.config.description.ifBlank { "Sem título" },
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor.copy(alpha = 0.8f),
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                if (state.isCompleted) {
                    Spacer(Modifier.width(6.dp))
                    CompletedBadge(textColor)
                }
                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            KronoIcons.Action.More, "Opções",
                            tint = textColor.copy(alpha = 0.55f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Excluir", color = MaterialTheme.colorScheme.error) },
                            leadingIcon = {
                                Icon(
                                    KronoIcons.Action.Delete, null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            },
                            onClick = { menuExpanded = false; onDelete() }
                        )
                    }
                }
            }

            // ── Row 2: controls (LEFT) + time display (RIGHT) ─────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // Play/Pause
                IconButton(
                    onClick = { if (state.isRunning) onPause() else onPlay() },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        if (state.isRunning) KronoIcons.Action.Pause else KronoIcons.Action.Play,
                        if (state.isRunning) "Pausar" else "Iniciar",
                        tint = textColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Reset
                IconButton(
                    onClick = onReset,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        KronoIcons.Action.Reset, "Reiniciar",
                        tint = textColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Overlay toggle
                IconButton(
                    onClick = onToggleOverlay,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        KronoIcons.Feature.Overlay,
                        if (state.isOverlayVisible) "Ocultar overlay" else "Mostrar overlay",
                        tint = textColor.copy(alpha = if (state.isOverlayVisible) 1f else 0.45f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(Modifier.weight(1f))

                // Time display — right side
                Text(
                    text = TimeUtils.formatSeconds(state.remainingSeconds),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
private fun CompletedBadge(textColor: Color) {
    Surface(
        color = Color.White.copy(alpha = 0.22f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Icon(
                KronoIcons.Action.Check, null,
                tint = textColor,
                modifier = Modifier.size(10.dp)
            )
            Text(
                "Concluído",
                color = textColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
