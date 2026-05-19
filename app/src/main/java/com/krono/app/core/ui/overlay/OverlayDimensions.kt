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
    val btnTopPadding: Dp,
    val cornerRadius: Dp,
    val minWidth: Dp,
    val maxWidth: Dp,
    val iconSize: Dp,
    val btnSize: Dp,
    val extraBtnSize: Dp,
    val extraIconSize: Dp,
    val quickIconSize: Dp,
    val quickBtnSize: Dp,
    val closeBtnSize: Dp,
    val closeIconSize: Dp,
    val controlGap: Dp
)

@Composable
fun rememberOverlayDimensions(
    scale: Float,
    currentScale: Float,
    cornerRadius: Float,
    showHours: Boolean,
    showSeconds: Boolean,
    showButtons: Boolean,
    widthScale: Float = 1f,
    bottomExtraButtonScale: Float = 1f,
    bottomExtraIconScale: Float = 1f
): OverlayDimensions = remember(
    scale,
    currentScale,
    cornerRadius,
    showHours,
    showSeconds,
    showButtons,
    widthScale,
    bottomExtraButtonScale,
    bottomExtraIconScale
) {
    val compactFactor = when {
        !showHours && !showSeconds -> 0.64f
        !showButtons -> 0.84f
        else -> 1f
    }
    val scaled = scale * currentScale

    OverlayDimensions(
        paddingH = (KronoTokens.Overlay.paddingH.value * scaled).dp,
        paddingV = (KronoTokens.Overlay.paddingV.value * scaled).dp,
        menuPaddingV = (KronoTokens.Overlay.menuPaddingV.value * scaled).dp,
        btnTopPadding = (KronoTokens.Overlay.btnTopPadding.value * scaled).dp,
        cornerRadius = (cornerRadius * scaled)
            .coerceAtMost(KronoTokens.Overlay.maxCornerRadiusFloat)
            .dp,
        minWidth = (KronoTokens.Overlay.minWidth.value * scaled * widthScale * compactFactor).dp,
        maxWidth = (KronoTokens.Overlay.maxWidth.value * scaled * widthScale * compactFactor).dp,
        iconSize = (KronoTokens.Overlay.iconSize.value * scaled).dp,
        btnSize = (KronoTokens.Overlay.buttonSize.value * scaled).dp,
        extraBtnSize = (KronoTokens.Overlay.buttonSize.value * scaled * bottomExtraButtonScale).dp,
        extraIconSize = (KronoTokens.Overlay.iconSize.value * scaled * bottomExtraIconScale).dp,
        quickIconSize = (KronoTokens.Overlay.quickIconSize.value * scaled).dp,
        quickBtnSize = (KronoTokens.Overlay.quickBtnSize.value * scaled).dp,
        closeBtnSize = (KronoTokens.Overlay.buttonSize.value * scaled * 0.78f).dp,
        closeIconSize = (KronoTokens.Overlay.iconSize.value * scaled * 0.78f).dp,
        controlGap = (KronoTokens.Spacing.sm.value * scaled).dp
    )
}
