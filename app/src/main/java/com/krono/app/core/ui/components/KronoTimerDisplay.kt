package com.krono.app.core.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.krono.app.core.data.toFormattedTime
import com.krono.app.ui.theme.KronoTokens
import com.krono.app.ui.theme.timerFontFamily

@Composable
fun KronoTimerDisplay(
    elapsedMs: Long,
    showHours: Boolean,
    showSeconds: Boolean,
    selectedFont: String,
    scale: Float,
    currentScale: Float,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    val timeFontSize = (KronoTokens.Overlay.timerFontSize.value * scale * currentScale).sp

    Text(
        text = elapsedMs.toFormattedTime(
            showHours = showHours,
            showSeconds = showSeconds
        ),
        color = textColor,
        fontSize = timeFontSize,
        fontWeight = FontWeight.Bold,
        fontFamily = timerFontFamily(selectedFont),
        maxLines = 1,
        softWrap = false,
        modifier = modifier
    )
}
