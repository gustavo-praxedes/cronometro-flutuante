package com.krono.app.feature.stopwatch

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krono.app.core.data.OverlayConfig
import com.krono.app.core.ui.components.KronoControlButtons
import com.krono.app.core.ui.components.KronoTimerDisplay
import com.krono.app.core.ui.components.AnimatedIconButton
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoTokens
import com.krono.app.core.ui.theme.KronoThemeOption
import com.krono.app.core.ui.theme.overlayColorsForTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    val entranceScale = remember { Animatable(0.88f) }
    val entranceAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            entranceScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = 0.40f,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        launch {
            entranceAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing)
            )
        }
    }

    val currentScale  = entranceScale.value
    val cornerRadius  = (config.stopwatchOverlayCornerRadius * scale * currentScale).coerceAtMost(KronoTokens.Overlay.maxCornerRadiusFloat).dp
    val bgColor       = Color(effectiveBg).copy(alpha = config.bgOpacity)
    val txtColor      = Color(config.stopwatchOverlayCustomTextColor ?: themeTxt).copy(alpha = config.textOpacity)
    val shape         = RoundedCornerShape(cornerRadius)

    val paddingH      = (KronoTokens.Overlay.paddingH.value * scale * currentScale).dp
    val paddingV      = (KronoTokens.Overlay.paddingV.value * scale * currentScale).dp
    val menuPaddingV  = (KronoTokens.Overlay.menuPaddingV.value * scale * currentScale).dp
    val compactFactor = when {
        !config.stopwatchOverlayShowHours && !config.stopwatchOverlayShowSeconds -> 0.64f
        !config.stopwatchOverlayShowButtons -> 0.84f
        else -> 1f
    }
    val minColWidth   = (KronoTokens.Overlay.minWidth.value * scale * currentScale * compactFactor).dp
    val quickIconSize = (KronoTokens.Overlay.quickIconSize.value * scale * currentScale).dp
    val quickBtnSize  = (KronoTokens.Overlay.quickBtnSize.value * scale * currentScale).dp

    val currentOnStart              by rememberUpdatedState(onStart)
    val currentOnPause              by rememberUpdatedState(onPause)
    val currentOnReset              by rememberUpdatedState(onReset)
    val currentOnSettings           by rememberUpdatedState(onSettings)
    val currentIsRunning            by rememberUpdatedState(isRunning)

    var menuVisible by remember { mutableStateOf(false) }
    var isDragging by remember { mutableStateOf(false) }

    val dragScale by animateFloatAsState(
        targetValue = if (isDragging) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow),
        label = "dragScale"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isRunning) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else txtColor.copy(alpha = 0.15f),
        animationSpec = tween(600),
        label = "borderColor"
    )

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

    Box(
        modifier = Modifier
            .wrapContentSize()
            .graphicsLayer {
                val finalScale = currentScale * dragScale
                scaleX = finalScale
                scaleY = finalScale
                alpha = entranceAlpha.value
                this.shape = shape
                clip = true
            }
            .background(bgColor, shape)
            .border(
                width = 0.86.dp,
                color = borderColor,
                shape = shape
            )
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart  = { isDragging = true },
                    onDragEnd    = { isDragging = false; onDragEnd() },
                    onDragCancel = { isDragging = false; onDragEnd() },
                    onDrag       = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount.x, dragAmount.y)
                        if (menuVisible) resetMenuTimer()
                    }
                )
            }
    ) {
        Column(
            modifier            = Modifier
                .widthIn(min = minColWidth)
                .padding(horizontal = paddingH, vertical = paddingV),
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
                    textColor = txtColor
                )

                val MainButtonRow = @Composable {
                    KronoControlButtons(
                        isRunning = currentIsRunning,
                        isAtLimit = state.isAtLimit,
                        scale = scale,
                        currentScale = currentScale,
                        textColor = txtColor,
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
                                .padding(top = menuPaddingV),
                            thickness = 0.5.dp,
                            color = txtColor.copy(alpha = 0.2f)
                        )
                        Row(
                            modifier              = Modifier
                                .fillMaxWidth()
                                .padding(vertical = menuPaddingV),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            QuickOptionIcon(KronoIcons.Action.Focus, config.focusModeEnabled, txtColor, quickBtnSize, quickIconSize) {
                                resetMenuTimer(); onToggleFocus()
                            }
                            QuickOptionIcon(KronoIcons.Action.Light, config.keepScreenOn, txtColor, quickBtnSize, quickIconSize) {
                                resetMenuTimer(); onToggleKeepScreenOn()
                            }
                            QuickOptionIcon(KronoIcons.Feature.Overlay, config.autoLaunch, txtColor, quickBtnSize, quickIconSize) {
                                resetMenuTimer(); onToggleAutoLaunch()
                            }
                            QuickOptionIcon(KronoIcons.Action.Volume, config.playPauseSoundEnabled, txtColor, quickBtnSize, quickIconSize) {
                                resetMenuTimer(); onToggleBeep()
                            }
                        }
                    }
                }

                Icon(
                    imageVector = KronoIcons.Action.MoreHoriz,
                    contentDescription = "Menu",
                    tint = txtColor.copy(alpha = 0.4f),
                    modifier = Modifier
                        .height((10f * scale * currentScale).dp)
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart  = { isDragging = true },
                                onDragEnd = { isDragging = false; onDragEnd() },
                                onDragCancel = { isDragging = false; onDragEnd() },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    if (dragAmount.y > 2f && !menuVisible) menuVisible = true
                                    onDrag(dragAmount.x, dragAmount.y)
                                    if (menuVisible) resetMenuTimer()
                                }
                            )
                        }
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
