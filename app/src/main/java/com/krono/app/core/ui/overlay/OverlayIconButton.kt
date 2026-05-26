package com.krono.app.core.ui.overlay

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.krono.app.core.ui.theme.KronoTokens

@Composable
fun OverlayIconButton(
    icon: ImageVector,
    contentDescription: String,
    tint: Color,
    touchWidth: Dp,
    touchHeight: Dp,
    visualWidth: Dp,
    visualHeight: Dp,
    iconSize: Dp,
    enabled: Boolean,
    visualAlpha: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isActive: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.88f else 1f,
        animationSpec = spring(
            dampingRatio = 0.7f,
            stiffness = Spring.StiffnessMedium
        ),
        label = "overlayButtonScale"
    )
    val pressAlpha by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.72f else 1f,
        animationSpec = tween(KronoOverlayButtonAnimationMs),
        label = "overlayButtonAlpha"
    )
    val shape = RoundedCornerShape(KronoTokens.Overlay.buttonCorner)
    val containerAlpha = if (isActive) {
        KronoTokens.Overlay.buttonContainerActiveAlpha
    } else {
        KronoTokens.Overlay.buttonContainerAlpha
    }
    val borderAlpha = if (isActive) {
        KronoTokens.Overlay.buttonBorderActiveAlpha
    } else {
        KronoTokens.Overlay.buttonBorderAlpha
    }

    Box(
        modifier = modifier
            .size(width = visualWidth, height = visualHeight)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                alpha = visualAlpha * pressAlpha
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = touchWidth, height = touchHeight)
                .semantics(mergeDescendants = true) { role = Role.Button }
                .clickable(
                    enabled = enabled,
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(width = visualWidth, height = visualHeight)
                    .clip(shape)
                    .background(tint.copy(alpha = containerAlpha), shape)
                    .border(
                        width = KronoTokens.Stroke.overlayButtonBorder,
                        color = tint.copy(alpha = borderAlpha),
                        shape = shape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = tint,
                    modifier = Modifier.size(iconSize)
                )
            }
        }
    }
}

private const val KronoOverlayButtonAnimationMs = 120
