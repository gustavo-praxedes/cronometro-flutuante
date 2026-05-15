package com.krono.app.feature.pomodoro

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.timerFontFamily
import com.krono.app.core.data.TimerDisplayFormat
import com.krono.app.core.data.formatSecondsByPattern
import androidx.compose.ui.platform.LocalContext
import com.krono.app.core.util.playPomodoroPhaseBeep
import com.krono.app.core.util.playPomodoroTick
import com.krono.app.core.util.KronoToolAudio
import com.krono.app.core.util.triggerPlayPauseFeedback

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroScreen(
    viewModel: PomodoroViewModel,
    onOpenSettings: () -> Unit,
    selectedFont: String = "SYSTEM_DEFAULT",
    timeFormat: String = "HH_MM_SS",
    selectedPreset: String = "CLASSICO",
    playPauseBeepEnabled: Boolean = false,
    playPauseVibrationEnabled: Boolean = false,
    playPauseVolume: Float = 0.8f,
    phaseBeepEnabled: Boolean = false,
    tickingSoundEnabled: Boolean = false,
    tickVolume: Float = 0.35f,
    focusAlertVolume: Float = 0.9f,
    breakAlertVolume: Float = 0.9f,
    autoStartBreak: Boolean = true,
    autoStartFocus: Boolean = true,
    onOpenOverlay: () -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val isRunning = state.isRunning
    val format = TimerDisplayFormat.fromKey(timeFormat)
    val phaseLabel = state.phaseLabel
    val remaining = state.remainingSeconds

    LaunchedEffect(selectedPreset) {
        viewModel.applyPreset(selectedPreset)
    }
    LaunchedEffect(autoStartBreak, autoStartFocus) {
        viewModel.setAutoAdvance(autoBreak = autoStartBreak, autoFocus = autoStartFocus)
    }
    LaunchedEffect(state.phaseTransitionId, phaseBeepEnabled, focusAlertVolume, breakAlertVolume) {
        if (phaseBeepEnabled && state.phaseTransitionId > 0) {
            playPomodoroPhaseBeep(
                context,
                isFocusPhase = phaseLabel == "Foco",
                volume = if (phaseLabel == "Foco") focusAlertVolume else breakAlertVolume
            )
        }
    }
    var lastTickSecond by remember { mutableStateOf<Long?>(null) }
    LaunchedEffect(remaining, isRunning, tickingSoundEnabled, tickVolume) {
        if (isRunning && tickingSoundEnabled) {
            if (lastTickSecond != remaining) {
                playPomodoroTick(context, tickVolume)
                lastTickSecond = remaining
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { },
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

            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val fontSize = (maxWidth.value * 0.18f).coerceIn(32f, 76f)
                Text(
                    text = formatSecondsByPattern(state.remainingSeconds, format),
                    fontSize = fontSize.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = timerFontFamily(selectedFont),
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier.animateContentSize()
                )
            }

            Spacer(Modifier.weight(1f))

            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalIconButton(
                    onClick = { viewModel.reset() },
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(KronoIcons.Action.StopFilled, "Reset", modifier = Modifier.size(28.dp))
                }

                FilledIconButton(
                    onClick = {
                        triggerPlayPauseFeedback(
                            context,
                            playPauseBeepEnabled,
                            playPauseVibrationEnabled,
                            playPauseVolume,
                            KronoToolAudio.POMODORO
                        )
                        if (isRunning) viewModel.pause() else viewModel.start()
                    },
                    shape = CircleShape,
                    modifier = Modifier.size(80.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Crossfade(targetState = isRunning, animationSpec = tween(300), label = "play_pause_anim") { running ->
                        Icon(
                            imageVector = if (running) KronoIcons.Action.Pause else KronoIcons.Action.Play,
                            contentDescription = if (running) "Pausar" else "Iniciar",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                FilledTonalIconButton(
                    onClick = onOpenOverlay,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = KronoIcons.Feature.Overlay,
                        contentDescription = "Abrir Overlay",
                        modifier = Modifier.size(28.dp)
                    )
                }

                FilledTonalIconButton(
                    onClick = { viewModel.skipPhase() },
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = KronoIcons.Action.Next,
                        contentDescription = "Próximo",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(Modifier.height(64.dp))
        }
    }
}


