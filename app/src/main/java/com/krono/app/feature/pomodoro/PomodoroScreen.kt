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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.timerFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroScreen(
    viewModel: PomodoroViewModel,
    onOpenSettings: () -> Unit,
    selectedFont: String = "SYSTEM_DEFAULT",
    onOpenOverlay: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val isRunning = state.isRunning

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
                val total = state.remainingSeconds
                val h = total / 3600
                val m = (total % 3600) / 60
                val s = total % 60

                Text(
                    text = String.format("%02d:%02d:%02d", h, m, s),
                    fontSize = fontSize.sp,
                    fontWeight = FontWeight.Bold,
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
                    onClick = { if (isRunning) viewModel.pause() else viewModel.start() },
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

