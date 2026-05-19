package com.krono.app.feature.pomodoro

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class PomodoroPhaseConfig(
    val id: String,
    val label: String,
    val totalSeconds: Long,
    val color: Int,
    val soundType: String
)

@Serializable
data class PomodoroPresetConfig(
    val id: String,
    val name: String,
    val cycles: Int,
    val phases: List<PomodoroPhaseConfig>,
    val isBuiltIn: Boolean = false
)

object PomodoroPresetCatalog {
    const val DEFAULT_ID = "DEFAULT"
    const val LONG_ID = "LONG"
    const val SHORT_ID = "SHORT"

    private val json = Json { ignoreUnknownKeys = true }

    fun decode(
        raw: String,
        legacyCustomName: String,
        legacyCustomSpec: String,
        legacyCustomCycles: Int
    ): List<PomodoroPresetConfig> {
        val decoded = runCatching {
            json.decodeFromString<List<PomodoroPresetConfig>>(raw)
        }.getOrElse { emptyList() }
        if (decoded.isEmpty()) {
            return defaults().let { base ->
                val legacyUser = legacyCustomToPreset(legacyCustomName, legacyCustomSpec, legacyCustomCycles)
                if (legacyUser == null) base else base + legacyUser
            }
        }

        val byId = decoded.associateBy { it.id }.toMutableMap()
        if (!byId.containsKey(DEFAULT_ID)) byId[DEFAULT_ID] = defaults().first { it.id == DEFAULT_ID }
        if (!byId.containsKey(LONG_ID)) byId[LONG_ID] = defaults().first { it.id == LONG_ID }
        if (!byId.containsKey(SHORT_ID)) byId[SHORT_ID] = defaults().first { it.id == SHORT_ID }

        return byId.values
            .map { preset ->
                preset.copy(
                    cycles = preset.cycles.coerceAtLeast(1),
                    phases = ensureRequiredPhases(
                        preset.phases.map { phase ->
                            phase.copy(
                                label = phase.label.ifBlank { "Etapa" },
                                totalSeconds = phase.totalSeconds.coerceAtLeast(1L),
                                soundType = phase.soundType.ifBlank { "FOCUS_A" }
                            )
                        }
                    )
                )
            }
            .sortedBy { sortIndex(it.id) }
    }

    fun encode(presets: List<PomodoroPresetConfig>): String = json.encodeToString(presets)

    fun defaults(): List<PomodoroPresetConfig> = listOf(
        PomodoroPresetConfig(
            id = DEFAULT_ID,
            name = "Pomodoro Padrão",
            cycles = 4,
            phases = listOf(
                PomodoroPhaseConfig("p1", "Foco", 25 * 60L, 0xFFEF4444.toInt(), "FOCUS_A"),
                PomodoroPhaseConfig("p2", "Pausa", 5 * 60L, 0xFF22C55E.toInt(), "BREAK_A")
            ),
            isBuiltIn = true
        ),
        PomodoroPresetConfig(
            id = LONG_ID,
            name = "Pomodoro Longo",
            cycles = 4,
            phases = listOf(
                PomodoroPhaseConfig("p1", "Foco", 50 * 60L, 0xFFEF4444.toInt(), "FOCUS_A"),
                PomodoroPhaseConfig("p2", "Pausa", 10 * 60L, 0xFF22C55E.toInt(), "BREAK_A")
            ),
            isBuiltIn = true
        ),
        PomodoroPresetConfig(
            id = SHORT_ID,
            name = "Pomodoro Curto",
            cycles = 4,
            phases = listOf(
                PomodoroPhaseConfig("p1", "Foco", 15 * 60L, 0xFFEF4444.toInt(), "FOCUS_A"),
                PomodoroPhaseConfig("p2", "Pausa", 5 * 60L, 0xFF22C55E.toInt(), "BREAK_A")
            ),
            isBuiltIn = true
        )
    )

    fun newUserPresetTemplate(index: Int): PomodoroPresetConfig {
        val safeIndex = index.coerceAtLeast(1)
        return PomodoroPresetConfig(
            id = "USR_$safeIndex",
            name = "Preset $safeIndex",
            cycles = 4,
            phases = listOf(
                PomodoroPhaseConfig("p1", "Foco", 25 * 60L, 0xFFEF4444.toInt(), "FOCUS_A"),
                PomodoroPhaseConfig("p2", "Pausa", 5 * 60L, 0xFF22C55E.toInt(), "BREAK_A")
            ),
            isBuiltIn = false
        )
    }

    fun ensureRequiredPhases(phases: List<PomodoroPhaseConfig>): List<PomodoroPhaseConfig> {
        if (phases.size >= 2) return phases
        val filled = phases.toMutableList()
        while (filled.size < 2) {
            val index = filled.size + 1
            val isFocus = index % 2 == 1
            filled.add(
                PomodoroPhaseConfig(
                    id = "p$index",
                    label = if (isFocus) "Foco" else "Pausa",
                    totalSeconds = if (isFocus) 25 * 60L else 5 * 60L,
                    color = if (isFocus) 0xFFEF4444.toInt() else 0xFF22C55E.toInt(),
                    soundType = if (isFocus) "FOCUS_A" else "BREAK_A"
                )
            )
        }
        return filled
    }

    private fun legacyCustomToPreset(
        legacyName: String,
        legacySpec: String,
        legacyCycles: Int
    ): PomodoroPresetConfig? {
        if (legacySpec.isBlank()) return null
        val legacyPhases = legacySpec.split(";").mapNotNull { row ->
            val parts = row.split("|")
            if (parts.size < 4) return@mapNotNull null
            val label = parts[0].ifBlank { "Etapa" }
            val minutes = parts[1].toLongOrNull()?.coerceAtLeast(1L) ?: 1L
            val color = parts[2].toLongOrNull()?.toInt() ?: 0xFFEF4444.toInt()
            val sound = parts[3].ifBlank { "FOCUS_A" }
            PomodoroPhaseConfig(
                id = "p${parts.hashCode().toUInt()}",
                label = label,
                totalSeconds = minutes * 60L,
                color = color,
                soundType = sound
            )
        }
        if (legacyPhases.isEmpty()) return null
        return PomodoroPresetConfig(
            id = "USR_1",
            name = legacyName.ifBlank { "Meu Preset" },
            cycles = legacyCycles.coerceAtLeast(1),
            phases = ensureRequiredPhases(legacyPhases),
            isBuiltIn = false
        )
    }

    private fun sortIndex(id: String): Int = when (id) {
        DEFAULT_ID -> 0
        LONG_ID -> 1
        SHORT_ID -> 2
        else -> 100
    }
}
