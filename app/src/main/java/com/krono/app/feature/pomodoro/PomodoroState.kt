package com.krono.app.feature.pomodoro

import com.krono.app.core.tool.ToolState

data class PomodoroState(
    val remainingSeconds: Long = 25 * 60L,
    val phaseLabel: String = "Foco",
    val phaseColor: Int = 0xFFEF4444.toInt(),
    val phaseSoundType: String = "FOCUS_A",
    val cycle: Int = 1,
    val phaseTransitionId: Long = 0L,
    override val isRunning: Boolean = false
) : ToolState {
    override val isAtLimit: Boolean get() = remainingSeconds <= 0L
    override val elapsedMs: Long get() = (25 * 60L - remainingSeconds) * 1000L
}
