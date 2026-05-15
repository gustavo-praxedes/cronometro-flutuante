package com.krono.app.feature.pomodoro

import androidx.compose.runtime.Composable
import com.krono.app.core.data.OverlayConfig
import com.krono.app.feature.countdown.CountdownConfig
import com.krono.app.feature.countdown.CountdownOverlayUi
import com.krono.app.feature.countdown.CountdownState

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
    val proxy = CountdownState(
        config = CountdownConfig(
            id = "pomodoro",
            description = state.phaseLabel,
            totalSeconds = state.remainingSeconds,
            backgroundColor = config.pomodoroOverlayCustomColor ?: config.backgroundColor
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
        overlayWidthScale = 0.96f,
        bottomExtraButtonScale = 1f,
        bottomExtraIconScale = 1f
    )
}
