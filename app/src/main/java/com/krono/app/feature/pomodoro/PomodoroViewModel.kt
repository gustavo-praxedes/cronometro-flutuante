package com.krono.app.feature.pomodoro

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.krono.app.core.audio.SoundTimingPolicy
import com.krono.app.core.data.OverlayDataStore
import com.krono.app.core.data.OverlayConfig
import com.krono.app.core.tool.ToolState
import com.krono.app.core.tool.ToolViewModel
import com.krono.app.core.util.playPomodoroPhaseBeep
import com.krono.app.core.util.prepareSecondTickFeedback
import com.krono.app.core.util.stopActiveTimerSounds
import com.krono.app.core.util.triggerSecondFeedback
import android.os.SystemClock
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PomodoroViewModel(application: Application) : AndroidViewModel(application), ToolViewModel {
    companion object {
        private const val FOCUS_SECONDS = 25 * 60L
        private const val BREAK_SECONDS = 5 * 60L
    }

    private val _state = MutableStateFlow(PomodoroState())
    val state: StateFlow<PomodoroState> = _state.asStateFlow()
    override val toolState: StateFlow<ToolState> = state
    private var tickerJob: Job? = null
    private val overlayDataStore = OverlayDataStore(application)
    private var focusSeconds = FOCUS_SECONDS
    private var breakSeconds = BREAK_SECONDS
    private var autoStartNextCycle = true
    private var currentPresetKey = PomodoroPresetCatalog.DEFAULT_ID
    private var currentPresetsSpecRaw = ""
    private var maxCycles = 4
    private var customPhases: List<PhaseDef> = defaultPhases()
    private var currentPhaseIndex = 0
    private var phaseDeadlineElapsedMs: Long? = null
    private var lastSecondFeedback: Long? = null
    private var latestConfig: OverlayConfig = OverlayConfig()

    private fun secondsToMs(seconds: Long): Long = seconds.coerceAtLeast(0L) * 1000L
    private fun msToRemainingSeconds(ms: Long): Long = ((ms.coerceAtLeast(0L) + 999L) / 1000L)

    private data class PhaseDef(
        val label: String,
        val seconds: Long,
        val color: Int,
        val soundType: String
    )

    private fun defaultPhases() = listOf(
        PhaseDef("Foco", focusSeconds, 0xFFEF4444.toInt(), "krono_alm_alarmbeep"),
        PhaseDef("Pausa", breakSeconds, 0xFF22C55E.toInt(), "krono_alm_beeps")
    )

    init {
        viewModelScope.launch {
            overlayDataStore.configFlow.collect { latestConfig = it }
        }
    }

    override fun start() {
        if (_state.value.isRunning) return
        _state.value = _state.value.copy(isRunning = true)
        lastSecondFeedback = null
        phaseDeadlineElapsedMs = SystemClock.elapsedRealtime() + _state.value.remainingMs
        prepareSecondTickFeedback(
            context = getApplication(),
            tickSoundEnabled = latestConfig.allSoundsEnabled && latestConfig.tickSoundEnabled,
            environmentSoundType = latestConfig.environmentSoundType
        )
        tickerJob?.cancel()
        tickerJob = viewModelScope.launch {
            while (_state.value.isRunning) {
                val deadline = phaseDeadlineElapsedMs ?: (SystemClock.elapsedRealtime() + _state.value.remainingMs)
                val now = SystemClock.elapsedRealtime()
                val nextRemainingMs = (deadline - now).coerceAtLeast(0L)
                val nextRemainingSeconds = msToRemainingSeconds(nextRemainingMs)
                if (nextRemainingMs == 0L) {
                    val completedPhase = customPhases[currentPhaseIndex]
                    overlayDataStore.accumulateTime(completedPhase.seconds.coerceAtLeast(0L) * 1000L)
                    val nextIndex = (currentPhaseIndex + 1) % customPhases.size
                    val wrapped = nextIndex == 0
                    if (wrapped && _state.value.cycle >= maxCycles) {
                        viewModelScope.launch {
                            overlayDataStore.recordPomodoroSession(currentPresetKey, maxCycles)
                        }
                        currentPhaseIndex = 0
                        val firstPhase = customPhases.first()
                        phaseDeadlineElapsedMs = null
                        _state.value = _state.value.copy(
                            remainingSeconds = firstPhase.seconds,
                            remainingMs = secondsToMs(firstPhase.seconds),
                            phaseLabel = firstPhase.label,
                            phaseColor = firstPhase.color,
                            phaseSoundType = firstPhase.soundType,
                            isFocusPhase = true,
                            isRunning = false
                        )
                        stopActiveTimerSounds("pomodoro complete")
                        tickerJob?.cancel()
                        break
                    }
                    if (wrapped) {
                        val nextCycle = (_state.value.cycle + 1).coerceAtMost(maxCycles)
                        _state.value = _state.value.copy(cycle = nextCycle)
                    }
                    currentPhaseIndex = nextIndex
                    val phase = customPhases[currentPhaseIndex]
                    val shouldAutoStart = autoStartNextCycle
                    phaseDeadlineElapsedMs = if (shouldAutoStart) {
                        SystemClock.elapsedRealtime() + (phase.seconds * 1000L)
                    } else null
                    _state.value = _state.value.copy(
                        remainingSeconds = phase.seconds,
                        remainingMs = secondsToMs(phase.seconds),
                        phaseLabel = phase.label,
                        phaseColor = phase.color,
                        phaseSoundType = phase.soundType,
                        isFocusPhase = currentPhaseIndex % 2 == 0,
                        phaseTransitionId = _state.value.phaseTransitionId + 1,
                        isRunning = shouldAutoStart
                    )
                    playPhaseAlertIfEnabled(phase, currentPhaseIndex % 2 == 0)
                    if (!shouldAutoStart) {
                        stopActiveTimerSounds("pomodoro auto stop")
                        tickerJob?.cancel()
                        break
                    }
                } else {
                    if (nextRemainingSeconds != _state.value.remainingSeconds) {
                        triggerSecondFeedbackIfEnabled(nextRemainingSeconds)
                    }
                    _state.value = _state.value.copy(
                        remainingSeconds = nextRemainingSeconds,
                        remainingMs = nextRemainingMs
                    )
                    val delayToNextSecond = delayUntilNextSecondBoundary(nextRemainingMs)
                    val delayToNextFrame = if (latestConfig.showMilliseconds) 50L else delayToNextSecond
                    delay(minOf(delayToNextSecond, delayToNextFrame).coerceAtLeast(1L))
                }
            }
        }
    }

    override fun pause() {
        stopActiveTimerSounds("pomodoro pause")
        val deadline = phaseDeadlineElapsedMs
        if (deadline != null) {
            val now = SystemClock.elapsedRealtime()
            val remainingMs = (deadline - now).coerceAtLeast(0L)
            _state.value = _state.value.copy(
                remainingSeconds = msToRemainingSeconds(remainingMs),
                remainingMs = remainingMs
            )
        }
        phaseDeadlineElapsedMs = null
        lastSecondFeedback = null
        _state.value = _state.value.copy(isRunning = false)
        tickerJob?.cancel()
    }

    override fun reset() {
        stopActiveTimerSounds("pomodoro reset")
        currentPhaseIndex = 0
        val phase = customPhases.firstOrNull() ?: PhaseDef("Foco", focusSeconds, 0xFFEF4444.toInt(), "krono_alm_alarmbeep")
        _state.value = PomodoroState(
            remainingSeconds = phase.seconds,
            remainingMs = secondsToMs(phase.seconds),
            phaseLabel = phase.label,
            phaseColor = phase.color,
            phaseSoundType = phase.soundType,
            isFocusPhase = true
        )
        phaseDeadlineElapsedMs = null
        lastSecondFeedback = null
        tickerJob?.cancel()
    }

    fun skipPhase() {
        currentPhaseIndex = (currentPhaseIndex + 1) % customPhases.size
        val wrapped = currentPhaseIndex == 0
        val phase = customPhases[currentPhaseIndex]
        _state.value = _state.value.copy(
            remainingSeconds = phase.seconds,
            remainingMs = secondsToMs(phase.seconds),
            phaseLabel = phase.label,
            phaseColor = phase.color,
            phaseSoundType = phase.soundType,
            isFocusPhase = currentPhaseIndex % 2 == 0,
            cycle = if (wrapped) (_state.value.cycle % maxCycles) + 1 else _state.value.cycle,
            phaseTransitionId = _state.value.phaseTransitionId + 1
        )
        if (_state.value.isRunning) {
            lastSecondFeedback = null
            phaseDeadlineElapsedMs = SystemClock.elapsedRealtime() + (phase.seconds * 1000L)
        }
        playPhaseAlertIfEnabled(phase, currentPhaseIndex % 2 == 0)
    }

    fun applyPreset(
        presetKey: String,
        presetsSpec: String = ""
    ) {
        val availablePresets = PomodoroPresetCatalog.decode(presetsSpec)
        val safePresetKey = PomodoroPresetCatalog.normalizeSelectedPresetId(presetKey)
        val selectedPreset = availablePresets.firstOrNull { it.id == safePresetKey }
            ?: availablePresets.firstOrNull()
        if (
            safePresetKey == currentPresetKey &&
            presetsSpec == currentPresetsSpecRaw &&
            selectedPreset?.cycles == maxCycles &&
            selectedPreset?.executionPhases()?.map { phase ->
                PhaseDef(
                    label = phase.label,
                    seconds = phase.totalSeconds.coerceAtLeast(1L),
                    color = phase.color,
                    soundType = phase.soundType
                )
            } == customPhases
        ) return

        customPhases = selectedPreset?.executionPhases()
            ?.map { phase ->
                PhaseDef(
                    label = phase.label,
                    seconds = phase.totalSeconds.coerceAtLeast(1L),
                    color = phase.color,
                    soundType = phase.soundType
                )
            }
            ?.ifEmpty { defaultPhases() }
            ?: defaultPhases()
        maxCycles = selectedPreset?.cycles?.coerceIn(1, 12) ?: 4
        currentPresetKey = selectedPreset?.id ?: PomodoroPresetCatalog.DEFAULT_ID
        currentPresetsSpecRaw = presetsSpec
        if (_state.value.isRunning) return
        currentPhaseIndex = 0
        val phase = customPhases.first()
        focusSeconds = phase.seconds
        breakSeconds = customPhases.getOrNull(1)?.seconds ?: phase.seconds
        _state.value = _state.value.copy(
            remainingSeconds = phase.seconds,
            remainingMs = secondsToMs(phase.seconds),
            phaseLabel = phase.label,
            phaseColor = phase.color,
            phaseSoundType = phase.soundType,
            isFocusPhase = true,
            isRunning = false
        )
        phaseDeadlineElapsedMs = null
        lastSecondFeedback = null
        tickerJob?.cancel()
    }

    fun setAutoAdvance(autoNextCycle: Boolean) {
        autoStartNextCycle = autoNextCycle
    }

    private fun playPhaseAlertIfEnabled(phase: PhaseDef, isFocusPhase: Boolean) {
        viewModelScope.launch {
            val config = latestConfig
            if (!config.allSoundsEnabled) return@launch
            if (isFocusPhase && !config.pomodoroFocusAlertEnabled) return@launch
            if (!isFocusPhase && !config.pomodoroBreakAlertEnabled) return@launch
            playPomodoroPhaseBeep(
                context = getApplication(),
                isFocusPhase = isFocusPhase,
                volume = if (isFocusPhase) config.focusAlertVolume else config.breakAlertVolume,
                soundType = phase.soundType,
                startDelayMs = SoundTimingPolicy.profile(phase.soundType).startDelayMs,
                maxLifetimeMs = SoundTimingPolicy.profile(phase.soundType).maxLifetimeMs
            )
        }
    }

    private suspend fun triggerSecondFeedbackIfEnabled(secondMarker: Long) {
        if (lastSecondFeedback == secondMarker) return
        lastSecondFeedback = secondMarker
        val config = latestConfig
        val profile = SoundTimingPolicy.profile(config.environmentSoundType)
        triggerSecondFeedback(
            context = getApplication(),
            vibrationEnabled = config.secondsVibrationEnabled,
            tickSoundEnabled = config.allSoundsEnabled && config.tickSoundEnabled,
            tickVolume = config.tickVolume,
            environmentSoundType = config.environmentSoundType,
            startDelayMs = profile.startDelayMs,
            staleAfterMs = profile.staleAfterMs
        )
    }

    private fun delayUntilNextSecondBoundary(remainingMs: Long): Long {
        val safeRemainingMs = remainingMs.coerceAtLeast(0L)
        if (safeRemainingMs <= 0L) return 1L
        val remainder = safeRemainingMs % 1000L
        return if (remainder == 0L) 1000L else remainder
    }
}
