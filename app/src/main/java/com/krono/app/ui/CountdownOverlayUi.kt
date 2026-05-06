package com.krono.app.ui

import androidx.compose.animation.Animatable
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krono.app.data.CountdownState
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoTokens
import com.krono.app.core.data.TimeUtils
import kotlinx.coroutines.launch

@Composable
fun CountdownOverlayUi(
    state     : CountdownState,
    onPlay    : () -> Unit,
    onPause   : () -> Unit,
    onReset   : () -> Unit,
    onClose   : () -> Unit,
    onDrag    : (dx: Float, dy: Float) -> Unit,
    onDragEnd : () -> Unit,
    modifier  : Modifier = Modifier
) {
    val bgColor   = Color(state.config.backgroundColor)
    val textColor = overlayTextColor(bgColor)

    // ── Animação de entrada — idêntica ao FloatingTimerUi ─────────────────
    val entranceScale = remember { Animatable(KronoTokens.Alpha.entranceInitialScale) }
    val entranceAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            entranceScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(dampingRatio = 0.40f, stiffness = Spring.StiffnessLow)
            )
        }
        launch {
            entranceAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = KronoTokens.Motion.durationSlow,
                    easing         = LinearOutSlowInEasing
                )
            )
        }
    }

    // ── Estado de arraste ──────────────────────────────────────────────────
    var isDragging by remember { mutableStateOf(false) }
    val dragScale by animateFloatAsState(
        targetValue = if (isDragging) KronoTokens.Alpha.dragScaleTarget else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow),
        label = "dragScale"
    )

    // ── Tokens Dinâmicos baseados no Scale de Entrada ─────────────────────
    val currentScale  = entranceScale.value
    val cornerRadius  = (KronoTokens.Overlay.defaultCornerRadius.value * currentScale)
        .coerceAtMost(KronoTokens.Overlay.maxCornerRadiusFloat).dp
    val shape         = RoundedCornerShape(cornerRadius)
    val paddingH      = (KronoTokens.Overlay.paddingH.value * currentScale).dp
    val paddingV      = (KronoTokens.Overlay.paddingV.value * currentScale).dp
    val btnTopPadding = (KronoTokens.Overlay.btnTopPadding.value * currentScale).dp
    val iconSizeDp    = (KronoTokens.Overlay.iconSize.value * currentScale).dp
    val btnSize       = (KronoTokens.Overlay.buttonSize.value * currentScale).dp
    val minWidth      = (KronoTokens.Overlay.minWidth.value * currentScale).dp
    val maxWidth      = (KronoTokens.Overlay.maxWidth.value).dp

    // ── Borda animada (running = primary, parado = sutil) ────────────────
    val borderColor by animateColorAsState(
        targetValue = if (state.isRunning)
            MaterialTheme.colorScheme.primary.copy(alpha = KronoTokens.Alpha.divider)
        else
            textColor.copy(alpha = KronoTokens.Alpha.glassBorder),
        animationSpec = tween(KronoTokens.Motion.durationSlow + 200),
        label = "borderColor"
    )

    // ── Fundo animado (concluído = vermelho erro) ────────────────────────
    val containerBg by animateColorAsState(
        targetValue = if (state.isCompleted) MaterialTheme.colorScheme.error else bgColor,
        animationSpec = tween(KronoTokens.Motion.durationNormal),
        label = "bg_color"
    )

    Box(
        modifier = modifier
            .wrapContentSize()
            .graphicsLayer {
                val finalScale = currentScale * dragScale
                scaleX = finalScale
                scaleY = finalScale
                alpha  = entranceAlpha.value
                this.shape = shape
                clip   = true
            }
            .background(containerBg, shape)
            .border(width = KronoTokens.Stroke.overlayBorder, color = borderColor, shape = shape)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart  = { isDragging = true },
                    onDragEnd    = { isDragging = false; onDragEnd() },
                    onDragCancel = { isDragging = false; onDragEnd() },
                    onDrag       = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount.x, dragAmount.y)
                    }
                )
            }
    ) {
        Column(
            modifier = Modifier
                .widthIn(min = minWidth, max = maxWidth)
                .padding(horizontal = paddingH, vertical = paddingV),
            horizontalAlignment = Alignment.Start
        ) {
            // ── Linha 1: Descrição ─────────────────────────────────────────
            Text(
                text       = state.config.description.ifBlank { "Cronômetro" },
                color      = textColor.copy(alpha = KronoTokens.Alpha.medium),
                fontSize   = (KronoTokens.Typography.statusLabel.value * currentScale).sp,
                fontWeight = FontWeight.Medium,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis,
                modifier   = Modifier.fillMaxWidth()
            )

            // ── Linha 2: Tempo + botões ──────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth().padding(top = btnTopPadding),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                // Tempo
                Text(
                    text       = TimeUtils.formatSeconds(state.remainingSeconds),
                    color      = textColor,
                    fontSize   = (KronoTokens.Overlay.timerFontSize.value * KronoTokens.Alpha.overlayTimerScale * currentScale).sp,
                    fontWeight = FontWeight.Bold,
                    maxLines   = 1,
                    softWrap   = false
                )

                // Play / Pause
                AnimatedIconButton(
                    onClick  = { if (state.isRunning) onPause() else onPlay() },
                    modifier = Modifier.size(btnSize)
                ) {
                    Icon(
                        imageVector        = if (state.isRunning) KronoIcons.Action.Pause else KronoIcons.Action.Play,
                        contentDescription = if (state.isRunning) "Pausar" else "Iniciar",
                        tint               = if (state.isRunning) MaterialTheme.colorScheme.primary else textColor,
                        modifier           = Modifier.size(iconSizeDp)
                    )
                }

                // Reset
                AnimatedIconButton(
                    onClick  = onReset,
                    modifier = Modifier.size(btnSize)
                ) {
                    Icon(
                        imageVector        = KronoIcons.Action.Reset,
                        contentDescription = "Reset",
                        tint               = textColor,
                        modifier           = Modifier.size(iconSizeDp)
                    )
                }

                // Fechar
                AnimatedIconButton(
                    onClick  = onClose,
                    modifier = Modifier.size(btnSize)
                ) {
                    Icon(
                        imageVector        = KronoIcons.Navigation.Close,
                        contentDescription = "Fechar",
                        tint               = textColor.copy(alpha = KronoTokens.Alpha.label),
                        modifier           = Modifier.size(iconSizeDp)
                    )
                }
            }
        }
    }
}

/** Retorna cor legível baseada na luminância do fundo */
internal fun overlayTextColor(bg: Color): Color {
    val lum = 0.299f * bg.red + 0.587f * bg.green + 0.114f * bg.blue
    return if (lum > KronoTokens.Alpha.overlayLuminanceThreshold) Color(0xFF1C1B1F) else Color(0xFFECECEC)
}

@Composable
private fun AnimatedIconButton(
    onClick  : () -> Unit,
    modifier : Modifier = Modifier,
    content  : @Composable () -> Unit
) {
    IconButton(
        onClick  = onClick,
        modifier = modifier
    ) {
        content()
    }
}
