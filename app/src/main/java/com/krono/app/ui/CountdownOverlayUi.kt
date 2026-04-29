package com.krono.app.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
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

    // Pulse animation when completed
    val scale by animateFloatAsState(
        targetValue = if (state.isCompleted) 1.06f else 1f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 300f),
        label = "completed_pulse"
    )

    val containerColor by animateColorAsState(
        targetValue = if (state.isCompleted) Color(0xFFB00020) else bgColor,
        animationSpec = spring(stiffness = 200f),
        label = "container_color"
    )

    // Compute readable text color from background luminance
    val textColor = readableTextColor(bgColor)

    Column(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(containerColor)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Description label
        if (state.config.description.isNotBlank()) {
            Text(
                text = state.config.description,
                color = textColor.copy(alpha = 0.7f),
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
        }

        // Time display — same font scale as FloatingTimerUi
        Text(
            text = TimeUtils.formatSeconds(state.remainingSeconds),
            color = textColor,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Controls row
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
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
                    modifier = Modifier.size(20.dp)
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
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Close
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Fechar overlay",
                    tint = textColor.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/** Returns black or white depending on background luminance for readability */
private fun readableTextColor(bg: Color): Color {
    val luminance = 0.299f * bg.red + 0.587f * bg.green + 0.114f * bg.blue
    return if (luminance > 0.5f) Color(0xFF1C1B1F) else Color(0xFFE6E1E5)
}
