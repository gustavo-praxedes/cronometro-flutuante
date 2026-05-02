package com.krono.app.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.krono.app.data.CountdownState
import com.krono.app.ui.theme.KronoIcons
import com.krono.app.ui.theme.KronoTokens
import com.krono.app.ui.overlayTextColor
import com.krono.app.data.TimeUtils
import kotlin.math.roundToInt

@Composable
fun CountdownCard(
    state           : CountdownState,
    onEdit          : () -> Unit,
    onDelete        : () -> Unit,
    onPlay          : () -> Unit,
    onPause         : () -> Unit,
    onReset         : () -> Unit,
    onToggleOverlay : () -> Unit,
    modifier        : Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val bgColor = Color(state.config.backgroundColor)
    val textColor = overlayTextColor(bgColor)

    val containerColor by animateColorAsState(
        targetValue = if (state.isCompleted) MaterialTheme.colorScheme.error else bgColor,
        animationSpec = spring(stiffness = KronoTokens.Motion.durationNormal.toFloat()),
        label = "card_bg"
    )

    Card(
        onClick   = onEdit,
        modifier  = modifier.fillMaxWidth(),
        shape     = KronoTokens.Shape.card,
        colors    = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = KronoTokens.Elevation.card)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = KronoTokens.Spacing.cardPaddingH,
                    vertical   = KronoTokens.Spacing.cardPaddingV
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.md)
        ) {
            // ── Row 1: Description + Badge + Menu ─────────────────────────────
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text       = state.config.description.ifBlank { "Sem título" },
                    style      = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color      = textColor.copy(alpha = KronoTokens.Alpha.medium),
                    maxLines   = 1,
                    modifier   = Modifier.weight(1f)
                )


                Box {
                    IconButton(
                        onClick  = { menuExpanded = true },
                        modifier = Modifier.size(KronoTokens.Icon.close)
                    ) {
                        Icon(
                            imageVector        = KronoIcons.Action.More,
                            contentDescription = "Opções",
                            tint               = textColor.copy(alpha = KronoTokens.Alpha.medium),
                            modifier           = Modifier.size(KronoTokens.Icon.listItem)
                        )
                    }

                    DropdownMenu(
                        expanded         = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text        = { Text("Excluir", color = MaterialTheme.colorScheme.error) },
                            leadingIcon = {
                                Icon(
                                    imageVector = KronoIcons.Action.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }

            // ── Row 2: Time Display (LEFT) + Controls (RIGHT) ─────────────────
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Time Display
                Text(
                    text          = TimeUtils.formatSeconds(state.remainingSeconds),
                    fontSize      = KronoTokens.Typography.timerCard,
                    fontWeight    = FontWeight.Bold,
                    color         = textColor,
                    letterSpacing = (0.5).toSp()
                )

                Spacer(Modifier.weight(1f))

                // Controls
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.sm)
                ) {
                    // Play/Pause
                    IconButton(
                        onClick  = { if (state.isRunning) onPause() else onPlay() },
                        modifier = Modifier.size(KronoTokens.Button.heightSmall)
                    ) {
                        Icon(
                            imageVector        = if (state.isRunning) KronoIcons.Action.Pause else KronoIcons.Action.Play,
                            contentDescription = if (state.isRunning) "Pausar" else "Iniciar",
                            tint               = textColor,
                            modifier           = Modifier.size(KronoTokens.Icon.cardAction)
                        )
                    }

                    // Reset
                    IconButton(
                        onClick  = onReset,
                        modifier = Modifier.size(KronoTokens.Button.heightSmall)
                    ) {
                        Icon(
                            imageVector        = KronoIcons.Action.Reset,
                            contentDescription = "Reiniciar",
                            tint               = textColor,
                            modifier           = Modifier.size(KronoTokens.Icon.button)
                        )
                    }

                    // Overlay toggle
                    IconButton(
                        onClick  = onToggleOverlay,
                        modifier = Modifier.size(KronoTokens.Button.heightSmall)
                    ) {
                        Icon(
                            imageVector = KronoIcons.Feature.Overlay,
                            contentDescription = "Overlay",
                            tint = textColor.copy(
                                alpha = if (state.isOverlayVisible) KronoTokens.Alpha.iconEnabled else KronoTokens.Alpha.iconDisabled
                            ),
                            modifier = Modifier.size(KronoTokens.Icon.listItem)
                        )
                    }
                }
            }
        }
    }
}


// Extension to help with Sp conversion if needed
private fun Double.toSp() = (this).toTextUnit()
private fun Double.toTextUnit() = androidx.compose.ui.unit.TextUnit(this.toFloat(), androidx.compose.ui.unit.TextUnitType.Sp)
