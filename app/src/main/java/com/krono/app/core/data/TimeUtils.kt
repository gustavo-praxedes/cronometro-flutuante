package com.krono.app.core.data

import java.util.Locale

// ============================================================
// TimeUtils.kt
// Funções utilitárias de formatação de tempo.
// ============================================================

fun Long.toFormattedTime(
    showHours: Boolean = true,
    showMinutes: Boolean = true,
    showSeconds: Boolean = true
): String {
    val totalSeconds = (this / 1000L).coerceAtLeast(0L)
    val hours = totalSeconds / 3600L
    val minutes = (totalSeconds % 3600L) / 60L
    val totalMinutes = totalSeconds / 60L
    val seconds = totalSeconds % 60L
    val parts = buildList {
        if (showHours) add(String.format(Locale.ROOT, "%02d", hours))
        if (showMinutes) add(String.format(Locale.ROOT, "%02d", if (showHours) minutes else totalMinutes))
        if (showSeconds) add(String.format(Locale.ROOT, "%02d", seconds))
    }
    return parts.ifEmpty { listOf("00") }.joinToString(":")
}

fun Long.toOverlayFormattedTime(
    showHours: Boolean = true,
    showMinutes: Boolean = true,
    showSeconds: Boolean = true,
    showMilliseconds: Boolean = false
): String {
    val totalMs = coerceAtLeast(0L)
    val millis = totalMs % 1000L
    val hasBaseFields = showHours || showMinutes || showSeconds
    if (!hasBaseFields) {
        return if (showMilliseconds) {
            String.format(Locale.ROOT, "%03d", millis)
        } else {
            "00"
        }
    }
    val base = totalMs.toFormattedTime(
        showHours = showHours,
        showMinutes = showMinutes,
        showSeconds = showSeconds
    )
    if (!showMilliseconds) return base
    return String.format(Locale.ROOT, "%s.%03d", base, millis)
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

