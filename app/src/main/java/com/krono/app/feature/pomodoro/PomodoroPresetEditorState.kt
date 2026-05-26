package com.krono.app.feature.pomodoro

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class PomodoroPresetEditorState(initialPreset: PomodoroPresetConfig) {
    var name by mutableStateOf(initialPreset.name)
    var cycles by mutableIntStateOf(initialPreset.cycles.coerceIn(1, 12))
    val items = mutableStateListOf<PomodoroPresetItem>().apply { addAll(initialPreset.items) }

    val canSave: Boolean
        get() = items.isNotEmpty()

    fun addCard() {
        items.add(PomodoroPresetCatalog.defaultCard(nextPhaseIndex()).withPhaseId(nextPhaseId()))
    }

    fun addGroup() {
        items.add(PomodoroPresetCatalog.defaultGroup(nextGroupIndex()).copy(id = nextGroupId()))
    }

    fun removeItem(id: String) {
        items.removeAll { it.id == id }
    }

    fun moveItem(fromIndex: Int, toIndex: Int) {
        if (fromIndex !in items.indices || items.isEmpty()) return
        val item = items.removeAt(fromIndex)
        items.add(toIndex.coerceIn(0, items.size), item)
    }

    fun addPhaseToGroup(groupId: String) {
        updateGroupById(groupId) { group ->
            group.copy(phases = group.phases + PomodoroPresetCatalog.defaultCard(nextPhaseIndex()).phase.copy(id = nextPhaseId()))
        }
    }

    fun removePhaseFromGroup(groupId: String, phaseId: String) {
        updateGroupById(groupId) { group ->
            group.copy(phases = group.phases.filterNot { it.id == phaseId })
        }
    }

    fun movePhaseInGroup(groupId: String, from: Int, to: Int) {
        updateGroupById(groupId) { group ->
            if (from !in group.phases.indices || group.phases.isEmpty()) return@updateGroupById group
            val moved = group.phases.toMutableList()
            val phase = moved.removeAt(from)
            moved.add(to.coerceIn(0, moved.size), phase)
            group.copy(phases = moved)
        }
    }

    fun moveCardToGroup(cardId: String, groupId: String, atIndex: Int) {
        val rootIndex = items.indexOfFirst { it is PomodoroPresetItem.Card && it.id == cardId }
        val card = items.getOrNull(rootIndex) as? PomodoroPresetItem.Card ?: return
        items.removeAt(rootIndex)
        updateGroupById(groupId) { group ->
            val updated = group.phases.toMutableList()
            updated.add(atIndex.coerceIn(0, updated.size), card.phase)
            group.copy(phases = updated)
        }
    }

    fun moveCardOutOfGroup(groupId: String, phaseId: String, atRootIndex: Int) {
        var movedPhase: PomodoroPhaseConfig? = null
        updateGroupById(groupId) { group ->
            movedPhase = group.phases.firstOrNull { it.id == phaseId }
            group.copy(phases = group.phases.filterNot { it.id == phaseId })
        }
        movedPhase?.let { phase ->
            items.add(atRootIndex.coerceIn(0, items.size), PomodoroPresetItem.Card(phase))
        }
    }

    fun updateGroup(updated: PomodoroPresetItem.Group) {
        val index = items.indexOfFirst { it is PomodoroPresetItem.Group && it.id == updated.id }
        if (index >= 0) items[index] = updated.copy(cycles = updated.cycles.coerceIn(1, 12))
    }

    fun updatePhase(groupId: String?, updated: PomodoroPhaseConfig) {
        if (groupId == null) {
            val index = items.indexOfFirst { it is PomodoroPresetItem.Card && it.phase.id == updated.id }
            if (index >= 0) items[index] = PomodoroPresetItem.Card(updated)
            return
        }
        updateGroupById(groupId) { group ->
            group.copy(phases = group.phases.map { phase -> if (phase.id == updated.id) updated else phase })
        }
    }

    fun toPresetConfig(original: PomodoroPresetConfig): PomodoroPresetConfig =
        original.copy(
            name = name.trim().ifBlank { original.name }.take(50),
            cycles = cycles.coerceIn(1, 12),
            items = items.toList()
        )

    private fun updateGroupById(groupId: String, transform: (PomodoroPresetItem.Group) -> PomodoroPresetItem.Group) {
        val index = items.indexOfFirst { it is PomodoroPresetItem.Group && it.id == groupId }
        val group = items.getOrNull(index) as? PomodoroPresetItem.Group ?: return
        items[index] = transform(group)
    }

    private fun nextPhaseIndex(): Int = allPhaseIds().size + 1

    private fun nextPhaseId(): String {
        val existing = allPhaseIds()
        var index = existing.size + 1
        while ("p$index" in existing) index++
        return "p$index"
    }

    private fun nextGroupIndex(): Int = items.count { it is PomodoroPresetItem.Group } + 1

    private fun nextGroupId(): String {
        val existing = items.filterIsInstance<PomodoroPresetItem.Group>().map { it.id }.toSet()
        var index = existing.size + 1
        while ("g$index" in existing) index++
        return "g$index"
    }

    private fun allPhaseIds(): Set<String> =
        items.flatMap { item ->
            when (item) {
                is PomodoroPresetItem.Card -> listOf(item.phase.id)
                is PomodoroPresetItem.Group -> item.phases.map { it.id }
            }
        }.toSet()
}

private fun PomodoroPresetItem.Card.withPhaseId(id: String): PomodoroPresetItem.Card =
    copy(phase = phase.copy(id = id))
