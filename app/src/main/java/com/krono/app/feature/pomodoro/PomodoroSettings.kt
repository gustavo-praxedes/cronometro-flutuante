package com.krono.app.feature.pomodoro

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.krono.app.R
import com.krono.app.core.audio.KronoSoundCatalog
import com.krono.app.core.data.OverlayConfig
import com.krono.app.core.data.OverlayDataStore
import com.krono.app.core.ui.components.AppearanceSlider
import com.krono.app.core.ui.settings.SettingsGroup
import com.krono.app.core.ui.settings.SettingsPanelLayout
import com.krono.app.core.ui.theme.KronoTokens
import com.krono.app.core.util.SOUND_NONE
import com.krono.app.core.util.loadNotificationSoundOptions
import kotlinx.coroutines.launch

@Composable
fun PomodoroSettings(dataStore: OverlayDataStore, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val config = dataStore.configFlow.collectAsState(initial = OverlayConfig()).value
    val scope = rememberCoroutineScope()
    val volumeMinLabel = stringResource(R.string.settings_volume_min)
    val volumeMaxLabel = stringResource(R.string.settings_volume_max)
    val notificationSoundOptions = remember(context) {
        loadNotificationSoundOptions(context).map { option ->
            val label = when (option.uriString) {
                SOUND_NONE -> context.getString(R.string.sound_none)
                else -> KronoSoundCatalog.pomodoroAlertLabelResId(option.uriString)
                    ?.let { context.getString(it) }
                    ?: option.uriString
            }
            option.copy(label = label)
        }
    }
    val presets = remember(
        config.pomodoroPresetsSpec,
        config.pomodoroCustomPresetName,
        config.pomodoroCustomPhasesSpec,
        config.pomodoroCustomCycles
    ) {
        PomodoroPresetCatalog.decode(
            raw = config.pomodoroPresetsSpec,
            legacyCustomName = config.pomodoroCustomPresetName,
            legacyCustomSpec = config.pomodoroCustomPhasesSpec,
            legacyCustomCycles = config.pomodoroCustomCycles
        )
    }
    val safeSelectedPresetId = presets.firstOrNull { it.id == config.pomodoroPreset }?.id
        ?: presets.firstOrNull()?.id
        ?: PomodoroPresetCatalog.DEFAULT_ID
    val selectedPreset = presets.firstOrNull { it.id == safeSelectedPresetId }

    LaunchedEffect(safeSelectedPresetId) {
        if (safeSelectedPresetId != config.pomodoroPreset) {
            dataStore.updateConfig(config.copy(pomodoroPreset = safeSelectedPresetId))
        }
    }
    LaunchedEffect(config.pomodoroPresetsSpec, config.pomodoroCustomPhasesSpec, config.pomodoroCustomCycles) {
        if (config.pomodoroPresetsSpec.isBlank()) {
            val focusSound = if (config.pomodoroFocusAlertEnabled) config.pomodoroFocusAlertSoundType else "NONE"
            val breakSound = if (config.pomodoroBreakAlertEnabled) config.pomodoroBreakAlertSoundType else "NONE"
            val seeded = PomodoroPresetCatalog.decode(
                raw = "",
                legacyCustomName = config.pomodoroCustomPresetName,
                legacyCustomSpec = config.pomodoroCustomPhasesSpec,
                legacyCustomCycles = config.pomodoroCustomCycles
            ).map { preset ->
                preset.copy(
                    items = preset.items.map { item ->
                        when (item) {
                            is PomodoroPresetItem.Card -> {
                                val index = preset.executionPhases().indexOfFirst { it.id == item.phase.id }
                                item.copy(phase = item.phase.copy(soundType = if (index % 2 == 0) focusSound else breakSound))
                            }
                            is PomodoroPresetItem.Group -> item.copy(
                                phases = item.phases.mapIndexed { index, phase ->
                                    phase.copy(soundType = if (index % 2 == 0) focusSound else breakSound)
                                }
                            )
                        }
                    }
                )
            }
            dataStore.updateConfig(
                config.copy(
                    pomodoroPresetsSpec = PomodoroPresetCatalog.encode(seeded),
                    pomodoroPreset = seeded.firstOrNull()?.id ?: PomodoroPresetCatalog.DEFAULT_ID
                )
            )
        }
    }

    var editingPreset by remember { mutableStateOf<PomodoroPresetConfig?>(null) }

    SettingsPanelLayout(modifier = modifier) {
        PomodoroPresetSettingsSection(
            presets = presets,
            selectedPreset = selectedPreset,
            selectedPresetId = safeSelectedPresetId,
            onPresetSelected = { value ->
                scope.launch { dataStore.updateConfig(config.copy(pomodoroPreset = value)) }
            },
            onCreatePreset = {
                editingPreset = PomodoroPresetCatalog.newUserPresetTemplate(nextUserPresetIndex(presets))
            },
            onEditPreset = { preset ->
                editingPreset = preset
            },
            onDeletePreset = { preset ->
                if (!preset.isBuiltIn) {
                    val updated = deletePreset(presets = presets, presetId = preset.id)
                    val nextPresetId = if (preset.id == safeSelectedPresetId) {
                        selectedPresetIdAfterDelete(
                            presets = presets,
                            updatedPresets = updated,
                            deletedPresetId = preset.id
                        )
                    } else {
                        safeSelectedPresetId.takeIf { id -> updated.any { it.id == id } }
                            ?: PomodoroPresetCatalog.DEFAULT_ID
                    }
                    scope.launch {
                        dataStore.updateConfig(
                            config.copy(
                                pomodoroPreset = nextPresetId,
                                pomodoroPresetsSpec = PomodoroPresetCatalog.encode(updated)
                            )
                        )
                    }
                }
            }
        )

        SettingsGroup(title = stringResource(R.string.settings_group_sounds)) {
            AppearanceSlider(
                label = stringResource(R.string.settings_volume_pomodoro),
                value = config.focusAlertVolume,
                minLabel = volumeMinLabel,
                maxLabel = volumeMaxLabel,
                range = 0f..1f,
                display = "${(config.focusAlertVolume * 100).toInt()}%",
                onChange = { value ->
                    scope.launch {
                        dataStore.updateConfig(
                            config.copy(
                                focusAlertVolume = value,
                                breakAlertVolume = value
                            )
                        )
                    }
                },
                modifier = Modifier.padding(
                    horizontal = KronoTokens.Settings.panelHorizontalInset,
                    vertical = KronoTokens.Spacing.sm
                )
            )
        }

        PomodoroBehaviorSettingsSection(
            config = config,
            onAutoNextCycleChange = { enabled ->
                scope.launch {
                    dataStore.updateConfig(
                        config.copy(
                            pomodoroAutoNextCycle = enabled,
                            pomodoroAutoStartBreak = enabled,
                            pomodoroAutoStartFocus = enabled
                        )
                    )
                }
            },
            onDndChange = { enabled ->
                scope.launch { dataStore.updateConfig(config.copy(pomodoroDndDuringFocus = enabled)) }
            },
            onDailyGoalChange = { value ->
                scope.launch { dataStore.updateConfig(config.copy(pomodoroDailyGoalCycles = value)) }
            }
        )

        PomodoroHistorySection(
            rawHistory = config.pomodoroSessionHistory,
            presets = presets
        )
    }

    val currentEditing = editingPreset
    if (currentEditing != null) {
        PomodoroPresetEditorDialog(
            initialPreset = currentEditing,
            selectedFont = config.overlayFontFamily,
            pomodoroVolume = config.focusAlertVolume,
            notificationSoundOptions = notificationSoundOptions,
            config = config,
            onDismiss = { editingPreset = null },
            onSave = { saved ->
                val updated = upsertPreset(presets, saved)
                scope.launch {
                    dataStore.updateConfig(
                        config.copy(
                            pomodoroPreset = saved.id,
                            pomodoroPresetsSpec = PomodoroPresetCatalog.encode(updated),
                            pomodoroCustomPresetName = saved.name,
                            pomodoroCustomCycles = saved.cycles,
                            pomodoroCustomPhasesSpec = saved.legacyPhasesSpec()
                        )
                    )
                }
                editingPreset = null
            }
        )
    }
}
