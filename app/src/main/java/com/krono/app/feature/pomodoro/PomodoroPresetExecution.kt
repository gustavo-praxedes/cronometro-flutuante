package com.krono.app.feature.pomodoro

internal fun PomodoroPresetConfig.executionPhases(): List<PomodoroPhaseConfig> =
    items.flatMap { item ->
        when (item) {
            is PomodoroPresetItem.Card -> listOf(item.phase)
            is PomodoroPresetItem.Group -> List(item.cycles.coerceIn(1, 12)) { item.phases }.flatten()
        }
    }

internal fun PomodoroPresetConfig.legacyPhasesSpec(): String =
    executionPhases().joinToString(";") { phase ->
        "${phase.label}|${(phase.totalSeconds / 60L).coerceAtLeast(1L)}|${phase.color.toLong()}|${phase.soundType}"
    }
