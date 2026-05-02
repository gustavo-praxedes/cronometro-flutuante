package com.krono.app.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import com.krono.app.ui.theme.KronoIcons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
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
import com.krono.app.data.TimeUtils
import com.krono.app.ui.theme.KronoTokens

@Composable
fun CountdownOverlayUi(
    state: CountdownState,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onClose: () -> Unit,
    onDrag: (dx: Float, dy: Float) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor   = Color(state.config.backgroundColor)
    val textColor = overlayTextColor(bgColor)

    // ── Animação de entrada — idêntica ao FloatingTimerUi ─────────────────
    val entranceScale = remember { Animatable(0.88f) }
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
                animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing)
            )
        }
    }

    // ── Estado de arraste ──────────────────────────────────────────────────
    var isDragging by remember { mutableStateOf(false) }
    val dragScale by animateFloatAsState(
        targetValue = if (isDragging) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow),
        label = "dragScale"
    )

    // ── Tokens — idênticos ao FloatingTimerUi ─────────────────────────────
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
    val maxWidth      = 270.dp   // hard cap — descrição longa não estica além disso

    // ── Borda animada (running = primary, parado = sutil) ────────────────
    val borderColor by animateColorAsState(
        targetValue = if (state.isRunning)
            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        else
            textColor.copy(alpha = 0.15f),
        animationSpec = tween(600),
        label = "borderColor"
    )

    // ── Fundo animado (concluído = vermelho) ──────────────────────────────
    val containerBg by animateColorAsState(
        targetValue = if (state.isCompleted) Color(0xFFB00020) else bgColor,
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
                alpha = entranceAlpha.value
                this.shape = shape
                clip = true
            }
            .background(containerBg, shape)
            .border(width = 0.86.dp, color = borderColor, shape = shape)
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
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { if (state.isRunning) onPause() else onPlay() },
                    onDoubleTap = { onReset() }
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
            // Quebra linha se longa, máx 2 linhas dentro do cap de largura
            Text(
                text = state.config.description.ifBlank { "Cronômetro" },
                color = textColor.copy(alpha = 0.72f),
                fontSize = (KronoTokens.Typography.statusLabel.value * currentScale).sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(btnTopPadding))

// ── Linha 2: Tempo + botões ────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = btnTopPadding),  // KronoTokens.Overlay.btnTopPadding
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tempo
                Text(
                    text = TimeUtils.formatSeconds(state.remainingSeconds),
                    color = textColor,
                    fontSize = (KronoTokens.Overlay.timerFontSize.value * 0.72f * currentScale).sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    softWrap = false
                )

                // Play / Pause
                AnimatedIconButton(
                    onClick = { if (state.isRunning) onPause() else onPlay() },
                    modifier = Modifier.size(btnSize)
                ) {
                    Icon(
                        imageVector = if (state.isRunning) KronoIcons.Action.Pause else KronoIcons.Action.Play,
                        contentDescription = if (state.isRunning) "Pausar" else "Iniciar",
                        tint = if (state.isRunning) MaterialTheme.colorScheme.primary else textColor,
                        modifier = Modifier.size(iconSizeDp)
                    )
                }

                // Reset
                AnimatedIconButton(
                    onClick = onReset,
                    modifier = Modifier.size(btnSize)
                ) {
                    Icon(KronoIcons.Action.Reset, "Reset", tint = textColor, modifier = Modifier.size(iconSizeDp))
                }

                // Fechar
                AnimatedIconButton(
                    onClick = onClose,
                    modifier = Modifier.size(btnSize)
                ) {
                    Icon(KronoIcons.Navigation.Close, "Fechar", tint = textColor.copy(alpha = 0.6f), modifier = Modifier.size(iconSizeDp))
                }
            }
        }
    }
}

/** Retorna cor legível baseada na luminância do fundo */
internal fun overlayTextColor(bg: Color): Color {
    val lum = 0.299f * bg.red + 0.587f * bg.green + 0.114f * bg.blue
    return if (lum > 0.45f) Color(0xFF1C1B1F) else Color(0xFFECECEC)
}
