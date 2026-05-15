package com.krono.app.feature.stopwatch

import com.krono.app.core.tool.ToolState

/**
 * Estado do cronômetro (Stopwatch).
 */
data class StopwatchState(
    val startTime   : Long    = -1L,
    val pauseOffset : Long    = 0L,
    override val isRunning   : Boolean = false,
    override val isAtLimit   : Boolean = false,
    override val elapsedMs   : Long    = 0L
) : ToolState

