package com.krono.app.feature.pomodoro

import androidx.compose.runtime.Stable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Stable
@Serializable
sealed class PomodoroPresetItem {
    @Stable
    @Serializable
    @SerialName("card")
    data class Card(
        val phase: PomodoroPhaseConfig
    ) : PomodoroPresetItem()

    @Stable
    @Serializable
    @SerialName("group")
    data class Group(
        val id: String,
        val label: String,
        val cycles: Int,
        val phases: List<PomodoroPhaseConfig>
    ) : PomodoroPresetItem()
}

val PomodoroPresetItem.id: String
    get() = when (this) {
        is PomodoroPresetItem.Card -> phase.id
        is PomodoroPresetItem.Group -> id
    }
