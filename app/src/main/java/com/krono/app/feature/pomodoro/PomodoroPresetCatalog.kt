package com.krono.app.feature.pomodoro

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Stable
@Serializable
data class PomodoroPhaseConfig(
    val id: String,
    val label: String,
    val totalSeconds: Long,
    val color: Int,
    val soundType: String
)

@Stable
@Serializable
data class PomodoroPresetConfig(
    val id: String,
    val name: String,
    val cycles: Int,
    val items: List<PomodoroPresetItem> = emptyList(),
    val isBuiltIn: Boolean = false
)

object PomodoroPresetCatalog {
    const val DEFAULT_ID = "DEFAULT"
    const val LONG_ID = "LONG"
    const val SHORT_ID = "SHORT"

    private val json = Json {
        ignoreUnknownKeys = true
        classDiscriminator = "type"
    }

    fun decode(
        raw: String,
        legacyCustomName: String,
        legacyCustomSpec: String,
        legacyCustomCycles: Int
    ): List<PomodoroPresetConfig> {
        val decoded = decodePresets(raw)
        if (decoded.isEmpty()) {
            return defaults().let { base ->
                val legacyUser = legacyCustomToPreset(legacyCustomName, legacyCustomSpec, legacyCustomCycles)
                if (legacyUser == null) base else base + legacyUser
            }
        }

        val byId = decoded.associateBy { it.id }.toMutableMap()
        defaults().forEach { preset ->
            if (!byId.containsKey(preset.id)) byId[preset.id] = preset
        }

        return byId.values
            .map(::sanitizePreset)
            .sortedBy { sortIndex(it.id) }
    }

    fun encode(presets: List<PomodoroPresetConfig>): String = json.encodeToString(presets.map(::sanitizePreset))

    fun defaults(): List<PomodoroPresetConfig> = listOf(
        PomodoroPresetConfig(
            id = DEFAULT_ID,
            name = "Pomodoro Padrão",
            cycles = 4,
            items = listOf(
                PomodoroPresetItem.Card(PomodoroPhaseConfig("p1", "Foco", 25 * 60L, 0xFFEF4444.toInt(), "krono_alm_alarmbeep")),
                PomodoroPresetItem.Card(PomodoroPhaseConfig("p2", "Pausa", 5 * 60L, 0xFF22C55E.toInt(), "krono_alm_beeps"))
            ),
            isBuiltIn = true
        ),
        PomodoroPresetConfig(
            id = LONG_ID,
            name = "Pomodoro Longo",
            cycles = 4,
            items = listOf(
                PomodoroPresetItem.Card(PomodoroPhaseConfig("p1", "Foco", 50 * 60L, 0xFFEF4444.toInt(), "krono_alm_alarmbeep")),
                PomodoroPresetItem.Card(PomodoroPhaseConfig("p2", "Pausa", 10 * 60L, 0xFF22C55E.toInt(), "krono_alm_beeps"))
            ),
            isBuiltIn = true
        ),
        PomodoroPresetConfig(
            id = SHORT_ID,
            name = "Pomodoro Curto",
            cycles = 4,
            items = listOf(
                PomodoroPresetItem.Card(PomodoroPhaseConfig("p1", "Foco", 15 * 60L, 0xFFEF4444.toInt(), "krono_alm_alarmbeep")),
                PomodoroPresetItem.Card(PomodoroPhaseConfig("p2", "Pausa", 5 * 60L, 0xFF22C55E.toInt(), "krono_alm_beeps"))
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
            items = listOf(defaultCard(1), defaultCard(2, isFocus = false)),
            isBuiltIn = false
        )
    }

    fun defaultCard(index: Int, isFocus: Boolean = index % 2 == 1): PomodoroPresetItem.Card {
        val safeIndex = index.coerceAtLeast(1)
        return PomodoroPresetItem.Card(
            PomodoroPhaseConfig(
                id = "p$safeIndex",
                label = if (isFocus) "Foco" else "Pausa",
                totalSeconds = if (isFocus) 25 * 60L else 5 * 60L,
                color = if (isFocus) 0xFFEF4444.toInt() else 0xFF22C55E.toInt(),
                soundType = if (isFocus) "krono_alm_alarmbeep" else "krono_alm_beeps"
            )
        )
    }

    fun defaultGroup(index: Int): PomodoroPresetItem.Group {
        val safeIndex = index.coerceAtLeast(1)
        return PomodoroPresetItem.Group(
            id = "g$safeIndex",
            label = "Grupo $safeIndex",
            cycles = 1,
            phases = emptyList()
        )
    }

    private fun decodePresets(raw: String): List<PomodoroPresetConfig> {
        if (raw.isBlank()) return emptyList()
        return runCatching {
            json.parseToJsonElement(raw).jsonArray.mapNotNull(::decodePresetElement)
        }.getOrElse { emptyList() }
    }

    private fun decodePresetElement(element: JsonElement): PomodoroPresetConfig? {
        val obj = element.jsonObject
        val id = obj.string("id") ?: return null
        val name = obj.string("name") ?: "Preset"
        val cycles = obj.int("cycles") ?: 4
        val isBuiltIn = obj.boolean("isBuiltIn") ?: false
        val items = when {
            obj["items"] is JsonArray -> runCatching {
                json.decodeFromJsonElement(
                    ListSerializer(PomodoroPresetItem.serializer()),
                    obj.getValue("items")
                )
            }.getOrElse { emptyList() }
            obj["phases"] is JsonArray -> runCatching {
                json.decodeFromJsonElement<List<PomodoroPhaseConfig>>(obj.getValue("phases"))
                    .map { PomodoroPresetItem.Card(it) }
            }.getOrElse { emptyList() }
            else -> emptyList()
        }
        return PomodoroPresetConfig(id, name, cycles, items, isBuiltIn)
    }

    private fun legacyCustomToPreset(
        legacyName: String,
        legacySpec: String,
        legacyCycles: Int
    ): PomodoroPresetConfig? {
        val legacyPhases = legacySpecToPhases(legacySpec)
        if (legacyPhases.isEmpty()) return null
        return PomodoroPresetConfig(
            id = "USR_1",
            name = legacyName.ifBlank { "Meu Preset" },
            cycles = legacyCycles.coerceIn(1, 12),
            items = legacyPhases.map { PomodoroPresetItem.Card(it) },
            isBuiltIn = false
        )
    }

    fun legacySpecToPhases(spec: String): List<PomodoroPhaseConfig> {
        if (spec.isBlank()) return emptyList()
        return spec.split(";").mapNotNull { row ->
            val parts = row.split("|")
            if (parts.size < 4) return@mapNotNull null
            val label = parts[0].ifBlank { "Etapa" }
            val minutes = parts[1].toLongOrNull()?.coerceAtLeast(1L) ?: 1L
            val color = parts[2].toLongOrNull()?.toInt() ?: 0xFFEF4444.toInt()
            val sound = parts[3].ifBlank { "krono_alm_alarmbeep" }
            PomodoroPhaseConfig(
                id = "p${parts.hashCode().toUInt()}",
                label = label,
                totalSeconds = minutes * 60L,
                color = color,
                soundType = normalizePresetSound(sound)
            )
        }
    }

    private fun sanitizePreset(preset: PomodoroPresetConfig): PomodoroPresetConfig =
        preset.copy(
            name = preset.name.ifBlank { "Preset" }.take(50),
            cycles = preset.cycles.coerceIn(1, 12),
            items = preset.items.mapIndexed { index, item -> sanitizeItem(item, index + 1) }
        )

    private fun sanitizeItem(item: PomodoroPresetItem, index: Int): PomodoroPresetItem = when (item) {
        is PomodoroPresetItem.Card -> PomodoroPresetItem.Card(sanitizePhase(item.phase, index))
        is PomodoroPresetItem.Group -> item.copy(
            id = item.id.ifBlank { "g$index" },
            label = item.label.ifBlank { "Grupo $index" }.take(50),
            cycles = item.cycles.coerceIn(1, 12),
            phases = item.phases.mapIndexed { phaseIndex, phase -> sanitizePhase(phase, phaseIndex + 1) }
        )
    }

    private fun sanitizePhase(phase: PomodoroPhaseConfig, index: Int): PomodoroPhaseConfig =
        phase.copy(
            id = phase.id.ifBlank { "p$index" },
            label = phase.label.ifBlank { "Etapa" }.take(50),
            totalSeconds = phase.totalSeconds.coerceAtLeast(1L),
            soundType = normalizePresetSound(phase.soundType)
        )

    private fun normalizePresetSound(soundType: String): String = when {
        soundType.isBlank() -> "krono_alm_alarmbeep"
        else -> soundType
    }

    private fun JsonObject.string(key: String): String? = this[key]?.jsonPrimitive?.content
    private fun JsonObject.int(key: String): Int? = this[key]?.jsonPrimitive?.intOrNull
    private fun JsonObject.boolean(key: String): Boolean? = this[key]?.jsonPrimitive?.booleanOrNull

    private fun sortIndex(id: String): Int = when (id) {
        DEFAULT_ID -> 0
        LONG_ID -> 1
        SHORT_ID -> 2
        else -> 100
    }
}
