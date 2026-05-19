package com.krono.app.core.ui.overlay

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.krono.app.core.ui.theme.KronoTokens

@Composable
fun overlayBorderColor(isRunning: Boolean, textColor: Color): Color {
    val color by animateColorAsState(
        targetValue = if (isRunning) {
            MaterialTheme.colorScheme.primary.copy(alpha = KronoTokens.Alpha.divider)
        } else {
            textColor.copy(alpha = KronoTokens.Alpha.glassBorder)
        },
        animationSpec = tween(KronoTokens.Motion.durationSlow + 200),
        label = "overlayBorderColor"
    )
    return color
}
