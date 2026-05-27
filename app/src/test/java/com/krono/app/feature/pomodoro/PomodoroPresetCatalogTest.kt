package com.krono.app.feature.pomodoro

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PomodoroPresetCatalogTest {
    @Test
    fun `defaults keep only standard preset`() {
        val defaults = PomodoroPresetCatalog.defaults()

        assertEquals(1, defaults.size)
        assertEquals(PomodoroPresetCatalog.DEFAULT_ID, defaults.single().id)
        assertTrue(defaults.single().isBuiltIn)
    }

    @Test
    fun `new user preset starts without cards`() {
        val preset = PomodoroPresetCatalog.newUserPresetTemplate(1)

        assertFalse(preset.isBuiltIn)
        assertTrue(preset.items.isEmpty())
    }

    @Test
    fun `migrate legacy phases to items`() {
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

        val decoded = PomodoroPresetCatalog.migrateLegacyPresets(raw, "", "", 1)
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
                    items = listOf(
                        PomodoroPresetItem.Card(
                            PomodoroPhaseConfig("p1", "Card", 60L, 0xFF6B7FD4.toInt(), "krono_alm_alarmbeep")
                        )
                    ),
                    isBuiltIn = false
                )
            )
        )

        assertTrue(encoded.contains("\"type\":\"card\""))
    }

    @Test
    fun `decode ignores legacy payload after migration boundary`() {
        val raw = """
            [
              {
                "id": "USR_1",
                "name": "Legado",
                "cycles": 3,
                "phases": [
                  {"id":"p1","label":"Foco","totalSeconds":1500,"color":-10972,"soundType":"krono_alm_alarmbeep"}
                ],
                "isBuiltIn": false
              }
            ]
        """.trimIndent()

        val decoded = PomodoroPresetCatalog.decode(raw)

        assertTrue(decoded.none { it.id == "USR_1" })
        assertTrue(decoded.any { it.id == PomodoroPresetCatalog.DEFAULT_ID })
    }

    @Test
    fun `decode removes deprecated long and short presets`() {
        val raw = """
            [
              {
                "id": "LONG",
                "name": "Pomodoro Longo",
                "cycles": 4,
                "items": [
                  {
                    "type":"card",
                    "phase":{"id":"p1","label":"Foco","totalSeconds":3000,"color":-10972,"soundType":"krono_alm_alarmbeep"}
                  }
                ],
                "isBuiltIn": true
              },
              {
                "id": "USR_1",
                "name": "Meu",
                "cycles": 1,
                "items": [
                  {
                    "type":"card",
                    "phase":{"id":"p2","label":"Card","totalSeconds":60,"color":-10972,"soundType":"krono_alm_alarmbeep"}
                  }
                ],
                "isBuiltIn": false
              }
            ]
        """.trimIndent()

        val decoded = PomodoroPresetCatalog.decode(raw)

        assertTrue(decoded.none { it.id == "LONG" })
        assertTrue(decoded.any { it.id == PomodoroPresetCatalog.DEFAULT_ID })
        assertTrue(decoded.any { it.id == "USR_1" })
    }

    @Test
    fun `execution phases expands group cycles`() {
        val preset = PomodoroPresetConfig(
            id = "USR_1",
            name = "Exec",
                cycles = 2,
                items = listOf(
                PomodoroPresetItem.Card(
                    PomodoroPhaseConfig("p1", "Card", 60L, 0xFF6B7FD4.toInt(), "krono_alm_alarmbeep")
                ),
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
