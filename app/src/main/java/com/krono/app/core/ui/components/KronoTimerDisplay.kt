package com.krono.app.core.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.krono.app.core.data.toOverlayFormattedTime
import com.krono.app.core.ui.theme.KronoTokens
import com.krono.app.core.ui.theme.timerFontFamily

@Composable
fun KronoTimerDisplay(
    elapsedMs: Long,
    showHours: Boolean,
    showMinutes: Boolean,
    showSeconds: Boolean,
    showMilliseconds: Boolean,
    selectedFont: String,
    scale: Float,
    currentScale: Float,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    val timeFontSize = (KronoTokens.Overlay.timerFontSize.value * scale * currentScale).sp

    Text(
        text = elapsedMs.toOverlayFormattedTime(
            showHours = showHours,
            showMinutes = showMinutes,
            showSeconds = showSeconds,
            showMilliseconds = showMilliseconds
        ),
        color = textColor,
        fontSize = timeFontSize,
        fontWeight = FontWeight.Normal,
        fontFamily = timerFontFamily(selectedFont),
        maxLines = 1,
        softWrap = false,
        modifier = modifier
    )
}

