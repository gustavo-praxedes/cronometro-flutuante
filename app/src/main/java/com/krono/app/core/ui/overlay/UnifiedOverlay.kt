package com.krono.app.core.ui.overlay

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.ui.unit.Dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.krono.app.R
import com.krono.app.core.data.OverlayConfig
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoTokens
import com.krono.app.core.ui.theme.timerFontFamily

/**
 * Standard floating overlay layout.
 *
 * Features own data and actions. This composable owns visual structure:
 * time row, fixed buttons and expandable quick options.
 */
@Composable
fun UnifiedOverlay(
    timeDisplay: String,
    label: String?,
    isRunning: Boolean,
    config: OverlayConfig,
    scale: Float,
    cornerRadius: Float,
    backgroundColor: Color,
    textColor: Color,
    buttons: List<OverlayButton>,
    quickOptions: List<OverlayQuickOption>,
    onDrag: (dx: Float, dy: Float) -> Unit,
    onDragEnd: () -> Unit,
    onClose: () -> Unit,
    onNavigateToApp: () -> Unit,
    onToggleBeep: () -> Unit,
    modifier: Modifier = Modifier,
    onMenuVisibilityChange: (Boolean) -> Unit = {}
) {
    val showPrimaryButtons = !config.hideOverlayButtons
    var menuExpanded by remember { mutableStateOf(false) }
    OverlayContainer(
        isRunning = isRunning,
        scale = scale,
        cornerRadius = cornerRadius,
        bgColor = backgroundColor,
        textColor = textColor,
        onDrag = onDrag,
        onDragEnd = onDragEnd,
        modifier = modifier,
        showHours = config.showHours,
        showMinutes = config.showMinutes,
        showSeconds = config.showSeconds,
        showMilliseconds = config.showMilliseconds,
        showButtons = showPrimaryButtons || menuExpanded
    ) { currentScale, txtColor, dimensions ->
        val cleanLabel = label.orEmpty().trim()
        val timerFontSize = (KronoTokens.Overlay.timerFontSize.value * scale * currentScale).sp
        val timerLineHeight = (KronoTokens.Overlay.timerLineHeight.value * scale * currentScale).sp
        val timerLetterSpacing = (KronoTokens.Overlay.timerLetterSpacing.value * scale * currentScale).sp
        val timerTextStyle = TextStyle(
            fontSize = timerFontSize,
            lineHeight = timerLineHeight,
            letterSpacing = timerLetterSpacing,
            fontWeight = FontWeight.Normal,
            fontFamily = timerFontFamily(config.overlayFontFamily),
            platformStyle = PlatformTextStyle(includeFontPadding = false)
        )
        val textMeasurer = rememberTextMeasurer()
        val density = LocalDensity.current
        val measuredTimerWidth = remember(timeDisplay, timerTextStyle, density) {
            with(density) {
                textMeasurer.measure(
                    text = AnnotatedString(timeDisplay),
                    style = timerTextStyle,
                    maxLines = 1,
                    softWrap = false
                ).size.width.toDp()
            }
        }
        val returnToScreenDescription = stringResource(R.string.action_return_to_screen)
        val beepDescription = stringResource(R.string.settings_all_sounds_label)
        val mainButtons = remember(buttons, returnToScreenDescription, onClose, onNavigateToApp) {
            buildList {
                addAll(buttons.take(2))
                add(
                    OverlayButton(
                        icon = KronoIcons.Action.Pip,
                        description = returnToScreenDescription,
                        onClick = {
                            onClose()
                            onNavigateToApp()
                        }
                    )
                )
                addAll(buttons.drop(2))
            }
        }
        val bottomOptions = remember(quickOptions, beepDescription, config.allSoundsEnabled, onToggleBeep) {
            quickOptions + OverlayQuickOption(
                icon = if (config.allSoundsEnabled) KronoIcons.Action.Notification else KronoIcons.Action.NotificationOff,
                description = beepDescription,
                isActive = config.allSoundsEnabled,
                onClick = onToggleBeep
            )
        }
        val minimumMainRowWidth = minimumOverlayButtonRowWidth(
            buttonCount = mainButtons.size,
            minButtonSize = dimensions.btnVisualSize,
            gap = dimensions.controlGap
        )
        val minimumQuickRowWidth = minimumOverlayButtonRowWidth(
            buttonCount = bottomOptions.size,
            minButtonSize = dimensions.quickVisualSize,
            gap = dimensions.controlGap
        )
        val contentWidth = maxOf(
            measuredTimerWidth,
            minimumMainRowWidth,
            minimumQuickRowWidth
        )
        val containerWidth = contentWidth + dimensions.paddingH + dimensions.paddingH
        val mainButtonWidth = adaptiveOverlayButtonWidth(
            rowWidth = contentWidth,
            buttonCount = mainButtons.size,
            minButtonSize = dimensions.btnVisualSize,
            gap = dimensions.controlGap
        )
        val labelTextStyle = TextStyle(
            fontSize = (KronoTokens.Typography.statusLabel.value * currentScale).sp,
            lineHeight = (KronoTokens.Typography.statusLabelLine.value * currentScale).sp,
            fontWeight = FontWeight.Normal,
            platformStyle = PlatformTextStyle(includeFontPadding = false)
        )
        val labelOverflows = remember(cleanLabel, labelTextStyle, contentWidth, density) {
            if (cleanLabel.isEmpty()) {
                false
            } else {
                with(density) {
                    textMeasurer.measure(
                        text = AnnotatedString(cleanLabel),
                        style = labelTextStyle,
                        maxLines = 1,
                        softWrap = false
                    ).size.width.toDp() > contentWidth
                }
            }
        }
        Box(
            modifier = Modifier
                .width(containerWidth)
                .padding(horizontal = dimensions.paddingH)
        ) {
            Column(
                modifier = Modifier.width(contentWidth),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (cleanLabel.isNotEmpty()) {
                    Gap(dimensions.labelTopInset)
                    Text(
                        text = cleanLabel,
                        color = txtColor.copy(alpha = KronoTokens.Alpha.medium),
                        fontSize = labelTextStyle.fontSize,
                        lineHeight = labelTextStyle.lineHeight,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Clip,
                        softWrap = false,
                        style = labelTextStyle,
                        modifier = Modifier
                            .width(contentWidth)
                            .offset(x = dimensions.timerVisualOffsetX)
                            .then(if (labelOverflows) Modifier.endFadeMask() else Modifier)
                    )
                    Gap(dimensions.labelBottomGap)
                } else if (bottomOptions.isNotEmpty()) {
                    Gap(dimensions.noLabelTopInset)
                }

                Text(
                    text = timeDisplay,
                    color = txtColor,
                    fontSize = timerFontSize,
                    lineHeight = timerLineHeight,
                    letterSpacing = timerLetterSpacing,
                    fontWeight = FontWeight.Normal,
                    fontFamily = timerFontFamily(config.overlayFontFamily),
                    maxLines = 1,
                    softWrap = false,
                    style = timerTextStyle,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .width(measuredTimerWidth)
                        .offset(x = dimensions.timerVisualOffsetX)
                )

                if (showPrimaryButtons && mainButtons.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .width(contentWidth)
                            .padding(top = dimensions.timeButtonGap),
                        horizontalArrangement = Arrangement.spacedBy(dimensions.controlGap),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        mainButtons.forEach { button ->
                            OverlayIconButton(
                                icon = button.icon,
                                contentDescription = button.description,
                                tint = if (button.isActive) MaterialTheme.colorScheme.primary else txtColor,
                                onClick = button.onClick,
                                enabled = button.enabled,
                                visualAlpha = button.visualAlpha,
                                touchWidth = mainButtonWidth,
                                touchHeight = dimensions.btnTouchSize,
                                visualWidth = mainButtonWidth,
                                visualHeight = dimensions.btnVisualSize,
                                iconSize = dimensions.iconSize,
                                isActive = button.isActive
                            )
                        }
                    }
                }

                if (showPrimaryButtons && mainButtons.isNotEmpty()) {
                    Gap(dimensions.buttonMenuGap)
                }

                OverlayExpandableMenu(
                    primaryButtons = if (showPrimaryButtons) emptyList() else mainButtons,
                    quickOptions = bottomOptions,
                    textColor = txtColor,
                    dimensions = dimensions,
                    rowWidth = contentWidth,
                    onExpandedChange = {
                        menuExpanded = it
                        onMenuVisibilityChange(it)
                    }
                )
            }

        }
    }
}

@Composable
private fun Gap(height: Dp) {
    Box(modifier = Modifier.height(height))
}

private fun Modifier.endFadeMask(): Modifier = drawWithContent {
    drawContent()
    val fadeWidth = size.width.coerceAtMost(24f)
    drawRect(
        brush = Brush.linearGradient(
            colorStops = arrayOf(
                0.0f to Color.White,
                1.0f to Color.Transparent
            ),
            start = Offset(size.width - fadeWidth, 0f),
            end = Offset(size.width, 0f)
        ),
        blendMode = BlendMode.DstIn
    )
}
