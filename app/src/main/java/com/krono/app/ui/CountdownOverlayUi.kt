package com.krono.app.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krono.app.data.CountdownState
import com.krono.app.data.TimeUtils

@Composable
fun CountdownOverlayUi(
    state: CountdownState,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = Color(state.config.backgroundColor)
    val textColor = overlayTextColor(bgColor)

    // Completed: pulsing red border
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    val containerColor by animateColorAsState(
        targetValue = if (state.isCompleted) Color(0xFFB00020) else bgColor,
        label = "bg"
    )

    Column(
        modifier = modifier
            .defaultMinSize(minWidth = 200.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(containerColor)
            .then(
                if (state.isCompleted) Modifier.drawBehind {
                    drawRoundRect(
                        color = Color.White.copy(alpha = pulseAlpha * 0.3f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx())
                    )
                } else Modifier
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        // ── Row 1: Description (top-left) + Close (top-right) ──────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = state.config.description.ifBlank { "Cronômetro" },
                color = textColor.copy(alpha = 0.85f),
                style = MaterialTheme.typography.labelLarge,  // larger than before
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Fechar",
                    tint = textColor.copy(alpha = 0.5f),
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // ── Row 2: Time display ────────────────────────────────────────────
        Text(
            text = TimeUtils.formatSeconds(state.remainingSeconds),
            color = textColor,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // ── Row 3: Controls ────────────────────────────────────────────────
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Play / Pause
            IconButton(
                onClick = { if (state.isRunning) onPause() else onPlay() },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (state.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (state.isRunning) "Pausar" else "Iniciar",
                    tint = textColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Reset
            IconButton(
                onClick = onReset,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Replay,
                    contentDescription = "Reiniciar",
                    tint = textColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

/** Luminance-based readable text color */
internal fun overlayTextColor(bg: Color): Color {
    val lum = 0.299f * bg.red + 0.587f * bg.green + 0.114f * bg.blue
    return if (lum > 0.5f) Color(0xFF1C1B1F) else Color(0xFFECECEC)
}
