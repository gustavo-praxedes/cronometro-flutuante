package com.krono.app.core.tool

/**
 * Callbacks para interações do usuário com uma ferramenta.
 */
interface ToolCallbacks {
    fun onPlay()
    fun onPause()
    fun onReset()
    fun onClose()
    fun onSettings()
    fun onFocusModeStarted()
}
