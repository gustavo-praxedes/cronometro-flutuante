package com.krono.app.feature.pomodoro

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.krono.app.core.data.OverlayConfig
import com.krono.app.feature.countdown.CountdownConfig
import com.krono.app.feature.countdown.CountdownOverlayUi
import com.krono.app.feature.countdown.CountdownState
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

    val proxy = CountdownState(
        config = CountdownConfig(
            id = "pomodoro",
            description = state.phaseLabel,
            totalSeconds = state.remainingSeconds,
            backgroundColor = flashColor ?: (config.pomodoroOverlayCustomColor ?: config.backgroundColor)
        ),
        remainingSeconds = state.remainingSeconds,
        isRunning = state.isRunning,
        isCompleted = false,
        isOverlayVisible = true
    )
    CountdownOverlayUi(
        state = proxy,
        onPlay = onPlay,
        onPause = onPause,
        onReset = onReset,
        onClose = onClose,
        onDrag = onDrag,
        onDragEnd = onDragEnd,
        showBottomClose = false,
        onBottomExtraAction = onNext,
        bottomExtraIcon = com.krono.app.core.ui.theme.KronoIcons.Action.Next,
        bottomExtraDescription = "Proximo",
        timeFormat = timeFormat,
        showButtons = config.pomodoroOverlayShowButtons,
        showHours = config.pomodoroOverlayShowHours,
        showSeconds = config.pomodoroOverlayShowSeconds,
        selectedFont = config.overlayFontFamily,
        overlayScale = config.pomodoroOverlayScale,
        overlayCornerRadius = config.pomodoroOverlayCornerRadius,
        overlayCustomColor = config.pomodoroOverlayCustomColor,
        overlayCustomTextColor = config.pomodoroOverlayCustomTextColor,
        overlayWidthScale = 0.96f,
        bottomExtraButtonScale = 1.22f,
        bottomExtraIconScale = 1.18f
    )
}
