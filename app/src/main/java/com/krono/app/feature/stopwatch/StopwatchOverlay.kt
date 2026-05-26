package com.krono.app.feature.stopwatch

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
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
import com.krono.app.core.ui.theme.overlayColorsForTheme

@Composable
fun StopwatchOverlay(
    state: StopwatchState,
    config: OverlayConfig,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onDrag: (dx: Float, dy: Float) -> Unit,
    onDragEnd: () -> Unit,
    onClose: () -> Unit,
    onSettings: () -> Unit,
    onToggleFocus: () -> Unit,
    onToggleKeepScreenOn: () -> Unit,
    onToggleAutoLaunch: () -> Unit,
    onToggleBeep: () -> Unit,
    onMenuVisibilityChange: (Boolean) -> Unit
) {
    val systemIsDark = isSystemInDarkTheme()
    val themeOption = runCatching {
        KronoThemeOption.valueOf(config.selectedTheme)
    }.getOrDefault(KronoThemeOption.AUTO)
    val (themeBg, themeTxt) = overlayColorsForTheme(themeOption, systemIsDark)
    val background = Color(config.overlayCustomColor ?: themeBg).copy(alpha = config.bgOpacity)
    val text = Color(config.overlayCustomTextColor ?: themeTxt).copy(alpha = config.textOpacity)
    val scale = config.scale

    val playDescription = stringResource(
        if (state.isRunning) R.string.action_pause else R.string.action_play
    )
    val resetDescription = stringResource(R.string.action_reset)
    val lapDescription = stringResource(R.string.action_lap)

    UnifiedOverlay(
        timeDisplay = state.elapsedMs.toOverlayFormattedTime(
            showHours = config.showHours,
            showMinutes = config.showMinutes,
            showSeconds = config.showSeconds,
            showMilliseconds = config.showMilliseconds
        ),
        label = null,
        isRunning = state.isRunning,
        config = config,
        scale = scale,
        cornerRadius = config.cornerRadius,
        backgroundColor = background,
        textColor = text,
        buttons = listOf(
            OverlayButton(
                icon = if (state.isRunning) KronoIcons.Action.Pause else KronoIcons.Action.Play,
                description = playDescription,
                isActive = state.isRunning,
                onClick = { if (state.isRunning) onPause() else onStart() }
            ),
            OverlayButton(
                icon = KronoIcons.Action.StopFilled,
                description = resetDescription,
                onClick = onReset
            ),
            OverlayButton(
                icon = KronoIcons.Action.Lap,
                description = lapDescription,
                enabled = false,
                onClick = {}
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
        onNavigateToApp = onSettings,
        onToggleBeep = onToggleBeep,
        onMenuVisibilityChange = onMenuVisibilityChange
    )

}
