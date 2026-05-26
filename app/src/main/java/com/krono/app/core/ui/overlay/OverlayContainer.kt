package com.krono.app.core.ui.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import com.krono.app.core.ui.theme.KronoTokens

/**
 * Shared overlay shell. Feature overlays should provide only their content.
 *
 * Minimal pattern for new overlays:
 *
 * OverlayContainer(
 *     isRunning = state.isRunning,
 *     scale = config.toolOverlayScale,
 *     cornerRadius = config.toolOverlayCornerRadius,
 *     bgColor = bgColor,
 *     textColor = textColor,
 *     onDrag = onDrag,
 *     onDragEnd = onDragEnd
 * ) { currentScale, txtColor, dimensions ->
 *     // Render feature content using dimensions + currentScale.
 * }
 */
@Composable
fun OverlayContainer(
    isRunning: Boolean,
    scale: Float,
    cornerRadius: Float,
    bgColor: Color,
    textColor: Color,
    onDrag: (dx: Float, dy: Float) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier,
    showHours: Boolean = true,
    showMinutes: Boolean = true,
    showSeconds: Boolean = true,
    showMilliseconds: Boolean = false,
    showButtons: Boolean = true,
    widthScale: Float = 1f,
    content: @Composable ColumnScope.(currentScale: Float, txtColor: Color, dimensions: OverlayDimensions) -> Unit
) {
    val scaleState = rememberOverlayScaleState()
    val dimensions = rememberOverlayDimensions(
        scale = scale,
        currentScale = scaleState.currentScale,
        cornerRadius = cornerRadius,
        showHours = showHours,
        showMinutes = showMinutes,
        showSeconds = showSeconds,
        showMilliseconds = showMilliseconds,
        showButtons = showButtons,
        widthScale = widthScale
    )
    val shape = RoundedCornerShape(dimensions.cornerRadius)
    val borderColor = overlayBorderColor(isRunning = isRunning, textColor = textColor)
    val currentOnDrag by rememberUpdatedState(onDrag)
    val currentOnDragEnd by rememberUpdatedState(onDragEnd)

    Box(
        modifier = modifier
            .wrapContentSize()
            .graphicsLayer {
                scaleX = scaleState.combinedScale
                scaleY = scaleState.combinedScale
                alpha = scaleState.alpha
                this.shape = shape
                clip = true
            }
            .background(bgColor, shape)
            .border(width = KronoTokens.Stroke.overlayBorder, color = borderColor, shape = shape)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { scaleState.onDragStart() },
                    onDragEnd = {
                        scaleState.onDragEnd()
                        currentOnDragEnd()
                    },
                    onDragCancel = {
                        scaleState.onDragEnd()
                        currentOnDragEnd()
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        currentOnDrag(dragAmount.x, dragAmount.y)
                    }
                )
            }
    ) {
        Column {
            content(scaleState.currentScale, textColor, dimensions)
        }
    }
}
