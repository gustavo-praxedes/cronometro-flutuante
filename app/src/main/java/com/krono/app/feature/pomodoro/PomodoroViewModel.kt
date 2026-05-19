package com.krono.app.feature.pomodoro

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.krono.app.core.data.OverlayDataStore
import com.krono.app.core.tool.ToolState
import com.krono.app.core.tool.ToolViewModel
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
    private var currentPresetKey = "CLASSICO"
    private var currentPresetsSpecRaw = ""
    private var maxCycles = 4
    private var customPhases: List<PhaseDef> = defaultPhases()
    private var currentPhaseIndex = 0
    private var phaseDeadlineElapsedMs: Long? = null

    private data class PhaseDef(
        val label: String,
        val seconds: Long,
        val color: Int,
        val soundType: String
    )

    private fun defaultPhases() = listOf(
        PhaseDef("Foco", focusSeconds, 0xFFEF4444.toInt(), "FOCUS_A"),
        PhaseDef("Pausa", breakSeconds, 0xFF22C55E.toInt(), "BREAK_A")
    )

    private fun parseCustomPhases(spec: String): List<PhaseDef> {
        return spec.split(";")
            .mapNotNull { row ->
                val p = row.split("|")
                if (p.size < 4) return@mapNotNull null
                val label = p[0].ifBlank { "Etapa" }
                val minutes = p[1].toLongOrNull()?.coerceAtLeast(1L) ?: 1L
                val color = p[2].toLongOrNull()?.toInt() ?: 0xFFEF4444.toInt()
                val sound = p[3].ifBlank { "FOCUS_A" }
                PhaseDef(label, minutes * 60L, color, sound)
            }
            .ifEmpty { listOf(PhaseDef("Etapa 1", 25 * 60L, 0xFFEF4444.toInt(), "FOCUS_A")) }
    }

    override fun start() {
        if (_state.value.isRunning) return
        _state.value = _state.value.copy(isRunning = true)
        phaseDeadlineElapsedMs = SystemClock.elapsedRealtime() + (_state.value.remainingSeconds * 1000L)
        tickerJob?.cancel()
        tickerJob = viewModelScope.launch {
            while (_state.value.isRunning) {
                delay(250L)
                val deadline = phaseDeadlineElapsedMs ?: (SystemClock.elapsedRealtime() + (_state.value.remainingSeconds * 1000L))
                val now = SystemClock.elapsedRealtime()
                val next = ((deadline - now) / 1000L).coerceAtLeast(0L)
                if (next == 0L) {
                    val completedPhase = customPhases[currentPhaseIndex]
                    overlayDataStore.accumulateTime(completedPhase.seconds.coerceAtLeast(0L) * 1000L)
                    val nextIndex = (currentPhaseIndex + 1) % customPhases.size
                    val wrapped = nextIndex == 0
                    if (wrapped) {
                        val nextCycle = (_state.value.cycle % maxCycles) + 1
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
                        phaseLabel = phase.label,
                        phaseColor = phase.color,
                        phaseSoundType = phase.soundType,
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
        val deadline = phaseDeadlineElapsedMs
        if (deadline != null) {
            val now = SystemClock.elapsedRealtime()
            val remaining = ((deadline - now) / 1000L).coerceAtLeast(0L)
            _state.value = _state.value.copy(remainingSeconds = remaining)
        }
        phaseDeadlineElapsedMs = null
        _state.value = _state.value.copy(isRunning = false)
        tickerJob?.cancel()
    }

    override fun reset() {
        currentPhaseIndex = 0
        val phase = customPhases.firstOrNull() ?: PhaseDef("Foco", focusSeconds, 0xFFEF4444.toInt(), "FOCUS_A")
        _state.value = PomodoroState(
            remainingSeconds = phase.seconds,
            phaseLabel = phase.label,
            phaseColor = phase.color,
            phaseSoundType = phase.soundType
        )
        phaseDeadlineElapsedMs = null
        tickerJob?.cancel()
    }

    fun skipPhase() {
        currentPhaseIndex = (currentPhaseIndex + 1) % customPhases.size
        val wrapped = currentPhaseIndex == 0
        val phase = customPhases[currentPhaseIndex]
        _state.value = _state.value.copy(
            remainingSeconds = phase.seconds,
            phaseLabel = phase.label,
            phaseColor = phase.color,
            phaseSoundType = phase.soundType,
            cycle = if (wrapped) (_state.value.cycle % maxCycles) + 1 else _state.value.cycle,
            phaseTransitionId = _state.value.phaseTransitionId + 1
        )
        if (_state.value.isRunning) {
            phaseDeadlineElapsedMs = SystemClock.elapsedRealtime() + (phase.seconds * 1000L)
        }
    }

    fun applyPreset(
        presetKey: String,
        presetsSpec: String = "",
        customFocusMinutes: Int = 25,
        customBreakMinutes: Int = 5,
        customCycles: Int = 4,
        customPhasesSpec: String = ""
    ) {
        val normalizedFocus = customFocusMinutes.coerceAtLeast(1)
        val normalizedBreak = customBreakMinutes.coerceAtLeast(1)
        val normalizedCycles = customCycles.coerceAtLeast(1)
        val normalizedSpec = customPhasesSpec.trim()
        val availablePresets = PomodoroPresetCatalog.decode(
            raw = presetsSpec,
            legacyCustomName = "Meu Preset",
            legacyCustomSpec = customPhasesSpec,
            legacyCustomCycles = customCycles
        )
        val selectedPreset = availablePresets.firstOrNull { it.id == presetKey }
        if (
            presetKey == currentPresetKey &&
            presetsSpec == currentPresetsSpecRaw &&
            normalizedCycles == maxCycles &&
            normalizedFocus == focusSeconds.toInt() / 60 &&
            normalizedBreak == breakSeconds.toInt() / 60 &&
            normalizedSpec == customPhases.joinToString(";") { "${it.label}|${(it.seconds / 60L).coerceAtLeast(1L)}|${it.color.toLong()}|${it.soundType}" }
        ) return

        customPhases = if (selectedPreset != null) {
            selectedPreset.phases.map { phase ->
                PhaseDef(
                    label = phase.label,
                    seconds = phase.totalSeconds.coerceAtLeast(1L),
                    color = phase.color,
                    soundType = phase.soundType
                )
            }
        } else {
            when (presetKey) {
                "CURTO" -> listOf(
                    PhaseDef("Foco", 15 * 60L, 0xFFEF4444.toInt(), "FOCUS_A"),
                    PhaseDef("Pausa", 5 * 60L, 0xFF22C55E.toInt(), "BREAK_A")
                )
                "LONGO" -> listOf(
                    PhaseDef("Foco", 50 * 60L, 0xFFEF4444.toInt(), "FOCUS_A"),
                    PhaseDef("Pausa", 10 * 60L, 0xFF22C55E.toInt(), "BREAK_A")
                )
                "CUSTOM" -> if (customPhasesSpec.isNotBlank()) parseCustomPhases(customPhasesSpec) else listOf(
                    PhaseDef("Foco", normalizedFocus.toLong() * 60L, 0xFFEF4444.toInt(), "FOCUS_A"),
                    PhaseDef("Pausa", normalizedBreak.toLong() * 60L, 0xFF22C55E.toInt(), "BREAK_A")
                )
                else -> listOf(
                    PhaseDef("Foco", FOCUS_SECONDS, 0xFFEF4444.toInt(), "FOCUS_A"),
                    PhaseDef("Pausa", BREAK_SECONDS, 0xFF22C55E.toInt(), "BREAK_A")
                )
            }
        }
        maxCycles = selectedPreset?.cycles?.coerceAtLeast(1) ?: normalizedCycles
        currentPresetKey = presetKey
        currentPresetsSpecRaw = presetsSpec
        if (_state.value.isRunning) return
        currentPhaseIndex = 0
        val phase = customPhases.first()
        focusSeconds = phase.seconds
        breakSeconds = customPhases.getOrNull(1)?.seconds ?: phase.seconds
        _state.value = _state.value.copy(
            remainingSeconds = phase.seconds,
            phaseLabel = phase.label,
            phaseColor = phase.color,
            phaseSoundType = phase.soundType,
            isRunning = false
        )
        phaseDeadlineElapsedMs = null
        tickerJob?.cancel()
    }

    fun setAutoAdvance(autoNextCycle: Boolean) {
        autoStartNextCycle = autoNextCycle
    }
}
