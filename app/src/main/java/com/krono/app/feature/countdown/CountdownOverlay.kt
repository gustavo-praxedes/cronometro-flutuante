package com.krono.app.feature.countdown

import androidx.compose.animation.Animatable
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krono.app.core.data.TimeUtils
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoTokens
import kotlinx.coroutines.launch

@Composable
fun CountdownOverlayUi(
    state: CountdownState,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onClose: () -> Unit,
    onDrag: (dx: Float, dy: Float) -> Unit,
    onDragEnd: () -> Unit,
    showBottomClose: Boolean = false,
    onBottomExtraAction: (() -> Unit)? = null,
    bottomExtraIcon: ImageVector? = null,
    bottomExtraDescription: String = "",
    overlayWidthScale: Float = 0.96f,
    bottomExtraButtonScale: Float = 1f,
    bottomExtraIconScale: Float = 1f,
    modifier: Modifier = Modifier
) {
    val bgColor = Color(state.config.backgroundColor)
    val textColor = overlayTextColor(bgColor)
    val entranceScale = remember { Animatable(KronoTokens.Alpha.entranceInitialScale) }
    val entranceAlpha = remember { Animatable(0f) }
    var isDragging by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        launch {
            entranceScale.animateTo(1f, spring(dampingRatio = 0.40f, stiffness = Spring.StiffnessLow))
        }
        launch {
            entranceAlpha.animateTo(1f, tween(durationMillis = KronoTokens.Motion.durationSlow, easing = LinearOutSlowInEasing))
        }
    }

    val dragScale by animateFloatAsState(
        targetValue = if (isDragging) KronoTokens.Alpha.dragScaleTarget else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow),
        label = "dragScale"
    )

    val currentScale = entranceScale.value
    val cornerRadius = (KronoTokens.Overlay.defaultCornerRadius.value * currentScale)
        .coerceAtMost(KronoTokens.Overlay.maxCornerRadiusFloat).dp
    val shape = RoundedCornerShape(cornerRadius)
    val paddingH = (KronoTokens.Overlay.paddingH.value * currentScale).dp
    val paddingV = (KronoTokens.Overlay.paddingV.value * currentScale).dp
    val btnTopPadding = (KronoTokens.Overlay.btnTopPadding.value * currentScale).dp
    val iconSizeDp = (KronoTokens.Overlay.iconSize.value * currentScale).dp
    val btnSize = (KronoTokens.Overlay.buttonSize.value * currentScale).dp
    val controlGap = (KronoTokens.Spacing.sm.value * currentScale).dp
    val closeBtnSize = (KronoTokens.Overlay.buttonSize.value * currentScale * 0.78f).dp
    val closeIconSize = (KronoTokens.Overlay.iconSize.value * currentScale * 0.78f).dp
    val minWidth = (KronoTokens.Overlay.minWidth.value * currentScale * overlayWidthScale).dp
    val maxWidth = (KronoTokens.Overlay.maxWidth.value * overlayWidthScale).dp
    val controlsCount = 2 + (if (onBottomExtraAction != null && bottomExtraIcon != null) 1 else 0) + (if (showBottomClose) 1 else 0)
    val controlsWidth = (btnSize * controlsCount) + (controlGap * (controlsCount - 1).coerceAtLeast(0))

    val borderColor by animateColorAsState(
        targetValue = if (state.isRunning)
            MaterialTheme.colorScheme.primary.copy(alpha = KronoTokens.Alpha.divider)
        else textColor.copy(alpha = KronoTokens.Alpha.glassBorder),
        animationSpec = tween(KronoTokens.Motion.durationSlow + 200),
        label = "borderColor"
    )

    val containerBg by animateColorAsState(
        targetValue = if (state.isCompleted) MaterialTheme.colorScheme.error else bgColor,
        animationSpec = tween(KronoTokens.Motion.durationNormal),
        label = "bg_color"
    )

    Box(
        modifier = modifier
            .wrapContentSize()
            .graphicsLayer {
                val finalScale = currentScale * dragScale
                scaleX = finalScale
                scaleY = finalScale
                alpha = entranceAlpha.value
                this.shape = shape
                clip = true
            }
            .background(containerBg, shape)
            .border(width = KronoTokens.Stroke.overlayBorder, color = borderColor, shape = shape)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { isDragging = true },
                    onDragEnd = { isDragging = false; onDragEnd() },
                    onDragCancel = { isDragging = false; onDragEnd() },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount.x, dragAmount.y)
                    }
                )
            }
    ) {
        Column(
            modifier = Modifier
                .widthIn(min = minWidth, max = maxWidth)
                .padding(horizontal = paddingH, vertical = paddingV),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = state.config.description.ifBlank { "Cronometro" },
                    color = textColor.copy(alpha = KronoTokens.Alpha.medium),
                    fontSize = (KronoTokens.Typography.statusLabel.value * currentScale).sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier.width(controlsWidth),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    AnimatedIconButton(onClick = onClose, modifier = Modifier.size(closeBtnSize)) {
                    Icon(KronoIcons.Navigation.Close, "Fechar", tint = textColor.copy(alpha = KronoTokens.Alpha.label), modifier = Modifier.size(closeIconSize))
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = btnTopPadding),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = TimeUtils.formatSeconds(state.remainingSeconds),
                    color = textColor,
                    fontSize = (KronoTokens.Overlay.timerFontSize.value * KronoTokens.Alpha.overlayTimerScale * currentScale).sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    softWrap = false
                )

                Spacer(Modifier.weight(1f))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(controlGap),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnimatedIconButton(onClick = { if (state.isRunning) onPause() else onPlay() }, modifier = Modifier.size(btnSize)) {
                        Icon(
                            imageVector = if (state.isRunning) KronoIcons.Action.Pause else KronoIcons.Action.Play,
                            contentDescription = if (state.isRunning) "Pausar" else "Iniciar",
                            tint = if (state.isRunning) MaterialTheme.colorScheme.primary else textColor,
                            modifier = Modifier.size(iconSizeDp)
                        )
                    }

                    AnimatedIconButton(onClick = onReset, modifier = Modifier.size(btnSize)) {
                        Icon(KronoIcons.Action.StopFilled, "Reset", tint = textColor, modifier = Modifier.size(iconSizeDp))
                    }

                    if (onBottomExtraAction != null && bottomExtraIcon != null) {
                        AnimatedIconButton(
                            onClick = onBottomExtraAction,
                            modifier = Modifier.size(btnSize * bottomExtraButtonScale)
                        ) {
                            Icon(
                                bottomExtraIcon,
                                bottomExtraDescription,
                                tint = textColor,
                                modifier = Modifier.size(iconSizeDp * bottomExtraIconScale)
                            )
                        }
                    }

                    if (showBottomClose) {
                        AnimatedIconButton(onClick = onClose, modifier = Modifier.size(btnSize)) {
                            Icon(KronoIcons.Navigation.Close, "Fechar", tint = textColor.copy(alpha = KronoTokens.Alpha.label), modifier = Modifier.size(iconSizeDp))
                        }
                    }
                }
            }
        }
    }
}

internal fun overlayTextColor(bg: Color): Color {
    val lum = 0.299f * bg.red + 0.587f * bg.green + 0.114f * bg.blue
    return if (lum > KronoTokens.Alpha.overlayLuminanceThreshold) Color(0xFF1C1B1F) else Color(0xFFECECEC)
}

@Composable
private fun AnimatedIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    IconButton(onClick = onClick, modifier = modifier) { content() }
}
