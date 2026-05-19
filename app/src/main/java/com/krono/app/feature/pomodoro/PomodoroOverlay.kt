package com.krono.app.feature.pomodoro

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.krono.app.R
import com.krono.app.core.data.OverlayConfig
import com.krono.app.core.data.TimerDisplayFormat
import com.krono.app.core.data.formatSecondsByPattern
import com.krono.app.core.ui.components.AnimatedIconButton
import com.krono.app.core.ui.overlay.OverlayContainer
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoTokens
import com.krono.app.core.ui.theme.timerFontFamily
import com.krono.app.feature.countdown.overlayTextColor
import kotlinx.coroutines.delay

@Composable
fun PomodoroOverlay(
    state: PomodoroState,
    config: OverlayConfig,
    timeFormat: String,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onNext: () -> Unit,
    onClose: () -> Unit,
    onDrag: (Float, Float) -> Unit,
    onDragEnd: () -> Unit
) {
    var flashColor by remember { mutableStateOf<Int?>(null) }
    LaunchedEffect(state.phaseTransitionId, state.phaseColor) {
        if (state.phaseTransitionId > 0) {
            val target = state.phaseColor
            repeat(3) {
                flashColor = target
                delay(140)
                flashColor = null
                delay(110)
            }
        }
    }

    val baseBg = Color(config.pomodoroOverlayCustomColor ?: config.backgroundColor)
    val containerBg by animateColorAsState(
        targetValue = flashColor?.let { Color(it) } ?: baseBg,
        animationSpec = tween(KronoTokens.Motion.durationNormal),
        label = "pomodoroOverlayBg"
    )
    val textColor = config.pomodoroOverlayCustomTextColor
        ?.let { Color(it) }
        ?: overlayTextColor(baseBg)
    val displayFormat = TimerDisplayFormat.fromKey(timeFormat)
    val labelText = state.phaseLabel.trim()
    val hasLabel = labelText.isNotEmpty()
    val controlsCount = if (config.pomodoroOverlayShowButtons) 3 else 1

    OverlayContainer(
        isRunning = state.isRunning,
        scale = config.pomodoroOverlayScale,
        cornerRadius = config.pomodoroOverlayCornerRadius,
        bgColor = containerBg,
        textColor = textColor,
        onDrag = onDrag,
        onDragEnd = onDragEnd,
        showHours = config.pomodoroOverlayShowHours,
        showSeconds = config.pomodoroOverlayShowSeconds,
        showButtons = config.pomodoroOverlayShowButtons,
        widthScale = 0.96f,
        bottomExtraButtonScale = 1.22f,
        bottomExtraIconScale = 1.18f
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
                    color = if (hasLabel) txtColor.copy(alpha = KronoTokens.Alpha.medium) else Color.Transparent,
                    fontSize = (KronoTokens.Typography.statusLabel.value * currentScale).sp,
                    fontWeight = FontWeight.Normal,
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimensions.btnTopPadding),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatSecondsByPattern(
                        state.remainingSeconds,
                        when {
                            config.pomodoroOverlayShowHours && config.pomodoroOverlayShowSeconds -> displayFormat
                            config.pomodoroOverlayShowHours && !config.pomodoroOverlayShowSeconds -> TimerDisplayFormat.HH_MM
                            !config.pomodoroOverlayShowHours && config.pomodoroOverlayShowSeconds -> TimerDisplayFormat.MM_SS
                            else -> TimerDisplayFormat.MM_SS
                        }
                    ),
                    color = txtColor,
                    fontSize = (KronoTokens.Overlay.timerFontSize.value * KronoTokens.Alpha.overlayTimerScale * currentScale).sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = timerFontFamily(config.overlayFontFamily),
                    maxLines = 1,
                    softWrap = false
                )

                Spacer(Modifier.weight(1f))

                if (config.pomodoroOverlayShowButtons) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(dimensions.controlGap),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AnimatedIconButton(
                            onClick = { if (state.isRunning) onPause() else onPlay() },
                            modifier = Modifier.size(dimensions.btnSize)
                        ) {
                            Icon(
                                imageVector = if (state.isRunning) KronoIcons.Action.Pause else KronoIcons.Action.Play,
                                contentDescription = if (state.isRunning) stringResource(R.string.action_pause) else stringResource(R.string.action_play),
                                tint = if (state.isRunning) MaterialTheme.colorScheme.primary else txtColor,
                                modifier = Modifier.size(dimensions.iconSize)
                            )
                        }
                        AnimatedIconButton(onClick = onReset, modifier = Modifier.size(dimensions.btnSize)) {
                            Icon(
                                KronoIcons.Action.StopFilled,
                                stringResource(R.string.action_reset),
                                tint = txtColor,
                                modifier = Modifier.size(dimensions.iconSize)
                            )
                        }
                        AnimatedIconButton(
                            onClick = onNext,
                            modifier = Modifier.size(dimensions.extraBtnSize)
                        ) {
                            Icon(
                                KronoIcons.Action.Next,
                                stringResource(R.string.action_next),
                                tint = txtColor,
                                modifier = Modifier.size(dimensions.extraIconSize)
                            )
                        }
                    }
                }
            }
        }
    }
}
