package com.krono.app.core.data

import java.util.Locale

enum class TimerDisplayFormat(val key: String, val label: String) {
    HH_MM_SS("HH_MM_SS", "HH:MM:SS"),
    MM_SS("MM_SS", "MM:SS"),
    HH_MM("HH_MM", "HH:MM");

    companion object {
        fun fromKey(value: String): TimerDisplayFormat =
            entries.find { it.key == value } ?: HH_MM_SS
    }
}

fun formatMillisByPattern(ms: Long, format: TimerDisplayFormat): String {
    val totalSeconds = (ms / 1000L).coerceAtLeast(0L)
    val h = totalSeconds / 3600L
    val m = (totalSeconds % 3600L) / 60L
    val s = totalSeconds % 60L
    return when (format) {
        TimerDisplayFormat.HH_MM_SS -> String.format(Locale.ROOT, "%02d:%02d:%02d", h, m, s)
        TimerDisplayFormat.MM_SS -> String.format(Locale.ROOT, "%02d:%02d", totalSeconds / 60L, s)
        TimerDisplayFormat.HH_MM -> String.format(Locale.ROOT, "%02d:%02d", h, m)
    }
}

fun formatSecondsByPattern(totalSecondsInput: Long, format: TimerDisplayFormat): String {
    val totalSeconds = totalSecondsInput.coerceAtLeast(0L)
    val h = totalSeconds / 3600L
    val m = (totalSeconds % 3600L) / 60L
    val s = totalSeconds % 60L
    return when (format) {
        TimerDisplayFormat.HH_MM_SS -> String.format(Locale.ROOT, "%02d:%02d:%02d", h, m, s)
        TimerDisplayFormat.MM_SS -> String.format(Locale.ROOT, "%02d:%02d", totalSeconds / 60L, s)
        TimerDisplayFormat.HH_MM -> String.format(Locale.ROOT, "%02d:%02d", h, m)
    }
}
