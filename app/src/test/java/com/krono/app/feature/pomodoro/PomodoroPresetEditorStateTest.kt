package com.krono.app.feature.pomodoro

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PomodoroPresetEditorStateTest {
    @Test
    fun `empty root disables save`() {
        val state = PomodoroPresetEditorState(
            PomodoroPresetConfig(id = "USR_1", name = "Vazio", cycles = 1, items = emptyList())
        )

        assertFalse(state.canSave)

        state.addGroup()

        assertFalse(state.canSave)
    }

    @Test
    fun `move item handles first to last`() {
        val state = PomodoroPresetEditorState(presetWithCards())

        state.moveItem(0, 2)

        assertEquals(listOf("p2", "p3", "p1"), state.items.map { it.id })
    }

    @Test
    fun `move root card into group`() {
        val state = PomodoroPresetEditorState(
            PomodoroPresetConfig(
                id = "USR_1",
                name = "Mover",
                cycles = 1,
                items = listOf(
                    validCard("p1"),
                    PomodoroPresetItem.Group("g1", "Grupo", 1, emptyList())
                )
            )
        )

        state.moveCardToGroup("p1", "g1", 0)

        assertEquals(listOf("g1"), state.items.map { it.id })
        assertEquals(listOf("p1"), (state.items.first() as PomodoroPresetItem.Group).phases.map { it.id })
    }

    @Test
    fun `move card out of group to root`() {
        val state = PomodoroPresetEditorState(
            PomodoroPresetConfig(
                id = "USR_1",
                name = "Mover",
                cycles = 1,
                items = listOf(
                    PomodoroPresetItem.Group(
                        id = "g1",
                        label = "Grupo",
                        cycles = 1,
                        phases = listOf(validCard("p1").phase)
                    )
                )
            )
        )

        state.moveCardOutOfGroup("g1", "p1", 1)

        assertEquals(listOf("g1", "p1"), state.items.map { it.id })
        assertTrue((state.items.first() as PomodoroPresetItem.Group).phases.isEmpty())
    }

    @Test
    fun `move phase inside group handles only item`() {
        val state = PomodoroPresetEditorState(
            PomodoroPresetConfig(
                id = "USR_1",
                name = "Grupo",
                cycles = 1,
                items = listOf(
                    PomodoroPresetItem.Group(
                        id = "g1",
                        label = "Grupo",
                        cycles = 1,
                        phases = listOf(validCard("p1").phase)
                    )
                )
            )
        )

        state.movePhaseInGroup("g1", 0, 1)

        assertEquals(listOf("p1"), (state.items.first() as PomodoroPresetItem.Group).phases.map { it.id })
    }

    @Test
    fun `save keeps root card only`() {
        val state = PomodoroPresetEditorState(
            PomodoroPresetConfig(
                id = "USR_1",
                name = "Card",
                cycles = 1,
                items = listOf(validCard("p1"))
            )
        )

        val saved = state.toPresetConfig(
            PomodoroPresetConfig(id = "USR_1", name = "Card", cycles = 1, items = emptyList())
        )

        assertTrue(state.canSave)
        assertEquals(1, saved.items.size)
        assertTrue(saved.items.first() is PomodoroPresetItem.Card)
    }

    @Test
    fun `new cards start invalid and unsavable until configured`() {
        val state = PomodoroPresetEditorState(
            PomodoroPresetConfig(id = "USR_1", name = "Novo", cycles = 1, items = emptyList())
        )

        state.addCard(PomodoroPresetCatalog.DEFAULT_NEW_CARD_COLOR)

        val card = state.items.first() as PomodoroPresetItem.Card
        assertEquals(PomodoroPresetCatalog.NEW_CARD_LABEL, card.phase.label)
        assertEquals(0L, card.phase.totalSeconds)
        assertFalse(state.canSave)
    }

    @Test
    fun `save drops empty group and blocks group only preset`() {
        val state = PomodoroPresetEditorState(
            PomodoroPresetConfig(
                id = "USR_1",
                name = "Grupo",
                cycles = 1,
                items = listOf(PomodoroPresetItem.Group("g1", "Grupo", 1, emptyList()))
            )
        )

        val saved = state.toPresetConfig(
            PomodoroPresetConfig(id = "USR_1", name = "Grupo", cycles = 1, items = emptyList())
        )

        assertFalse(state.canSave)
        assertTrue(saved.items.isEmpty())
    }

    @Test
    fun `save drops empty group and keeps external card`() {
        val state = PomodoroPresetEditorState(
            PomodoroPresetConfig(
                id = "USR_1",
                name = "Misturado",
                cycles = 1,
                items = listOf(
                    PomodoroPresetItem.Group("g1", "Grupo", 1, emptyList()),
                    validCard("p1")
                )
            )
        )

        val saved = state.toPresetConfig(
            PomodoroPresetConfig(id = "USR_1", name = "Misturado", cycles = 1, items = emptyList())
        )

        assertTrue(state.canSave)
        assertEquals(listOf("p1"), saved.items.map { it.id })
    }

    @Test
    fun `save keeps group with internal card`() {
        val state = PomodoroPresetEditorState(
            PomodoroPresetConfig(
                id = "USR_1",
                name = "Grupo",
                cycles = 1,
                items = listOf(
                    PomodoroPresetItem.Group(
                        id = "g1",
                        label = "Grupo",
                        cycles = 1,
                        phases = listOf(validCard("p1").phase)
                    )
                )
            )
        )

        val saved = state.toPresetConfig(
            PomodoroPresetConfig(id = "USR_1", name = "Grupo", cycles = 1, items = emptyList())
        )

        assertTrue(state.canSave)
        assertEquals(listOf("g1"), saved.items.map { it.id })
    }

    private fun presetWithCards(): PomodoroPresetConfig =
        PomodoroPresetConfig(
            id = "USR_1",
            name = "Cards",
            cycles = 1,
            items = listOf(
                validCard("p1", label = "Foco 1"),
                validCard("p2", label = "Pausa 1"),
                validCard("p3", label = "Foco 2")
            )
        )

    private fun validCard(
        id: String,
        label: String = "Card",
        totalSeconds: Long = 60L,
        color: Int = 0xFF6B7FD4.toInt()
    ): PomodoroPresetItem.Card = PomodoroPresetItem.Card(
        PomodoroPhaseConfig(
            id = id,
            label = label,
            totalSeconds = totalSeconds,
            color = color,
            soundType = "krono_alm_alarmbeep"
        )
    )
}
