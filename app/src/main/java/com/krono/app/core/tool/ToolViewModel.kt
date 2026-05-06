package com.krono.app.core.tool

import kotlinx.coroutines.flow.StateFlow

/**
 * Interface base para ViewModels de ferramentas.
 */
interface ToolViewModel {
    val toolState: StateFlow<ToolState>
    fun start()
    fun pause()
    fun reset()
}
