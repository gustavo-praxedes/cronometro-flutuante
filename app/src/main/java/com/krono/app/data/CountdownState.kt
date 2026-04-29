package com.krono.app.data

data class CountdownState(
    val config: CountdownConfig,
    val remainingSeconds: Long = config.totalSeconds,
    val isRunning: Boolean = false,
    val isCompleted: Boolean = false,
    val isOverlayVisible: Boolean = false
)
