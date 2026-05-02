package com.krono.app.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import com.krono.app.ui.theme.KronoIcons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color // Importado para usar Color.White ou Transparent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krono.app.data.TimerState
import com.krono.app.data.toFormattedTime
import com.krono.app.ui.theme.timerFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    timerState    : TimerState,
    selectedFont  : String = "SYSTEM_DEFAULT",
    onStart       : () -> Unit,
    onPause       : () -> Unit,
    onReset       : () -> Unit,
    onOpenOverlay : () -> Unit,
    onOpenSettings: () -> Unit
) {
    val isRunning = timerState.isRunning

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { },
                // Ajuste para branco ou transparente para sumir com a divisória
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                ),
                navigationIcon = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            imageVector = KronoIcons.Navigation.Menu,
                            contentDescription = "Configurações",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(1f))

            // ── Display do Tempo ───────────────────────────────────────────
            BoxWithConstraints(
                modifier         = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val fontSize = (maxWidth.value * 0.18f).coerceIn(32f, 76f)
                Text(
                    text       = timerState.elapsedMs.toFormattedTime(showHours = true, showSeconds = true),
                    fontSize   = fontSize.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = timerFontFamily(selectedFont),
                    color      = MaterialTheme.colorScheme.onBackground,
                    maxLines   = 1,
                    softWrap   = false,
                    modifier   = Modifier.animateContentSize()
                )
            }

            Spacer(Modifier.weight(1f))

            // ── Botões de controle ─────────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                FilledTonalIconButton(
                    onClick  = onReset,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        KronoIcons.Action.Reset, "Reset",
                        modifier = Modifier.size(28.dp)
                    )
                }

                FilledIconButton(
                    onClick  = { if (isRunning) onPause() else onStart() },
                    enabled  = !timerState.isAtLimit,
                    shape    = CircleShape,
                    modifier = Modifier.size(80.dp),
                    colors   = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor   = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Crossfade(
                        targetState  = isRunning,
                        animationSpec = tween(300),
                        label        = "play_pause_anim"
                    ) { running ->
                        Icon(
                            imageVector        = if (running) KronoIcons.Action.Pause else KronoIcons.Action.Play,
                            contentDescription = if (running) "Pausar" else "Iniciar",
                            modifier           = Modifier.size(40.dp)
                        )
                    }
                }

                // Substituído: Ícone de Picture-in-Picture (Overlay)
                FilledTonalIconButton(
                    onClick  = onOpenOverlay,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = KronoIcons.Feature.Overlay,
                        contentDescription = "Abrir Overlay",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(Modifier.height(64.dp))
        }
    }
}
