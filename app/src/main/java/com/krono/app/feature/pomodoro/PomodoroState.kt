package com.krono.app.feature.pomodoro

import com.krono.app.core.tool.ToolState

data class PomodoroState(
    val remainingSeconds: Long = 25 * 60L,
    val remainingMs: Long = remainingSeconds * 1000L,
    val phaseLabel: String = "Foco",
    val phaseColor: Int = 0xFFEF4444.toInt(),
    val phaseSoundType: String = "krono_alm_alarmbeep",
    val isFocusPhase: Boolean = true,
    val cycle: Int = 1,
    val phaseTransitionId: Long = 0L,
    override val isRunning: Boolean = false
) : ToolState {
    override val isAtLimit: Boolean get() = remainingSeconds <= 0L
    override val elapsedMs: Long get() = (25 * 60L * 1000L - remainingMs).coerceAtLeast(0L)
}
