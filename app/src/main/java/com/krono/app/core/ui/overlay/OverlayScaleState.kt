package com.krono.app.core.ui.overlay

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.krono.app.core.ui.theme.KronoTokens
import kotlinx.coroutines.launch

@Stable
class OverlayScaleState internal constructor(
    val currentScale: Float,
    val alpha: Float,
    val dragScale: Float,
    private val setDragging: (Boolean) -> Unit
) {
    val combinedScale: Float get() = currentScale * dragScale
    fun onDragStart() = setDragging(true)
    fun onDragEnd() = setDragging(false)
}

@Composable
fun rememberOverlayScaleState(): OverlayScaleState {
    val entranceScale = remember { Animatable(KronoTokens.Alpha.entranceInitialScale) }
    val entranceAlpha = remember { Animatable(0f) }
    var isDragging by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            entranceScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = 0.40f,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        scope.launch {
            entranceAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = KronoTokens.Motion.durationSlow,
                    easing = LinearOutSlowInEasing
                )
            )
        }
    }

    val dragScale by animateFloatAsState(
        targetValue = if (isDragging) KronoTokens.Alpha.dragScaleTarget else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow),
        label = "overlayDragScale"
    )

    return OverlayScaleState(
        currentScale = entranceScale.value,
        alpha = entranceAlpha.value,
        dragScale = dragScale,
        setDragging = { isDragging = it }
    )
}
