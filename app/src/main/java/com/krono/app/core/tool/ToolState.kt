package com.krono.app.core.tool

/**
 * Representa o estado básico de uma ferramenta de tempo (cronômetro, contagem regressiva, etc).
 */
interface ToolState {
    val isRunning: Boolean
    val isAtLimit: Boolean
    val elapsedMs: Long
}

