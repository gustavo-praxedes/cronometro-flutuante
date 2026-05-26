package com.krono.app.feature.pomodoro

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PomodoroPresetCatalogTest {
    @Test
    fun `decode migrates legacy phases to items`() {
        val raw = """
            [
              {
                "id": "USR_1",
                "name": "Legado",
                "cycles": 3,
                "phases": [
                  {"id":"p1","label":"Foco","totalSeconds":1500,"color":-10972,"soundType":"krono_alm_alarmbeep"},
                  {"id":"p2","label":"Pausa","totalSeconds":300,"color":-14513333,"soundType":"krono_alm_beeps"}
                ],
                "isBuiltIn": false
              }
            ]
        """.trimIndent()

        val decoded = PomodoroPresetCatalog.decode(raw, "", "", 1)
        val migrated = decoded.first { it.id == "USR_1" }

        assertEquals(3, migrated.cycles)
        assertEquals(2, migrated.items.size)
        assertTrue(migrated.items.all { it is PomodoroPresetItem.Card })
    }

    @Test
    fun `encode writes item discriminator`() {
        val encoded = PomodoroPresetCatalog.encode(
            listOf(
                PomodoroPresetConfig(
                    id = "USR_1",
                    name = "Novo",
                    cycles = 1,
                    items = listOf(PomodoroPresetCatalog.defaultCard(1)),
                    isBuiltIn = false
                )
            )
        )

        assertTrue(encoded.contains("\"type\":\"card\""))
    }

    @Test
    fun `execution phases expands group cycles`() {
        val preset = PomodoroPresetConfig(
            id = "USR_1",
            name = "Exec",
            cycles = 2,
            items = listOf(
                PomodoroPresetCatalog.defaultCard(1),
                PomodoroPresetItem.Group(
                    id = "g1",
                    label = "Grupo",
                    cycles = 2,
                    phases = listOf(
                        PomodoroPhaseConfig("p2", "Pausa", 300, 0xFF22C55E.toInt(), "krono_alm_beeps")
                    )
                )
            )
        )

        assertEquals(listOf("p1", "p2", "p2"), preset.executionPhases().map { it.id })
    }
}
