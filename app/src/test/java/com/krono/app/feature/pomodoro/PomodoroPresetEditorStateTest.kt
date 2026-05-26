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

        assertTrue(state.canSave)
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
                    PomodoroPresetCatalog.defaultCard(1),
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
                        phases = listOf(PomodoroPresetCatalog.defaultCard(1).phase)
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
                        phases = listOf(PomodoroPresetCatalog.defaultCard(1).phase)
                    )
                )
            )
        )

        state.movePhaseInGroup("g1", 0, 1)

        assertEquals(listOf("p1"), (state.items.first() as PomodoroPresetItem.Group).phases.map { it.id })
    }

    private fun presetWithCards(): PomodoroPresetConfig =
        PomodoroPresetConfig(
            id = "USR_1",
            name = "Cards",
            cycles = 1,
            items = listOf(
                PomodoroPresetCatalog.defaultCard(1),
                PomodoroPresetCatalog.defaultCard(2, isFocus = false),
                PomodoroPresetCatalog.defaultCard(3)
            )
        )
}
