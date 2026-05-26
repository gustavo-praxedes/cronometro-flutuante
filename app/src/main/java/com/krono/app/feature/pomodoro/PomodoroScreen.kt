package com.krono.app.feature.pomodoro

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.animateColorAsState
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
import com.krono.app.core.audio.SoundTimingPolicy
import com.krono.app.core.util.triggerPlayPauseFeedback
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroScreen(
    viewModel: PomodoroViewModel,
    onOpenSettings: () -> Unit,
    selectedFont: String = "SYSTEM_DEFAULT",
    timeFormat: String = "HH_MM_SS",
    selectedPreset: String = "CLASSICO",
    presetsSpec: String = "",
    customPresetPhasesSpec: String = "",
    customFocusMinutes: Int = 25,
    customBreakMinutes: Int = 5,
    customCycles: Int = 4,
    showHours: Boolean = true,
    showMinutes: Boolean = true,
    showSeconds: Boolean = true,
    showMilliseconds: Boolean = false,
    playPauseBeepEnabled: Boolean = false,
    playPauseVibrationEnabled: Boolean = true,
    playPauseVolume: Float = 0.8f,
    playPauseSoundType: String = "krono_tip_complete",
    autoStartNextCycle: Boolean = true,
    focusModeEnabled: Boolean = false,
    onStartFocusMode: () -> Unit = {},
    openOverlayOnPlay: Boolean = false,
    onOpenOverlay: () -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val isRunning = state.isRunning
    val format = TimerDisplayFormat.fromKey(timeFormat)
    val phaseLabel = state.phaseLabel
    val soundProfile = SoundTimingPolicy.profile(playPauseSoundType)
    val soundStartDelayMs = soundProfile.startDelayMs
    val soundMaxLifetimeMs = soundProfile.maxLifetimeMs
    var flashColor by remember { mutableStateOf<Color?>(null) }
    val displayBg by animateColorAsState(
        targetValue = flashColor ?: Color.Transparent,
        animationSpec = tween(120),
        label = "pomodoro_display_flash"
    )

    LaunchedEffect(selectedPreset, presetsSpec, customFocusMinutes, customBreakMinutes, customCycles, customPresetPhasesSpec) {
        viewModel.applyPreset(
            presetKey = selectedPreset,
            presetsSpec = presetsSpec,
            customFocusMinutes = customFocusMinutes,
            customBreakMinutes = customBreakMinutes,
            customCycles = customCycles,
            customPhasesSpec = customPresetPhasesSpec
        )
    }
    LaunchedEffect(autoStartNextCycle) {
        viewModel.setAutoAdvance(autoNextCycle = autoStartNextCycle)
    }
    LaunchedEffect(state.phaseTransitionId) {
        if (state.phaseTransitionId > 0) {
            val target = Color(state.phaseColor)
            repeat(3) {
                flashColor = target.copy(alpha = 0.28f)
                delay(140)
                flashColor = null
                delay(110)
            }
        }
    }
    Scaffold(
        containerColor = displayBg,
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
                    modifier = Modifier
                        .animateContentSize()
                        .padding(horizontal = 12.dp, vertical = 4.dp)
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
                        if (isRunning) {
                            viewModel.pause()
                            triggerPlayPauseFeedback(
                                context,
                                playPauseBeepEnabled,
                                playPauseVibrationEnabled,
                                playPauseVolume,
                                playPauseSoundType,
                                soundStartDelayMs,
                                soundMaxLifetimeMs
                            )
                        } else {
                            triggerPlayPauseFeedback(
                                context,
                                playPauseBeepEnabled,
                                playPauseVibrationEnabled,
                                playPauseVolume,
                                playPauseSoundType,
                                soundStartDelayMs,
                                soundMaxLifetimeMs
                            )
                            viewModel.start()
                            if (openOverlayOnPlay) onOpenOverlay()
                            if (focusModeEnabled) onStartFocusMode()
                        }
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


