package com.krono.app.feature.countdown

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.krono.app.R
import com.krono.app.core.data.TimerDisplayFormat
import com.krono.app.core.data.formatSecondsByPattern
import com.krono.app.core.ui.components.AnimatedIconButton
import com.krono.app.core.ui.overlay.OverlayContainer
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoTokens
import kotlinx.coroutines.delay

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
    timeFormat: String = "HH_MM_SS",
    showButtons: Boolean = true,
    showHours: Boolean = true,
    showSeconds: Boolean = true,
    selectedFont: String = "CHIVO_MONO",
    overlayScale: Float = 1f,
    overlayCornerRadius: Float = KronoTokens.Overlay.defaultCornerRadius.value,
    overlayCustomColor: Int? = null,
    overlayCustomTextColor: Int? = null,
    overlayWidthScale: Float = 0.96f,
    bottomExtraButtonScale: Float = 1f,
    bottomExtraIconScale: Float = 1f,
    modifier: Modifier = Modifier
) {
    val bgColor = Color(overlayCustomColor ?: state.config.backgroundColor)
    val textColor = overlayCustomTextColor?.let { Color(it) } ?: overlayTextColor(bgColor)
    val displayFormat = TimerDisplayFormat.fromKey(timeFormat)
    var flashOn by remember(state.config.id) { mutableStateOf(false) }
    var completionHandled by remember(state.config.id) { mutableStateOf(false) }
    LaunchedEffect(state.isCompleted) {
        if (state.isCompleted && !completionHandled) {
            completionHandled = true
            repeat(3) {
                flashOn = true
                delay(130L)
                flashOn = false
                delay(110L)
            }
        }
        if (!state.isCompleted) {
            completionHandled = false
            flashOn = false
        }
    }
    val controlsCount = if (showButtons) {
        2 + (if (onBottomExtraAction != null && bottomExtraIcon != null) 1 else 0) + (if (showBottomClose) 1 else 0)
    } else 1

    val containerBg by animateColorAsState(
        targetValue = if (flashOn) MaterialTheme.colorScheme.error else bgColor,
        animationSpec = tween(KronoTokens.Motion.durationNormal),
        label = "bg_color"
    )
    val labelText = state.config.description.trim()
    val hasLabel = labelText.isNotEmpty()

    OverlayContainer(
        isRunning = state.isRunning,
        scale = overlayScale,
        cornerRadius = overlayCornerRadius,
        bgColor = containerBg,
        textColor = textColor,
        onDrag = onDrag,
        onDragEnd = onDragEnd,
        modifier = modifier,
        showHours = showHours,
        showSeconds = showSeconds,
        showButtons = showButtons,
        widthScale = overlayWidthScale,
        bottomExtraButtonScale = bottomExtraButtonScale,
        bottomExtraIconScale = bottomExtraIconScale
    ) { currentScale, txtColor, dimensions ->
        val controlsWidth = (dimensions.btnSize * controlsCount) +
            (dimensions.controlGap * (controlsCount - 1).coerceAtLeast(0))
        Column(
            modifier = Modifier
                .widthIn(min = dimensions.minWidth, max = dimensions.maxWidth)
                .padding(horizontal = dimensions.paddingH, vertical = dimensions.paddingV),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (hasLabel) labelText else " ",
                    color = if (hasLabel) textColor.copy(alpha = KronoTokens.Alpha.medium) else Color.Transparent,
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
                    AnimatedIconButton(onClick = onClose, modifier = Modifier.size(dimensions.closeBtnSize)) {
                        Icon(
                            KronoIcons.Navigation.Close,
                            stringResource(R.string.action_close),
                            tint = txtColor.copy(alpha = KronoTokens.Alpha.label),
                            modifier = Modifier.size(dimensions.closeIconSize)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = dimensions.btnTopPadding),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatSecondsByPattern(
                        state.remainingSeconds,
                        when {
                            showHours && showSeconds -> displayFormat
                            showHours && !showSeconds -> TimerDisplayFormat.HH_MM
                            !showHours && showSeconds -> TimerDisplayFormat.MM_SS
                            else -> TimerDisplayFormat.MM_SS
                        }
                    ),
                    color = txtColor,
                    fontSize = (KronoTokens.Overlay.timerFontSize.value * KronoTokens.Alpha.overlayTimerScale * currentScale).sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = com.krono.app.core.ui.theme.timerFontFamily(selectedFont),
                    maxLines = 1,
                    softWrap = false
                )

                Spacer(Modifier.weight(1f))

                if (showButtons) Row(
                    horizontalArrangement = Arrangement.spacedBy(dimensions.controlGap),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnimatedIconButton(onClick = { if (state.isRunning) onPause() else onPlay() }, modifier = Modifier.size(dimensions.btnSize)) {
                        Icon(
                            imageVector = if (state.isRunning) KronoIcons.Action.Pause else KronoIcons.Action.Play,
                            contentDescription = if (state.isRunning) stringResource(R.string.action_pause) else stringResource(R.string.action_play),
                            tint = if (state.isRunning) MaterialTheme.colorScheme.primary else txtColor,
                            modifier = Modifier.size(dimensions.iconSize)
                        )
                    }

                    AnimatedIconButton(onClick = onReset, modifier = Modifier.size(dimensions.btnSize)) {
                        Icon(KronoIcons.Action.StopFilled, stringResource(R.string.action_reset), tint = txtColor, modifier = Modifier.size(dimensions.iconSize))
                    }

                    if (onBottomExtraAction != null && bottomExtraIcon != null) {
                        AnimatedIconButton(
                            onClick = onBottomExtraAction,
                            modifier = Modifier.size(dimensions.extraBtnSize)
                        ) {
                            Icon(
                                bottomExtraIcon,
                                bottomExtraDescription,
                                tint = txtColor,
                                modifier = Modifier.size(dimensions.extraIconSize)
                            )
                        }
                    }

                    if (showBottomClose) {
                        AnimatedIconButton(onClick = onClose, modifier = Modifier.size(dimensions.btnSize)) {
                            Icon(
                                KronoIcons.Navigation.Close,
                                stringResource(R.string.action_close),
                                tint = txtColor.copy(alpha = KronoTokens.Alpha.label),
                                modifier = Modifier.size(dimensions.iconSize)
                            )
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
