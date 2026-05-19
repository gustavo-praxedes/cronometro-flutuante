package com.krono.app.feature.stopwatch

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.krono.app.core.data.OverlayConfig
import com.krono.app.core.ui.components.AnimatedIconButton
import com.krono.app.core.ui.components.KronoControlButtons
import com.krono.app.core.ui.components.KronoTimerDisplay
import com.krono.app.core.ui.overlay.OverlayContainer
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoTokens
import com.krono.app.core.ui.theme.KronoThemeOption
import com.krono.app.core.ui.theme.overlayColorsForTheme
import kotlinx.coroutines.delay

@Composable
fun StopwatchOverlay(
    state                 : StopwatchState,
    config                : OverlayConfig,
    onStart               : () -> Unit,
    onPause               : () -> Unit,
    onReset               : () -> Unit,
    onDrag                : (dx: Float, dy: Float) -> Unit,
    onDragEnd             : () -> Unit,
    onClose               : () -> Unit,
    onSettings            : () -> Unit,
    onToggleFocus         : () -> Unit,
    onToggleKeepScreenOn  : () -> Unit,
    onToggleAutoLaunch    : () -> Unit,
    onToggleBeep          : () -> Unit,
    onMenuVisibilityChange: (Boolean) -> Unit
) {
    val isRunning = state.isRunning
    val scale     = config.stopwatchOverlayScale
    val systemIsDark = androidx.compose.foundation.isSystemInDarkTheme()
    val themeOption = runCatching { KronoThemeOption.valueOf(config.selectedTheme) }.getOrDefault(KronoThemeOption.AUTO)
    val (themeBg, themeTxt) = overlayColorsForTheme(themeOption, systemIsDark)
    val effectiveBg = config.stopwatchOverlayCustomColor ?: themeBg

    val bgColor       = Color(effectiveBg).copy(alpha = config.bgOpacity)
    val txtColor      = Color(config.stopwatchOverlayCustomTextColor ?: themeTxt).copy(alpha = config.textOpacity)

    val currentOnStart              by rememberUpdatedState(onStart)
    val currentOnPause              by rememberUpdatedState(onPause)
    val currentOnReset              by rememberUpdatedState(onReset)
    val currentOnSettings           by rememberUpdatedState(onSettings)
    val currentIsRunning            by rememberUpdatedState(isRunning)

    var menuVisible by remember { mutableStateOf(false) }

    LaunchedEffect(menuVisible) {
        onMenuVisibilityChange(menuVisible)
    }

    var menuInteractionTick by remember { mutableIntStateOf(0) }
    LaunchedEffect(menuVisible, menuInteractionTick) {
        if (menuVisible) {
            delay(KronoTokens.Overlay.menuTimeoutMs)
            menuVisible = false
        }
    }

    fun resetMenuTimer() { menuInteractionTick++ }

    OverlayContainer(
        isRunning = isRunning,
        scale = scale,
        cornerRadius = config.stopwatchOverlayCornerRadius,
        bgColor = bgColor,
        textColor = txtColor,
        onDrag = { dx, dy ->
            onDrag(dx, dy)
            if (menuVisible) resetMenuTimer()
        },
        onDragEnd = onDragEnd,
        showHours = config.stopwatchOverlayShowHours,
        showSeconds = config.stopwatchOverlayShowSeconds,
        showButtons = config.stopwatchOverlayShowButtons
    ) { currentScale, contentTextColor, dimensions ->
        Column(
            modifier            = Modifier
                .widthIn(min = dimensions.minWidth)
                .padding(horizontal = dimensions.paddingH, vertical = dimensions.paddingV),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.width(IntrinsicSize.Max),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                KronoTimerDisplay(
                    elapsedMs = state.elapsedMs,
                    showHours = config.stopwatchOverlayShowHours,
                    showSeconds = config.stopwatchOverlayShowSeconds,
                    selectedFont = config.overlayFontFamily,
                    scale = scale,
                    currentScale = currentScale,
                    textColor = contentTextColor
                )

                val MainButtonRow = @Composable {
                    KronoControlButtons(
                        isRunning = currentIsRunning,
                        isAtLimit = state.isAtLimit,
                        scale = scale,
                        currentScale = currentScale,
                        textColor = contentTextColor,
                        onStartPause = {
                            if (menuVisible) resetMenuTimer()
                            if (currentIsRunning) currentOnPause() else currentOnStart()
                        },
                        onReset = {
                            if (menuVisible) resetMenuTimer()
                            currentOnReset()
                        },
                        onSettings = {
                            menuVisible = false
                            currentOnSettings()
                        },
                        onClose = onClose
                    )
                }

                if (config.stopwatchOverlayShowButtons) {
                    MainButtonRow()
                } else {
                    AnimatedVisibility(
                        visible = menuVisible,
                        modifier = Modifier.fillMaxWidth(),
                        enter = expandVertically(expandFrom = Alignment.Top, animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessLow)) + fadeIn(),
                        exit = shrinkVertically(shrinkTowards = Alignment.Top, animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMedium)) + fadeOut()
                    ) {
                        MainButtonRow()
                    }
                }

                AnimatedVisibility(
                    visible = menuVisible,
                    modifier = Modifier.fillMaxWidth(),
                    enter = expandVertically(expandFrom = Alignment.Top, animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessLow)) + fadeIn(),
                    exit = shrinkVertically(shrinkTowards = Alignment.Top, animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMedium)) + fadeOut()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = dimensions.menuPaddingV),
                            thickness = 0.5.dp,
                            color = contentTextColor.copy(alpha = 0.2f)
                        )
                        Row(
                            modifier              = Modifier
                                .fillMaxWidth()
                                .padding(vertical = dimensions.menuPaddingV),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            QuickOptionIcon(KronoIcons.Action.Focus, config.focusModeEnabled, contentTextColor, dimensions.quickBtnSize, dimensions.quickIconSize) {
                                resetMenuTimer(); onToggleFocus()
                            }
                            QuickOptionIcon(KronoIcons.Action.Light, config.keepScreenOn, contentTextColor, dimensions.quickBtnSize, dimensions.quickIconSize) {
                                resetMenuTimer(); onToggleKeepScreenOn()
                            }
                            QuickOptionIcon(KronoIcons.Feature.Overlay, config.autoLaunch, contentTextColor, dimensions.quickBtnSize, dimensions.quickIconSize) {
                                resetMenuTimer(); onToggleAutoLaunch()
                            }
                            QuickOptionIcon(KronoIcons.Action.Volume, config.playPauseSoundEnabled, contentTextColor, dimensions.quickBtnSize, dimensions.quickIconSize) {
                                resetMenuTimer(); onToggleBeep()
                            }
                        }
                    }
                }

                Icon(
                    imageVector = KronoIcons.Action.MoreHoriz,
                    contentDescription = "Menu",
                    tint = contentTextColor.copy(alpha = 0.4f),
                    modifier = Modifier
                        .height((10f * scale * currentScale).dp)
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    menuVisible = !menuVisible
                                    if (menuVisible) resetMenuTimer()
                                }
                            )
                        }
                )
            }
        }
    }
}

@Composable
private fun QuickOptionIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isActive: Boolean,
    txtColor: Color,
    btnSize: androidx.compose.ui.unit.Dp,
    iconSize: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    AnimatedIconButton(
        onClick  = onClick,
        modifier = Modifier.size(btnSize)
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = if (isActive) MaterialTheme.colorScheme.primary else txtColor.copy(alpha = 0.4f),
            modifier           = Modifier.size(iconSize)
        )
    }
}
