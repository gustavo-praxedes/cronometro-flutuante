package com.krono.app.feature.pomodoro

internal fun PomodoroPresetConfig.executionPhases(): List<PomodoroPhaseConfig> =
    items.flatMap { item ->
        when (item) {
            is PomodoroPresetItem.Card -> listOf(item.phase)
            is PomodoroPresetItem.Group -> List(item.cycles.coerceIn(1, 12)) { item.phases }.flatten()
        }
    }
