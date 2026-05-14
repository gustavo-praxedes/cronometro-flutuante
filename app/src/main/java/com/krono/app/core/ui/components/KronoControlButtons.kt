package com.krono.app.core.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoTokens

@Composable
fun AnimatedIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = Spring.StiffnessMedium
        ),
        label = "btnScale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (isPressed) 0.6f else 1f,
        animationSpec = tween(150),
        label = "btnAlpha"
    )

    IconButton(
        onClick = onClick,
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
            this.alpha = alpha
        },
        enabled = enabled,
        interactionSource = interactionSource,
        content = content
    )
}

@Composable
fun KronoControlButtons(
    isRunning: Boolean,
    isAtLimit: Boolean,
    scale: Float,
    currentScale: Float,
    textColor: Color,
    onStartPause: () -> Unit,
    onReset: () -> Unit,
    onSettings: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconSizeDp = (KronoTokens.Overlay.iconSize.value * scale * currentScale).dp
    val btnSize = (KronoTokens.Overlay.buttonSize.value * scale * currentScale).dp
    val btnTopPadding = (KronoTokens.Overlay.btnTopPadding.value * scale * currentScale).dp

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = btnTopPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedIconButton(
            onClick = onStartPause,
            enabled = !isAtLimit,
            modifier = Modifier.size(btnSize)
        ) {
            Icon(
                imageVector = if (isRunning) KronoIcons.Action.Pause else KronoIcons.Action.Play,
                contentDescription = if (isRunning) "Pausar" else "Iniciar",
                tint = if (isRunning) MaterialTheme.colorScheme.primary else textColor,
                modifier = Modifier.size(iconSizeDp)
            )
        }

        AnimatedIconButton(
            onClick = onReset,
            modifier = Modifier.size(btnSize)
        ) {
            Icon(KronoIcons.Action.StopFilled, "Reset", tint = textColor, modifier = Modifier.size(iconSizeDp))
        }

        AnimatedIconButton(
            onClick = onSettings,
            modifier = Modifier.size(btnSize)
        ) {
            Icon(KronoIcons.Navigation.Menu, "Config", tint = textColor, modifier = Modifier.size(iconSizeDp))
        }

        AnimatedIconButton(
            onClick = onClose,
            modifier = Modifier.size(btnSize)
        ) {
            Icon(KronoIcons.Navigation.Close, "Fechar", tint = textColor, modifier = Modifier.size(iconSizeDp))
        }
    }
}

