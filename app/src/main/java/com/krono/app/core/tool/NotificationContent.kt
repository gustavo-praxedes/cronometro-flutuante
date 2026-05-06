package com.krono.app.core.tool

/**
 * Representa os dados necessários para exibir uma notificação de ferramenta.
 */
data class NotificationContent(
    val title: String,
    val text: String,
    val isRunning: Boolean,
    val isCompleted: Boolean = false,
    val startTime: Long = -1L,
    val pauseOffset: Long = 0L,
    val elapsedMs: Long = 0L
)
