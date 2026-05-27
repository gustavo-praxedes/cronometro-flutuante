package com.krono.app.feature.stopwatch

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.krono.app.core.audio.SoundTimingPolicy
import com.krono.app.core.data.OverlayDataStore
import com.krono.app.core.data.OverlayConfig
import com.krono.app.core.data.TimerPreferences
import com.krono.app.core.tool.ToolViewModel
import com.krono.app.core.util.prepareSecondTickFeedback
import com.krono.app.core.util.stopActiveTimerSounds
import com.krono.app.core.util.triggerSecondFeedback
import com.krono.app.feature.stopwatch.StopwatchState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * StopwatchViewModel: Implementação do cronômetro seguindo a nova arquitetura modular.
 */
class StopwatchViewModel(application: Application) : AndroidViewModel(application), ToolViewModel {

    private val timerPreferences = TimerPreferences(application)
    private val overlayDataStore = OverlayDataStore(application)

    private val _stopwatchState = MutableStateFlow(loadInitialState())
    
    // Implementação da interface ToolViewModel
    override val toolState: StateFlow<StopwatchState> = _stopwatchState.asStateFlow()

    // Para compatibilidade temporária com MainService/OverlayManager enquanto eles não são migrados para ToolViewModel
    val StopwatchState: StateFlow<StopwatchState> = toolState

    val currentSessionMs: Long
        get() {
            val current = _stopwatchState.value
            return when {
                current.isRunning && current.startTime != -1L ->
                    current.pauseOffset + (System.currentTimeMillis() - current.startTime)
                else ->
                    current.pauseOffset
            }
        }

    private var timerJob: Job? = null
    private var lastSecondFeedback: Long? = null
    private var latestConfig: OverlayConfig = OverlayConfig()
    init {
        viewModelScope.launch {
            overlayDataStore.configFlow.collect { latestConfig = it }
        }
        if (_stopwatchState.value.isRunning) {
            prepareSecondTickFeedback(
                context = getApplication(),
                tickSoundEnabled = latestConfig.allSoundsEnabled && latestConfig.tickSoundEnabled,
                environmentSoundType = latestConfig.environmentSoundType
            )
            startUpdateLoop()
        }
    }

    private fun loadInitialState(): StopwatchState {
        val saved = timerPreferences.loadState()
        val elapsed = computeElapsed(saved)
        return StopwatchState(
            startTime = saved.startTime,
            pauseOffset = saved.pauseOffset,
            isRunning = saved.isRunning,
            isAtLimit = saved.isAtLimit,
            elapsedMs = elapsed
        )
    }

    private fun computeElapsed(state: StopwatchState): Long = when {
        state.isAtLimit -> state.pauseOffset
        state.isRunning && state.startTime != -1L ->
            state.pauseOffset + (System.currentTimeMillis() - state.startTime)
        else -> state.pauseOffset
    }

    override fun start() {
        val current = _stopwatchState.value
        if (current.isRunning || current.isAtLimit) return

        val newState = current.copy(
            startTime = System.currentTimeMillis(),
            isRunning = true
        )
        _stopwatchState.value = newState
        timerPreferences.saveState(newState.toStopwatchState())
        lastSecondFeedback = null
        prepareSecondTickFeedback(
            context = getApplication(),
            tickSoundEnabled = latestConfig.allSoundsEnabled && latestConfig.tickSoundEnabled,
            environmentSoundType = latestConfig.environmentSoundType
        )
        startUpdateLoop()
    }

    override fun pause() {
        val current = _stopwatchState.value
        if (!current.isRunning) {
            stopActiveTimerSounds("stopwatch pause idle")
            return
        }
        stopActiveTimerSounds("stopwatch pause")

        val now = System.currentTimeMillis()
        val accumulated = current.pauseOffset + (now - current.startTime)
        val newState = current.copy(
            startTime = -1L,
            pauseOffset = accumulated,
            isRunning = false,
            elapsedMs = accumulated
        )
        _stopwatchState.value = newState
        timerPreferences.saveState(newState.toStopwatchState())
        stopUpdateLoop()
    }

    override fun reset() {
        stopActiveTimerSounds("stopwatch reset")
        val elapsedToAccumulate = _stopwatchState.value.elapsedMs.coerceAtLeast(0L)
        stopUpdateLoop()
        val newState = StopwatchState()
        _stopwatchState.value = newState
        lastSecondFeedback = null
        timerPreferences.clearState()
        if (elapsedToAccumulate > 0L) {
            viewModelScope.launch {
                overlayDataStore.accumulateTime(elapsedToAccumulate)
            }
        }
    }

    private fun startUpdateLoop() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                val current = _stopwatchState.value
                if (!current.isRunning) break

                val elapsed = current.pauseOffset + (System.currentTimeMillis() - current.startTime)
                val second = elapsed / 1000L
                if (second > 0L && lastSecondFeedback != second) {
                    lastSecondFeedback = second
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

                _stopwatchState.value = current.copy(elapsedMs = elapsed)

                val now = System.currentTimeMillis()
                val nextSecondAt = current.startTime + (((elapsed / 1000L) + 1L) * 1000L) - current.pauseOffset
                val nextFrameAt = if (latestConfig.showMilliseconds) now + 50L else nextSecondAt
                delay((minOf(nextSecondAt, nextFrameAt) - System.currentTimeMillis()).coerceAtLeast(1L))
            }
        }
    }

    private fun stopUpdateLoop() {
        timerJob?.cancel()
        timerJob = null
    }

    override fun onCleared() {
        super.onCleared()
        timerPreferences.saveStateSync(_stopwatchState.value.toStopwatchState())
        stopUpdateLoop()
        stopActiveTimerSounds("stopwatch cleared")
    }

    // Helper para converter para o modelo de dados legado de preferências
    private fun StopwatchState.toStopwatchState() = StopwatchState(
        startTime = this.startTime,
        pauseOffset = this.pauseOffset,
        isRunning = this.isRunning,
        isAtLimit = this.isAtLimit,
        elapsedMs = this.elapsedMs
    )
}

