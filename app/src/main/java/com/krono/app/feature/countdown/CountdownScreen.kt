package com.krono.app.feature.countdown

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krono.app.core.data.TimerDisplayFormat
import com.krono.app.core.data.formatSecondsByPattern
import com.krono.app.core.data.toOverlayFormattedTime
import com.krono.app.core.audio.SoundTimingPolicy
import com.krono.app.core.service.MainService
import com.krono.app.R
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoTokens
import com.krono.app.core.ui.theme.timerFontFamily
import com.krono.app.core.util.triggerPlayPauseFeedback

private val SCREEN_OVERLAY_ID = CountdownViewModel.SCREEN_OVERLAY_ID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountdownScreen(
    viewModel: CountdownViewModel,
    timeFormat: String,
    selectedFont: String,
    initialConfiguredSeconds: Long = 0L,
    showHours: Boolean = true,
    showMinutes: Boolean = true,
    showSeconds: Boolean = true,
    showMilliseconds: Boolean = false,
    onConfiguredTimeChange: (Long) -> Unit = {},
    playPauseBeepEnabled: Boolean = false,
    playPauseVibrationEnabled: Boolean = false,
    playPauseVolume: Float = 0.8f,
    playPauseSoundType: String = "krono_tip_complete",
    openOverlayOnPlay: Boolean = false,
    focusModeEnabled: Boolean = false,
    onStartFocusMode: () -> Unit = {},
    onOpenSettings: () -> Unit
) {
    val context = LocalContext.current
    val countdowns by viewModel.countdowns.collectAsState()
    val maxSeconds = 99L * 3600L + 59L * 60L + 59L
    val hydratedInitial = initialConfiguredSeconds.coerceIn(0L, maxSeconds)

    var draftSeconds by remember(hydratedInitial) { mutableLongStateOf(hydratedInitial) }
    var baseSeconds by remember(hydratedInitial) { mutableLongStateOf(hydratedInitial) }
    var hasStartedSession by remember { mutableStateOf(false) }
    var userConfiguredTime by remember { mutableStateOf(false) }
    var lastSavedConfiguredSeconds by remember { mutableLongStateOf(hydratedInitial) }
    var wheelResetToken by remember { mutableIntStateOf(0) }
    var dialogOpen by remember { mutableStateOf(false) }
    var editingCountdown by remember { mutableStateOf<CountdownConfig?>(null) }

    val visibleCountdowns = remember(countdowns) { countdowns.filterNot { it.config.id == SCREEN_OVERLAY_ID } }
    val screenOverlayState = remember(countdowns) { countdowns.firstOrNull { it.config.id == SCREEN_OVERLAY_ID } }
    val currentRemainingMs = screenOverlayState?.remainingMs ?: (baseSeconds * 1000L)
    val currentRemainingSeconds = ((currentRemainingMs + 999L) / 1000L).coerceAtLeast(0L)
    val isRunning = screenOverlayState?.isRunning == true
    val hasCards = visibleCountdowns.isNotEmpty()
    val screenCardBgColor = MaterialTheme.colorScheme.primaryContainer.toArgb()
    val soundProfile = SoundTimingPolicy.profile(playPauseSoundType)
    val soundStartDelayMs = soundProfile.startDelayMs
    val soundMaxLifetimeMs = soundProfile.maxLifetimeMs
    val topSectionWeight by animateFloatAsState(
        targetValue = if (hasCards) 0.56f else 1f,
        animationSpec = tween(350),
        label = "countdown_top_weight"
    )

    fun syncOverlay(id: String) {
        context.startService(
            android.content.Intent(context, MainService::class.java).apply {
                action = CountdownViewModel.ACTION_COUNTDOWN_SYNC
                putExtra(CountdownViewModel.EXTRA_COUNTDOWN_ID, id)
            }
        )
    }

    fun setScreenCountdown(seconds: Long, resetWheel: Boolean = false) {
        val safe = seconds.coerceIn(0L, maxSeconds)
        if (safe != baseSeconds) userConfiguredTime = true
        draftSeconds = safe
        baseSeconds = safe
        if (resetWheel) wheelResetToken += 1
        viewModel.upsertTransientCountdown(
            config = CountdownConfig(
                id = SCREEN_OVERLAY_ID,
                description = "",
                totalSeconds = safe,
                backgroundColor = screenCardBgColor
            ),
            remainingSeconds = safe,
            isRunning = false
        )
        viewModel.previewRemaining(SCREEN_OVERLAY_ID, safe)
        syncOverlay(SCREEN_OVERLAY_ID)
        if (safe != lastSavedConfiguredSeconds) {
            lastSavedConfiguredSeconds = safe
            onConfiguredTimeChange(safe)
        }
    }

    LaunchedEffect(screenOverlayState?.config?.id) {
        // Só cria estado inicial quando não existe countdown transitório da tela.
        // Se já existir, preserva o valor atual selecionado pelo usuário.
        val existing = screenOverlayState
        if (existing == null) {
            draftSeconds = hydratedInitial
            baseSeconds = hydratedInitial
            lastSavedConfiguredSeconds = hydratedInitial
            wheelResetToken += 1
            viewModel.upsertTransientCountdown(
                config = CountdownConfig(
                    id = SCREEN_OVERLAY_ID,
                    description = "",
                    totalSeconds = hydratedInitial,
                    backgroundColor = screenCardBgColor
                ),
                remainingSeconds = hydratedInitial,
                isRunning = false
            )
            syncOverlay(SCREEN_OVERLAY_ID)
        } else {
            val safeExisting = existing.remainingSeconds.coerceIn(0L, maxSeconds)
            draftSeconds = safeExisting
            baseSeconds = safeExisting
            lastSavedConfiguredSeconds = safeExisting
        }
    }
    LaunchedEffect(isRunning) {
        if (isRunning) {
            hasStartedSession = true
        }
    }
    LaunchedEffect(screenOverlayState?.isCompleted) {
        if (screenOverlayState?.isCompleted == true) {
            hasStartedSession = false
            draftSeconds = baseSeconds
        }
    }

    LaunchedEffect(isRunning, currentRemainingSeconds) {
        if (!isRunning) {
            val safe = currentRemainingSeconds.coerceIn(0L, maxSeconds)
            if (!hasStartedSession && safe != lastSavedConfiguredSeconds) {
                lastSavedConfiguredSeconds = safe
                onConfiguredTimeChange(safe)
            }
        }
    }
    LaunchedEffect(isRunning, currentRemainingSeconds, baseSeconds) {
        if (!isRunning && currentRemainingSeconds == baseSeconds) {
            hasStartedSession = false
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
                            contentDescription = stringResource(R.string.settings_title),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 32.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(topSectionWeight),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.weight(1f))

                    if (!isRunning && (!hasStartedSession || screenOverlayState?.isCompleted == true)) {
                        key(wheelResetToken) {
                            CountdownScreenWheelPicker(
                                totalSeconds = currentRemainingSeconds,
                                numberFontSize = 76.sp,
                                fontFamily = timerFontFamily(selectedFont),
                                onValueChange = ::setScreenCountdown
                            )
                        }
                    } else {
                        Text(
                            text = currentRemainingMs.toOverlayFormattedTime(
                                showHours = showHours,
                                showMinutes = showMinutes,
                                showSeconds = showSeconds,
                                showMilliseconds = showMilliseconds
                            ),
                            fontSize = 76.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = timerFontFamily(selectedFont),
                            style = TextStyle(
                                lineHeight = 90.sp,
                                platformStyle = PlatformTextStyle(includeFontPadding = false)
                            ),
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                            softWrap = false
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilledTonalIconButton(
                            onClick = {
                                if (hasStartedSession) {
                                    viewModel.accumulateElapsedByTotalAndRemaining(
                                        totalSeconds = baseSeconds,
                                        remainingSeconds = currentRemainingSeconds
                                    )
                                }
                                hasStartedSession = false
                                viewModel.pause(context, SCREEN_OVERLAY_ID)
                                viewModel.setRemainingAndSync(context, SCREEN_OVERLAY_ID, baseSeconds, clearCompleted = true)
                                syncOverlay(SCREEN_OVERLAY_ID)
                            },
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(KronoIcons.Action.StopFilled, stringResource(R.string.countdown_action_reset), modifier = Modifier.size(28.dp))
                        }

                        FilledIconButton(
                            onClick = {
                                if (isRunning) {
                                    viewModel.pause(context, SCREEN_OVERLAY_ID, feedbackAlreadyHandled = true)
                                    triggerPlayPauseFeedback(context, playPauseBeepEnabled, playPauseVibrationEnabled, playPauseVolume, playPauseSoundType, soundStartDelayMs, soundMaxLifetimeMs)
                                } else {
                                    if (currentRemainingSeconds <= 0L && baseSeconds <= 0L) return@FilledIconButton
                                    if (currentRemainingSeconds <= 0L) {
                                        viewModel.setRemainingSeconds(SCREEN_OVERLAY_ID, baseSeconds, clearCompleted = true)
                                    }
                                    hasStartedSession = true
                                    triggerPlayPauseFeedback(context, playPauseBeepEnabled, playPauseVibrationEnabled, playPauseVolume, playPauseSoundType, soundStartDelayMs, soundMaxLifetimeMs)
                                    viewModel.play(context, SCREEN_OVERLAY_ID, feedbackAlreadyHandled = true)
                                    if (openOverlayOnPlay && screenOverlayState?.isOverlayVisible != true) {
                                        viewModel.toggleOverlay(context, SCREEN_OVERLAY_ID)
                                    }
                                    if (focusModeEnabled) onStartFocusMode()
                                }
                            },
                            shape = androidx.compose.foundation.shape.CircleShape,
                            modifier = Modifier.size(80.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Icon(
                                imageVector = if (isRunning) KronoIcons.Action.Pause else KronoIcons.Action.Play,
                                contentDescription = if (isRunning) stringResource(R.string.countdown_action_pause) else stringResource(R.string.countdown_action_start),
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        FilledTonalIconButton(
                            onClick = {
                                viewModel.upsertTransientCountdown(
                                    config = CountdownConfig(
                                        id = SCREEN_OVERLAY_ID,
                                        description = "",
                                        totalSeconds = currentRemainingSeconds,
                                        backgroundColor = screenCardBgColor
                                    ),
                                    remainingSeconds = currentRemainingSeconds,
                                    isRunning = isRunning
                                )
                                viewModel.previewRemaining(SCREEN_OVERLAY_ID, currentRemainingSeconds)
                                syncOverlay(SCREEN_OVERLAY_ID)
                                val visible = screenOverlayState?.isOverlayVisible ?: false
                                if (!visible) viewModel.toggleOverlay(context, SCREEN_OVERLAY_ID)
                            },
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(KronoIcons.Feature.Overlay, stringResource(R.string.countdown_overlay_desc), modifier = Modifier.size(28.dp))
                        }

                        FilledTonalIconButton(
                            onClick = { dialogOpen = true },
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(KronoIcons.Action.Add, stringResource(R.string.countdown_add_timer), modifier = Modifier.size(28.dp))
                        }
                    }
                    Spacer(Modifier.height(64.dp))
                }
            }

            if (hasCards) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight((1f - topSectionWeight).coerceAtLeast(0.001f)),
                    contentPadding = PaddingValues(
                        top = KronoTokens.Spacing.sm,
                        bottom = 160.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.listItemGap)
                ) {
                    items(visibleCountdowns, key = { it.config.id }) { state ->
                        CountdownCard(
                            state = state,
                            timeFormat = timeFormat,
                            showHours = showHours,
                            showMinutes = showMinutes,
                            showSeconds = showSeconds,
                            showMilliseconds = showMilliseconds,
                            onEdit = { editingCountdown = state.config },
                            onPlay = {
                                triggerPlayPauseFeedback(context, playPauseBeepEnabled, playPauseVibrationEnabled, playPauseVolume, playPauseSoundType, soundStartDelayMs, soundMaxLifetimeMs)
                                viewModel.play(context, state.config.id, feedbackAlreadyHandled = true)
                                if (openOverlayOnPlay && !state.isOverlayVisible) {
                                    viewModel.toggleOverlay(context, state.config.id)
                                }
                                if (focusModeEnabled) onStartFocusMode()
                            },
                            onPause = {
                                viewModel.pause(context, state.config.id, feedbackAlreadyHandled = true)
                                triggerPlayPauseFeedback(context, playPauseBeepEnabled, playPauseVibrationEnabled, playPauseVolume, playPauseSoundType, soundStartDelayMs, soundMaxLifetimeMs)
                            },
                            onReset = { viewModel.reset(context, state.config.id) },
                            onToggleOverlay = { viewModel.toggleOverlay(context, state.config.id) },
                            onDelete = { viewModel.deleteCountdown(context, state.config.id) }
                        )
                    }
                }
            }
        }
    }

    if (dialogOpen) {
        CountdownConfigDialog(
            initial = null,
            onDismiss = { dialogOpen = false },
            onConfirm = { config ->
                val toCreate = config.copy(
                    totalSeconds = if (config.totalSeconds > 0L) config.totalSeconds else draftSeconds
                )
                viewModel.addCountdown(toCreate)
                dialogOpen = false
            },
            onPreview = null
        )
    }

    editingCountdown?.let { configToEdit ->
        CountdownConfigDialog(
            initial = configToEdit,
            onDismiss = { editingCountdown = null },
            onConfirm = { updated ->
                viewModel.updateConfig(updated)
                viewModel.setRemainingAndSync(context, updated.id, updated.totalSeconds, clearCompleted = true)
                syncOverlay(updated.id)
                editingCountdown = null
            },
            onPreview = { seconds ->
                viewModel.previewRemaining(configToEdit.id, seconds)
                syncOverlay(configToEdit.id)
            }
        )
    }

}


