package com.krono.app.feature.pomodoro

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.krono.app.R
import com.krono.app.core.data.OverlayConfig
import com.krono.app.core.data.toOverlayFormattedTime
import com.krono.app.core.ui.overlay.OverlayButton
import com.krono.app.core.ui.overlay.OverlayQuickOption
import com.krono.app.core.ui.overlay.UnifiedOverlay
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoThemeOption
import com.krono.app.core.ui.theme.KronoTokens
import com.krono.app.core.ui.theme.overlayColorsForTheme
import com.krono.app.feature.countdown.overlayTextColor
import kotlinx.coroutines.delay

@Composable
fun PomodoroOverlay(
    state: PomodoroState,
    config: OverlayConfig,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onNext: () -> Unit,
    onClose: () -> Unit,
    onNavigateToApp: () -> Unit,
    onToggleFocus: () -> Unit,
    onToggleKeepScreenOn: () -> Unit,
    onToggleAutoLaunch: () -> Unit,
    onToggleBeep: () -> Unit,
    onDrag: (Float, Float) -> Unit,
    onDragEnd: () -> Unit,
    onMenuVisibilityChange: (Boolean) -> Unit = {}
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

    val systemIsDark = isSystemInDarkTheme()
    val themeOption = runCatching {
        KronoThemeOption.valueOf(config.selectedTheme)
    }.getOrDefault(KronoThemeOption.AUTO)
    val (themeBg, _) = overlayColorsForTheme(themeOption, systemIsDark)
    val baseBg = Color(config.overlayCustomColor ?: themeBg)
    val background by animateColorAsState(
        targetValue = (flashColor?.let { Color(it) } ?: baseBg).copy(alpha = config.bgOpacity),
        animationSpec = tween(KronoTokens.Motion.durationNormal),
        label = "pomodoroOverlayBg"
    )
    val text = (
        config.overlayCustomTextColor?.let { Color(it) } ?: overlayTextColor(baseBg)
    ).copy(alpha = config.textOpacity)

    val playDescription = stringResource(
        if (state.isRunning) R.string.action_pause else R.string.action_play
    )

    UnifiedOverlay(
        timeDisplay = state.remainingMs.toOverlayFormattedTime(
            showHours = config.showHours,
            showMinutes = config.showMinutes,
            showSeconds = config.showSeconds,
            showMilliseconds = config.showMilliseconds
        ),
        label = state.phaseLabel,
        isRunning = state.isRunning,
        config = config,
        scale = config.scale,
        cornerRadius = config.cornerRadius,
        backgroundColor = background,
        textColor = text,
        buttons = listOf(
            OverlayButton(
                icon = if (state.isRunning) KronoIcons.Action.Pause else KronoIcons.Action.Play,
                description = playDescription,
                isActive = state.isRunning,
                onClick = { if (state.isRunning) onPause() else onPlay() }
            ),
            OverlayButton(
                icon = KronoIcons.Action.StopFilled,
                description = stringResource(R.string.action_reset),
                onClick = onReset
            ),
            OverlayButton(
                icon = KronoIcons.Action.Next,
                description = stringResource(R.string.action_next),
                onClick = onNext
            )
        ),
        quickOptions = listOf(
            OverlayQuickOption(
                icon = KronoIcons.Action.Focus,
                description = stringResource(R.string.label_focus_mode),
                isActive = config.focusModeEnabled,
                onClick = onToggleFocus
            ),
            OverlayQuickOption(
                icon = KronoIcons.Action.Light,
                description = stringResource(R.string.label_wake_lock),
                isActive = config.keepScreenOn,
                onClick = onToggleKeepScreenOn
            ),
            OverlayQuickOption(
                icon = KronoIcons.Feature.Overlay,
                description = stringResource(R.string.label_auto_launch),
                isActive = config.autoLaunch,
                onClick = onToggleAutoLaunch
            )
        ),
        onDrag = onDrag,
        onDragEnd = onDragEnd,
        onClose = onClose,
        onNavigateToApp = onNavigateToApp,
        onToggleBeep = onToggleBeep,
        onMenuVisibilityChange = onMenuVisibilityChange
    )

}
