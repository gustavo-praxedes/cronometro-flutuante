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
    const val NEW_CARD_LABEL = "Novo card"
    const val DEFAULT_NEW_CARD_COLOR = 0xFF6B7FD4.toInt()

    private const val LEGACY_DEFAULT_ID = "CLASSICO"
    private const val LEGACY_LONG_ID = "LONGO"
    private const val LEGACY_SHORT_ID = "CURTO"
    private const val REMOVED_LONG_ID = "LONG"
    private const val REMOVED_SHORT_ID = "SHORT"
    private const val DEFAULT_CYCLES = 4
    private const val DEFAULT_GROUP_LABEL = "Ciclo"
    private const val FOCUS_LABEL = "Foco"
    private const val SHORT_BREAK_LABEL = "Pausa curta"
    private const val LONG_BREAK_LABEL = "Pausa longa"

    private val json = Json {
        ignoreUnknownKeys = true
        classDiscriminator = "type"
    }

    fun decode(raw: String): List<PomodoroPresetConfig> {
        val decoded = decodeCurrentPresets(raw).filterNot(::isCatalogPreset)
        if (decoded.isEmpty() && raw.isBlank()) return defaults()
        return (defaults() + decoded)
            .map(::sanitizePreset)
            .sortedBy { sortIndex(it.id) }
    }

    fun migrateLegacyPresets(
        raw: String,
        legacyCustomName: String,
        legacyCustomSpec: String,
        legacyCustomCycles: Int
    ): List<PomodoroPresetConfig> {
        val decoded = decodeLegacyAwarePresets(raw).filterNot(::isCatalogPreset)
        val migratedCustom = if (decoded.isEmpty()) {
            legacyCustomToPreset(legacyCustomName, legacyCustomSpec, legacyCustomCycles)?.let(::listOf).orEmpty()
        } else {
            decoded
        }
        return (defaults() + migratedCustom)
            .map(::sanitizePreset)
            .sortedBy { sortIndex(it.id) }
    }

    fun encode(presets: List<PomodoroPresetConfig>): String =
        json.encodeToString(presets.map(::sanitizePreset))

    fun defaults(): List<PomodoroPresetConfig> = listOf(defaultPreset())

    fun newUserPresetTemplate(index: Int): PomodoroPresetConfig {
        val safeIndex = index.coerceAtLeast(1)
        return PomodoroPresetConfig(
            id = "USR_$safeIndex",
            name = "Novo preset",
            cycles = DEFAULT_CYCLES,
            items = emptyList(),
            isBuiltIn = false
        )
    }

    fun defaultCard(
        index: Int,
        defaultColor: Int = DEFAULT_NEW_CARD_COLOR
    ): PomodoroPresetItem.Card {
        val safeIndex = index.coerceAtLeast(1)
        return PomodoroPresetItem.Card(
            PomodoroPhaseConfig(
                id = "p$safeIndex",
                label = NEW_CARD_LABEL,
                totalSeconds = 0L,
                color = defaultColor,
                soundType = "krono_alm_alarmbeep"
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

    fun normalizePresetId(id: String): String = when (id) {
        LEGACY_DEFAULT_ID -> DEFAULT_ID
        else -> id
    }

    fun normalizeSelectedPresetId(id: String): String = when (id) {
        DEFAULT_ID,
        LEGACY_DEFAULT_ID,
        LEGACY_LONG_ID,
        LEGACY_SHORT_ID,
        REMOVED_LONG_ID,
        REMOVED_SHORT_ID -> DEFAULT_ID
        else -> id
    }

    fun isLegacyPresetStorage(raw: String): Boolean = raw.contains("\"phases\"")

    fun requiresCatalogMigration(raw: String): Boolean {
        if (raw.isBlank()) return true
        val decoded = decodeCurrentPresets(raw)
        if (decoded.isEmpty()) return true
        if (decoded.any(::isRemovedBuiltInPreset)) return true
        val storedDefault = decoded.firstOrNull { it.id == DEFAULT_ID } ?: return true
        return !matchesCurrentDefaultShape(storedDefault)
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

    private fun defaultPreset(): PomodoroPresetConfig = PomodoroPresetConfig(
        id = DEFAULT_ID,
        name = "Pomodoro Padrão",
        cycles = DEFAULT_CYCLES,
        items = listOf(
            PomodoroPresetItem.Group(
                id = "g1",
                label = DEFAULT_GROUP_LABEL,
                cycles = 1,
                phases = listOf(
                    phase("p1", FOCUS_LABEL, 25 * 60L, 0xFFEF4444.toInt(), "krono_alm_alarmbeep"),
                    phase("p2", SHORT_BREAK_LABEL, 5 * 60L, 0xFF22C55E.toInt(), "krono_alm_beeps"),
                    phase("p3", FOCUS_LABEL, 25 * 60L, 0xFFEF4444.toInt(), "krono_alm_alarmbeep"),
                    phase("p4", SHORT_BREAK_LABEL, 5 * 60L, 0xFF22C55E.toInt(), "krono_alm_beeps"),
                    phase("p5", FOCUS_LABEL, 25 * 60L, 0xFFEF4444.toInt(), "krono_alm_alarmbeep"),
                    phase("p6", SHORT_BREAK_LABEL, 5 * 60L, 0xFF22C55E.toInt(), "krono_alm_beeps"),
                    phase("p7", FOCUS_LABEL, 25 * 60L, 0xFFEF4444.toInt(), "krono_alm_alarmbeep")
                )
            ),
            PomodoroPresetItem.Card(
                phase("p8", LONG_BREAK_LABEL, 25 * 60L, 0xFF22C55E.toInt(), "krono_alm_beeps")
            )
        ),
        isBuiltIn = true
    )

    private fun phase(
        id: String,
        label: String,
        totalSeconds: Long,
        color: Int,
        soundType: String
    ) = PomodoroPhaseConfig(
        id = id,
        label = label,
        totalSeconds = totalSeconds,
        color = color,
        soundType = soundType
    )

    private fun decodeCurrentPresets(raw: String): List<PomodoroPresetConfig> {
        if (raw.isBlank()) return emptyList()
        return runCatching {
            json.parseToJsonElement(raw).jsonArray.mapNotNull(::decodePresetElement)
        }.getOrElse { emptyList() }
    }

    private fun decodeLegacyAwarePresets(raw: String): List<PomodoroPresetConfig> {
        if (raw.isBlank()) return emptyList()
        return runCatching {
            json.parseToJsonElement(raw).jsonArray.mapNotNull(::decodeLegacyPresetElement)
        }.getOrElse { emptyList() }
    }

    private fun decodePresetElement(element: JsonElement): PomodoroPresetConfig? {
        val obj = element.jsonObject
        val id = normalizePresetId(obj.string("id") ?: return null)
        if (id.isRemovedBuiltInId()) return null
        val name = obj.string("name") ?: "Preset"
        val cycles = obj.int("cycles") ?: DEFAULT_CYCLES
        val isBuiltIn = obj.boolean("isBuiltIn") ?: false
        val items = when {
            obj["items"] is JsonArray -> runCatching {
                json.decodeFromJsonElement(
                    ListSerializer(PomodoroPresetItem.serializer()),
                    obj.getValue("items")
                )
            }.getOrElse { emptyList() }
            else -> emptyList()
        }
        if (items.isEmpty()) return null
        return PomodoroPresetConfig(id, name, cycles, items, isBuiltIn)
    }

    private fun decodeLegacyPresetElement(element: JsonElement): PomodoroPresetConfig? {
        val obj = element.jsonObject
        val id = normalizePresetId(obj.string("id") ?: return null)
        if (id.isRemovedBuiltInId()) return null
        val name = obj.string("name") ?: "Preset"
        val cycles = obj.int("cycles") ?: DEFAULT_CYCLES
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
        if (items.isEmpty()) return null
        return PomodoroPresetConfig(id, name, cycles, items, isBuiltIn)
    }

    private fun legacyCustomToPreset(
        legacyName: String,
        legacySpec: String,
        legacyCustomCycles: Int
    ): PomodoroPresetConfig? {
        val legacyPhases = legacySpecToPhases(legacySpec)
        if (legacyPhases.isEmpty()) return null
        return PomodoroPresetConfig(
            id = "USR_1",
            name = legacyName.ifBlank { "Meu Preset" },
            cycles = legacyCustomCycles.coerceIn(1, 12),
            items = legacyPhases.map { PomodoroPresetItem.Card(it) },
            isBuiltIn = false
        )
    }

    private fun sanitizePreset(preset: PomodoroPresetConfig): PomodoroPresetConfig =
        preset.copy(
            id = normalizePresetId(preset.id),
            name = preset.name.ifBlank { "Preset" }.take(50),
            cycles = preset.cycles.coerceIn(1, 12),
            items = preset.items.mapIndexedNotNull { index, item -> sanitizeItem(item, index + 1) },
            isBuiltIn = preset.id == DEFAULT_ID && preset.isBuiltIn
        )

    private fun sanitizeItem(item: PomodoroPresetItem, index: Int): PomodoroPresetItem? = when (item) {
        is PomodoroPresetItem.Card -> sanitizePhase(item.phase, index)?.let(PomodoroPresetItem::Card)
        is PomodoroPresetItem.Group -> item.copy(
            id = item.id.ifBlank { "g$index" },
            label = item.label.ifBlank { "Grupo $index" }.take(50),
            cycles = item.cycles.coerceIn(1, 12),
            phases = item.phases.mapIndexedNotNull { phaseIndex, phase -> sanitizePhase(phase, phaseIndex + 1) }
        ).takeIf { it.phases.isNotEmpty() }
    }

    private fun sanitizePhase(phase: PomodoroPhaseConfig, index: Int): PomodoroPhaseConfig? =
        phase.takeIf { it.totalSeconds > 0L }?.copy(
            id = phase.id.ifBlank { "p$index" },
            label = phase.label.ifBlank { NEW_CARD_LABEL }.take(50),
            soundType = normalizePresetSound(phase.soundType)
        )

    private fun normalizePresetSound(soundType: String): String = when {
        soundType.isBlank() -> "krono_alm_alarmbeep"
        else -> soundType
    }

    private fun isCatalogPreset(preset: PomodoroPresetConfig): Boolean =
        preset.id == DEFAULT_ID || preset.id.isRemovedBuiltInId()

    private fun isRemovedBuiltInPreset(preset: PomodoroPresetConfig): Boolean =
        preset.id.isRemovedBuiltInId()

    private fun String.isRemovedBuiltInId(): Boolean =
        this == REMOVED_LONG_ID || this == REMOVED_SHORT_ID

    private fun matchesCurrentDefaultShape(preset: PomodoroPresetConfig): Boolean {
        if (preset.id != DEFAULT_ID || preset.cycles != DEFAULT_CYCLES || preset.items.size != 2) return false
        val group = preset.items.firstOrNull() as? PomodoroPresetItem.Group ?: return false
        val longBreak = preset.items.getOrNull(1) as? PomodoroPresetItem.Card ?: return false
        if (group.cycles != 1 || group.phases.size != 7) return false
        val expectedLabels = listOf(
            FOCUS_LABEL,
            SHORT_BREAK_LABEL,
            FOCUS_LABEL,
            SHORT_BREAK_LABEL,
            FOCUS_LABEL,
            SHORT_BREAK_LABEL,
            FOCUS_LABEL
        )
        val expectedDurations = listOf(1500L, 300L, 1500L, 300L, 1500L, 300L, 1500L)
        if (group.phases.map { it.label } != expectedLabels) return false
        if (group.phases.map { it.totalSeconds } != expectedDurations) return false
        return longBreak.phase.label == LONG_BREAK_LABEL && longBreak.phase.totalSeconds == 1500L
    }

    private fun JsonObject.string(key: String): String? = this[key]?.jsonPrimitive?.content
    private fun JsonObject.int(key: String): Int? = this[key]?.jsonPrimitive?.intOrNull
    private fun JsonObject.boolean(key: String): Boolean? = this[key]?.jsonPrimitive?.booleanOrNull

    private fun sortIndex(id: String): Int = when (id) {
        DEFAULT_ID -> 0
        else -> 100
    }
}
