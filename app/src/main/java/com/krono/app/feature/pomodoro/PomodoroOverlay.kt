package com.krono.app.feature.pomodoro

import androidx.compose.runtime.Composable
import com.krono.app.feature.countdown.CountdownConfig
import com.krono.app.feature.countdown.CountdownOverlayUi
import com.krono.app.feature.countdown.CountdownState

@Composable
fun PomodoroOverlay(
    state: PomodoroState,
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
            backgroundColor = 0xFF1E1E1E.toInt()
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
        overlayWidthScale = 0.96f,
        bottomExtraButtonScale = 1f,
        bottomExtraIconScale = 1f
    )
}
