package com.krono.app.core.data

import java.util.Locale

// ============================================================
// TimeUtils.kt
// Funções utilitárias de formatação de tempo.
// ============================================================

fun Long.toFormattedTime(
    showHours: Boolean = true,
    showSeconds: Boolean = true
): String {
    val totalSeconds = this / 1000L
    val hours        = totalSeconds / 3600L
    val minutes      = (totalSeconds % 3600L) / 60L
    val seconds      = totalSeconds % 60L

    return when {
        showHours && showSeconds ->
            String.format(Locale.ROOT, "%02d:%02d:%02d", hours, minutes, seconds)

        !showHours && showSeconds -> {
            val totalMinutes = totalSeconds / 60L
            val secs         = totalSeconds % 60L
            String.format(Locale.ROOT, "%02d:%02d", totalMinutes, secs)
        }

        showHours && !showSeconds ->
            String.format(Locale.ROOT, "%02d:%02d", hours, minutes)

        else -> {
            val totalMinutes = totalSeconds / 60L
            String.format(Locale.ROOT, "%02d", totalMinutes)
        }
    }
}

// Alias para facilitar o uso nos patches
object TimeUtils {
    /**
     * Formata segundos como HH:MM:SS.
     * Horas sempre 2 dígitos (ex: 01:05:09).
     * Suporta até 99:59:59.
     */
    fun formatSeconds(totalSeconds: Long): String {
        val h = (totalSeconds / 3600).coerceIn(0, 99)
        val m = (totalSeconds % 3600) / 60
        val s = totalSeconds % 60
        return String.format(Locale.ROOT, "%02d:%02d:%02d", h, m, s)
    }
}

// Formata milissegundos em "HHh MMm SSs" para exibição no diálogo de doação.
fun formatLifetimeDetailed(totalMs: Long): String {
    if (totalMs <= 0L) return "0h 00m 00s"
    val totalSeconds = totalMs / 1000L
    val h = totalSeconds / 3600L
    val m = (totalSeconds % 3600L) / 60L
    val s = totalSeconds % 60L
    return String.format(Locale.ROOT, "%dh %02dm %02ds", h, m, s)
}

