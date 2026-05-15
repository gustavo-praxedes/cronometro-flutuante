package com.krono.app.feature.pomodoro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krono.app.core.tool.ToolState
import com.krono.app.core.tool.ToolViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PomodoroViewModel : ViewModel(), ToolViewModel {
    companion object {
        private const val FOCUS_SECONDS = 25 * 60L
        private const val BREAK_SECONDS = 5 * 60L
    }

    private val _state = MutableStateFlow(PomodoroState())
    val state: StateFlow<PomodoroState> = _state.asStateFlow()
    override val toolState: StateFlow<ToolState> = state
    private var tickerJob: Job? = null
    private var focusSeconds = FOCUS_SECONDS
    private var breakSeconds = BREAK_SECONDS
    private var autoStartBreak = true
    private var autoStartFocus = true
    private var currentPresetKey = "CLASSICO"

    override fun start() {
        if (_state.value.isRunning) return
        _state.value = _state.value.copy(isRunning = true)
        tickerJob?.cancel()
        tickerJob = viewModelScope.launch {
            while (_state.value.isRunning) {
                delay(1000L)
                val next = (_state.value.remainingSeconds - 1).coerceAtLeast(0L)
                if (next == 0L) {
                    val inFocus = _state.value.phaseLabel == "Foco"
                    val shouldAutoStart = if (inFocus) autoStartBreak else autoStartFocus
                    _state.value = _state.value.copy(
                        remainingSeconds = if (inFocus) breakSeconds else focusSeconds,
                        phaseLabel = if (inFocus) "Pausa" else "Foco",
                        cycle = if (inFocus) _state.value.cycle else _state.value.cycle + 1,
                        phaseTransitionId = _state.value.phaseTransitionId + 1,
                        isRunning = shouldAutoStart
                    )
                    if (!shouldAutoStart) {
                        tickerJob?.cancel()
                        break
                    }
                } else {
                    _state.value = _state.value.copy(remainingSeconds = next)
                }
            }
        }
    }

    override fun pause() {
        _state.value = _state.value.copy(isRunning = false)
        tickerJob?.cancel()
    }

    override fun reset() {
        _state.value = PomodoroState()
        tickerJob?.cancel()
    }

    fun skipPhase() {
        val inFocus = _state.value.phaseLabel == "Foco"
        _state.value = _state.value.copy(
            remainingSeconds = if (inFocus) breakSeconds else focusSeconds,
            phaseLabel = if (inFocus) "Pausa" else "Foco",
            cycle = if (inFocus) _state.value.cycle else _state.value.cycle + 1,
            phaseTransitionId = _state.value.phaseTransitionId + 1
        )
    }

    fun applyPreset(presetKey: String) {
        if (presetKey == currentPresetKey) return
        val (nextFocus, nextBreak) = when (presetKey) {
            "CURTO" -> 15 * 60L to 5 * 60L
            "LONGO" -> 50 * 60L to 10 * 60L
            else -> FOCUS_SECONDS to BREAK_SECONDS // CLASSICO
        }
        currentPresetKey = presetKey
        focusSeconds = nextFocus
        breakSeconds = nextBreak
        _state.value = _state.value.copy(
            remainingSeconds = if (_state.value.phaseLabel == "Foco") focusSeconds else breakSeconds,
            isRunning = false
        )
        tickerJob?.cancel()
    }

    fun setAutoAdvance(autoBreak: Boolean, autoFocus: Boolean) {
        autoStartBreak = autoBreak
        autoStartFocus = autoFocus
    }
}
