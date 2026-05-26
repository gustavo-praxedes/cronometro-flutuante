package com.krono.app.feature.countdown

import com.krono.app.core.tool.ToolState

data class CountdownState(
    val config: CountdownConfig,
    val remainingSeconds: Long = config.totalSeconds,
    val remainingMs: Long = remainingSeconds * 1000L,
    override val isRunning: Boolean = false,
    val isCompleted: Boolean = false,
    val isOverlayVisible: Boolean = false
) : ToolState {
    override val isAtLimit: Boolean get() = isCompleted
    override val elapsedMs: Long get() = ((config.totalSeconds * 1000L) - remainingMs).coerceAtLeast(0L)
}
