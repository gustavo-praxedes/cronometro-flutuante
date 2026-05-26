package com.krono.app.feature.countdown

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import kotlinx.coroutines.delay

@Composable
fun CountdownOverlayUi(
    state: CountdownState,
    config: OverlayConfig,
    useToolColor: Boolean,
    showPlusOne: Boolean,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onPlusOne: () -> Unit,
    onClose: () -> Unit,
    onNavigateToApp: () -> Unit,
    onToggleFocus: () -> Unit,
    onToggleKeepScreenOn: () -> Unit,
    onToggleAutoLaunch: () -> Unit,
    onToggleBeep: () -> Unit,
    onDrag: (dx: Float, dy: Float) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier,
    onMenuVisibilityChange: (Boolean) -> Unit = {}
) {
    val systemIsDark = isSystemInDarkTheme()
    val themeOption = runCatching {
        KronoThemeOption.valueOf(config.selectedTheme)
    }.getOrDefault(KronoThemeOption.AUTO)
    val (themeBg, _) = overlayColorsForTheme(themeOption, systemIsDark)
    val baseBg = when {
        useToolColor -> Color(config.overlayCustomColor ?: themeBg)
        else -> Color(state.config.backgroundColor)
    }
    val baseText = if (useToolColor) {
        config.overlayCustomTextColor?.let { Color(it) } ?: overlayTextColor(baseBg)
    } else {
        overlayTextColor(baseBg)
    }

    var flashOn by remember(state.config.id) { mutableStateOf(false) }
    var completionHandled by remember(state.config.id) { mutableStateOf(false) }
    LaunchedEffect(state.isCompleted) {
        if (state.isCompleted && !completionHandled) {
            completionHandled = true
            repeat(3) {
                flashOn = true
                delay(130L)
                flashOn = false
                delay(110L)
            }
        }
        if (!state.isCompleted) {
            completionHandled = false
            flashOn = false
        }
    }

    val background by animateColorAsState(
        targetValue = if (flashOn) MaterialTheme.colorScheme.error else baseBg.copy(alpha = config.bgOpacity),
        animationSpec = tween(KronoTokens.Motion.durationNormal),
        label = "countdownOverlayBg"
    )
    val text = baseText.copy(alpha = config.textOpacity)
    val playDescription = stringResource(
        if (state.isRunning) R.string.action_pause else R.string.action_play
    )

    val buttons = buildList {
        add(
            OverlayButton(
                icon = if (state.isRunning) KronoIcons.Action.Pause else KronoIcons.Action.Play,
                description = playDescription,
                isActive = state.isRunning,
                onClick = { if (state.isRunning) onPause() else onPlay() }
            )
        )
        add(
            OverlayButton(
                icon = KronoIcons.Action.StopFilled,
                description = stringResource(R.string.action_reset),
                onClick = onReset
            )
        )
        if (showPlusOne) {
            add(
                OverlayButton(
                    icon = KronoIcons.Action.PlusOne,
                    description = stringResource(R.string.action_plus_one_minute),
                    onClick = onPlusOne
                )
            )
        }
    }

    UnifiedOverlay(
        timeDisplay = state.remainingMs.toOverlayFormattedTime(
            showHours = config.showHours,
            showMinutes = config.showMinutes,
            showSeconds = config.showSeconds,
            showMilliseconds = config.showMilliseconds
        ),
        label = state.config.description,
        isRunning = state.isRunning,
        config = config,
        scale = config.scale,
        cornerRadius = config.cornerRadius,
        backgroundColor = background,
        textColor = text,
        buttons = buttons,
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
        modifier = modifier,
        onMenuVisibilityChange = onMenuVisibilityChange
    )
}

internal fun overlayTextColor(bg: Color): Color {
    val lum = 0.299f * bg.red + 0.587f * bg.green + 0.114f * bg.blue
    return if (lum > KronoTokens.Alpha.overlayLuminanceThreshold) {
        Color(0xFF1C1B1F)
    } else {
        Color(0xFFECECEC)
    }
}
