package com.krono.app.core.ui.overlay

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.krono.app.core.ui.theme.KronoTokens

@Stable
data class OverlayDimensions(
    val paddingH: Dp,
    val paddingV: Dp,
    val menuPaddingV: Dp,
    val labelTopInset: Dp,
    val labelBottomGap: Dp,
    val noLabelTopInset: Dp,
    val timeButtonGap: Dp,
    val buttonMenuGap: Dp,
    val menuDividerButtonGap: Dp,
    val handleTopGap: Dp,
    val handleBottomGap: Dp,
    val cornerRadius: Dp,
    val containerWidth: Dp,
    val contentWidth: Dp,
    val gridWidth: Dp,
    val timerWidth: Dp,
    val timerVisualOffsetX: Dp,
    val iconSize: Dp,
    val btnTouchSize: Dp,
    val btnVisualSize: Dp,
    val buttonRowWidth: Dp,
    val quickRowWidth: Dp,
    val quickIconSize: Dp,
    val quickTouchSize: Dp,
    val quickVisualSize: Dp,
    val handleTouchHeight: Dp,
    val handlePillWidth: Dp,
    val handlePillHeight: Dp,
    val controlGap: Dp
)

@Composable
fun rememberOverlayDimensions(
    scale: Float,
    currentScale: Float,
    cornerRadius: Float,
    showHours: Boolean,
    showMinutes: Boolean,
    showSeconds: Boolean,
    showMilliseconds: Boolean,
    showButtons: Boolean,
    widthScale: Float = 1f
): OverlayDimensions = remember(
    scale,
    currentScale,
    cornerRadius,
    showHours,
    showMinutes,
    showSeconds,
    showMilliseconds,
    showButtons,
    widthScale
) {
    val timerWidth = when {
        showMilliseconds && showHours && showMinutes -> KronoTokens.Overlay.timerWidthMillisecondsFull
        showMilliseconds && (showHours || showMinutes || showSeconds) -> KronoTokens.Overlay.timerWidthMillisecondsNoHours
        showMilliseconds -> KronoTokens.Overlay.timerWidthMillisecondsCompact
        listOf(showHours, showMinutes, showSeconds).count { it } >= 3 -> KronoTokens.Overlay.timerWidthFull
        listOf(showHours, showMinutes, showSeconds).count { it } >= 2 -> KronoTokens.Overlay.timerWidthMedium
        else -> KronoTokens.Overlay.timerWidthCompact
    }
    val gridWidthBase = maxOf(
        timerWidth.value,
        KronoTokens.Overlay.controlRowMinWidth.value,
        KronoTokens.Overlay.handlePillWidth.value
    )
    val buttonRowWidthBase = gridWidthBase
    val quickRowWidthBase = gridWidthBase
    val contentWidthBase = maxOf(
        gridWidthBase,
        if (showButtons) buttonRowWidthBase else KronoTokens.Overlay.handlePillWidth.value,
        quickRowWidthBase
    )
    val containerWidthBase = (contentWidthBase + KronoTokens.Overlay.paddingH.value * 2f)
        .coerceIn(KronoTokens.Overlay.minWidth.value, KronoTokens.Overlay.maxWidth.value)
    val contentWidth = (containerWidthBase - KronoTokens.Overlay.paddingH.value * 2f)
        .coerceAtLeast(KronoTokens.Overlay.timerWidthCompact.value)
    val scaled = scale * currentScale

    OverlayDimensions(
        paddingH = (KronoTokens.Overlay.paddingH.value * scaled).dp,
        paddingV = (KronoTokens.Overlay.paddingV.value * scaled).dp,
        menuPaddingV = (KronoTokens.Overlay.menuPaddingV.value * scaled).dp,
        labelTopInset = (KronoTokens.Overlay.labelTopInset.value * scaled).dp,
        labelBottomGap = (KronoTokens.Overlay.labelBottomGap.value * scaled).dp,
        noLabelTopInset = (KronoTokens.Overlay.noLabelTopInset.value * scaled).dp,
        timeButtonGap = (KronoTokens.Overlay.timeButtonGap.value * scaled).dp,
        buttonMenuGap = (KronoTokens.Overlay.buttonMenuGap.value * scaled).dp,
        menuDividerButtonGap = (KronoTokens.Overlay.menuDividerButtonGap.value * scaled).dp,
        handleTopGap = (KronoTokens.Overlay.handleTopGap.value * scaled).dp,
        handleBottomGap = (KronoTokens.Overlay.handleBottomGap.value * scaled).dp,
        cornerRadius = (cornerRadius * scaled)
            .coerceAtMost(KronoTokens.Overlay.maxCornerRadiusFloat)
            .dp,
        containerWidth = (containerWidthBase * scaled * widthScale).dp,
        contentWidth = (contentWidth * scaled * widthScale).dp,
        gridWidth = (gridWidthBase * scaled * widthScale).dp,
        timerWidth = (timerWidth.value * scaled * widthScale).dp,
        timerVisualOffsetX = (KronoTokens.Overlay.timerVisualOffsetX.value * scaled).dp,
        iconSize = (KronoTokens.Overlay.iconSize.value * scaled).dp,
        btnTouchSize = (KronoTokens.Overlay.buttonTouchSize.value * scaled).dp,
        btnVisualSize = (KronoTokens.Overlay.buttonVisualSize.value * scaled).dp,
        buttonRowWidth = (buttonRowWidthBase * scaled * widthScale).dp,
        quickRowWidth = (quickRowWidthBase * scaled * widthScale).dp,
        quickIconSize = (KronoTokens.Overlay.quickIconSize.value * scaled).dp,
        quickTouchSize = (KronoTokens.Overlay.buttonTouchSize.value * scaled).dp,
        quickVisualSize = (KronoTokens.Overlay.buttonVisualSize.value * scaled).dp,
        handleTouchHeight = (KronoTokens.Overlay.handleTouchHeight.value * scaled).dp,
        handlePillWidth = (KronoTokens.Overlay.handlePillWidth.value * scaled).dp,
        handlePillHeight = (KronoTokens.Overlay.handlePillHeight.value * scaled).dp,
        controlGap = (KronoTokens.Overlay.btnSpacing.value * scaled).dp
    )
}

internal fun minimumOverlayButtonRowWidth(
    buttonCount: Int,
    minButtonSize: Dp,
    gap: Dp
): Dp {
    if (buttonCount <= 0) return 0.dp
    val buttons = minButtonSize.value * buttonCount
    val gaps = gap.value * (buttonCount - 1)
    return (buttons + gaps).dp
}

internal fun adaptiveOverlayButtonWidth(
    rowWidth: Dp,
    buttonCount: Int,
    minButtonSize: Dp,
    gap: Dp
): Dp {
    if (buttonCount <= 0) return minButtonSize
    val gaps = gap.value * (buttonCount - 1)
    val available = (rowWidth.value - gaps) / buttonCount
    return maxOf(minButtonSize.value, available).dp
}
