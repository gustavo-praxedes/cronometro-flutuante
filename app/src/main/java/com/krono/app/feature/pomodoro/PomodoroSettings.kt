package com.krono.app.feature.pomodoro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.krono.app.R
import com.krono.app.core.data.OverlayConfig
import com.krono.app.core.data.OverlayDataStore
import com.krono.app.core.ui.components.AppearanceSlider
import com.krono.app.core.ui.components.KronoDropdown
import com.krono.app.core.ui.components.SettingsDivider
import com.krono.app.core.ui.components.SettingsRow
import com.krono.app.core.ui.components.ToggleRow
import com.krono.app.core.ui.dialogs.ColorPickerDialog
import com.krono.app.core.ui.settings.OverlayToolSettingsSection
import com.krono.app.core.ui.settings.SettingsGroup
import com.krono.app.core.ui.settings.SettingsPanelLayout
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoTokens
import com.krono.app.core.ui.theme.timerFontFamily
import com.krono.app.core.util.playPomodoroPhaseBeep
import com.krono.app.core.util.playPomodoroTick
import com.krono.app.feature.countdown.CountdownScreenWheelPicker
import kotlinx.coroutines.launch

private val PHASE_SOUND_OPTIONS = listOf(
    "FOCUS_A",
    "FOCUS_B",
    "FOCUS_C",
    "FOCUS_D",
    "BREAK_A",
    "BREAK_B",
    "BREAK_C",
    "BREAK_D"
)

@Composable
fun PomodoroSettings(dataStore: OverlayDataStore, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val config = dataStore.configFlow.collectAsState(initial = OverlayConfig()).value
    val scope = rememberCoroutineScope()
    val volumeMinLabel = stringResource(R.string.settings_volume_min)
    val volumeMaxLabel = stringResource(R.string.settings_volume_max)
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
            val seeded = PomodoroPresetCatalog.decode(
                raw = "",
                legacyCustomName = config.pomodoroCustomPresetName,
                legacyCustomSpec = config.pomodoroCustomPhasesSpec,
                legacyCustomCycles = config.pomodoroCustomCycles
            )
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
        SettingsGroup(title = stringResource(R.string.settings_subgroup_presets)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = KronoTokens.Settings.panelHorizontalInset,
                        vertical = KronoTokens.Spacing.sm
                    ),
                horizontalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.sm)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    KronoDropdown(
                        value = safeSelectedPresetId,
                        onValueChange = { value ->
                            scope.launch { dataStore.updateConfig(config.copy(pomodoroPreset = value)) }
                        },
                        options = presets.map { it.id },
                        label = stringResource(R.string.pomodoro_preset_label),
                        leadingIcon = KronoIcons.Action.ListAlt,
                        textMapping = { key -> presets.firstOrNull { it.id == key }?.name ?: key }
                    )
                }
                FilledTonalIconButton(
                    onClick = {
                        editingPreset = PomodoroPresetCatalog.newUserPresetTemplate(nextUserPresetIndex(presets))
                    },
                    modifier = Modifier.size(KronoTokens.Size.iconBox)
                ) {
                    Icon(
                        imageVector = KronoIcons.Action.ListAltAdd,
                        contentDescription = stringResource(R.string.pomodoro_create_preset_label)
                    )
                }
                FilledTonalIconButton(
                    onClick = { editingPreset = selectedPreset },
                    enabled = selectedPreset != null,
                    modifier = Modifier.size(KronoTokens.Size.iconBox)
                ) {
                    Icon(
                        imageVector = KronoIcons.Action.Settings,
                        contentDescription = stringResource(R.string.action_edit)
                    )
                }
            }
        }

        SettingsGroup(title = stringResource(R.string.settings_group_sounds_vibration)) {
            ToggleRow(
                label = stringResource(R.string.pomodoro_tick_label),
                subtitle = stringResource(R.string.pomodoro_tick_subtitle),
                leadingIcon = KronoIcons.Feature.Timer,
                checked = config.pomodoroTickingSound,
                onChange = { enabled ->
                    scope.launch { dataStore.updateConfig(config.copy(pomodoroTickingSound = enabled)) }
                }
            )
            SettingsDivider()
            AppearanceSlider(
                label = stringResource(R.string.settings_volume_tick),
                value = config.tickVolume,
                minLabel = volumeMinLabel,
                maxLabel = volumeMaxLabel,
                range = 0f..1f,
                display = "${(config.tickVolume * 100).toInt()}%",
                onChange = { value ->
                    scope.launch { dataStore.updateConfig(config.copy(tickVolume = value)) }
                },
                modifier = Modifier.padding(
                    horizontal = KronoTokens.Settings.panelHorizontalInset,
                    vertical = KronoTokens.Spacing.sm
                )
            )
            SettingsDivider()
            KronoDropdown(
                value = config.pomodoroTickSoundType,
                onValueChange = { value ->
                    scope.launch { dataStore.updateConfig(config.copy(pomodoroTickSoundType = value)) }
                    playPomodoroTick(context, config.tickVolume, value)
                },
                options = listOf("TICK_A", "TICK_B", "TICK_C", "TICK_D"),
                label = stringResource(R.string.pomodoro_tick_sound_choice),
                leadingIcon = KronoIcons.Action.Volume
            )
            SettingsDivider()
            ToggleRow(
                label = stringResource(R.string.pomodoro_focus_alert_toggle),
                subtitle = stringResource(R.string.pomodoro_focus_alert_subtitle),
                leadingIcon = KronoIcons.Action.Focus,
                checked = config.pomodoroFocusAlertEnabled,
                onChange = { enabled ->
                    scope.launch { dataStore.updateConfig(config.copy(pomodoroFocusAlertEnabled = enabled)) }
                }
            )
            SettingsDivider()
            AppearanceSlider(
                label = stringResource(R.string.settings_volume_focus_alert),
                value = config.focusAlertVolume,
                minLabel = volumeMinLabel,
                maxLabel = volumeMaxLabel,
                range = 0f..1f,
                display = "${(config.focusAlertVolume * 100).toInt()}%",
                onChange = { value ->
                    scope.launch { dataStore.updateConfig(config.copy(focusAlertVolume = value)) }
                },
                modifier = Modifier.padding(
                    horizontal = KronoTokens.Settings.panelHorizontalInset,
                    vertical = KronoTokens.Spacing.sm
                )
            )
            SettingsDivider()
            KronoDropdown(
                value = config.pomodoroFocusAlertSoundType,
                onValueChange = { value ->
                    scope.launch { dataStore.updateConfig(config.copy(pomodoroFocusAlertSoundType = value)) }
                    playPomodoroPhaseBeep(
                        context = context,
                        isFocusPhase = true,
                        volume = config.focusAlertVolume,
                        soundType = value
                    )
                },
                options = listOf("FOCUS_A", "FOCUS_B", "FOCUS_C", "FOCUS_D"),
                label = stringResource(R.string.pomodoro_focus_sound_choice),
                leadingIcon = KronoIcons.Action.Volume
            )
            SettingsDivider()
            ToggleRow(
                label = stringResource(R.string.pomodoro_break_alert_toggle),
                subtitle = stringResource(R.string.pomodoro_break_alert_subtitle),
                leadingIcon = KronoIcons.Feature.HourglassBottom,
                checked = config.pomodoroBreakAlertEnabled,
                onChange = { enabled ->
                    scope.launch { dataStore.updateConfig(config.copy(pomodoroBreakAlertEnabled = enabled)) }
                }
            )
            SettingsDivider()
            AppearanceSlider(
                label = stringResource(R.string.settings_volume_break_alert),
                value = config.breakAlertVolume,
                minLabel = volumeMinLabel,
                maxLabel = volumeMaxLabel,
                range = 0f..1f,
                display = "${(config.breakAlertVolume * 100).toInt()}%",
                onChange = { value ->
                    scope.launch { dataStore.updateConfig(config.copy(breakAlertVolume = value)) }
                },
                modifier = Modifier.padding(
                    horizontal = KronoTokens.Settings.panelHorizontalInset,
                    vertical = KronoTokens.Spacing.sm
                )
            )
            SettingsDivider()
            KronoDropdown(
                value = config.pomodoroBreakAlertSoundType,
                onValueChange = { value ->
                    scope.launch { dataStore.updateConfig(config.copy(pomodoroBreakAlertSoundType = value)) }
                    playPomodoroPhaseBeep(
                        context = context,
                        isFocusPhase = false,
                        volume = config.breakAlertVolume,
                        soundType = value
                    )
                },
                options = listOf("BREAK_A", "BREAK_B", "BREAK_C", "BREAK_D"),
                label = stringResource(R.string.pomodoro_break_sound_choice),
                leadingIcon = KronoIcons.Action.Volume
            )
            SettingsDivider()
            ToggleRow(
                label = stringResource(R.string.pomodoro_auto_next_cycle_label),
                subtitle = stringResource(R.string.pomodoro_auto_next_cycle_subtitle),
                leadingIcon = KronoIcons.Action.Autorenew,
                checked = config.pomodoroAutoNextCycle,
                onChange = { enabled ->
                    scope.launch {
                        dataStore.updateConfig(
                            config.copy(
                                pomodoroAutoNextCycle = enabled,
                                pomodoroAutoStartBreak = enabled,
                                pomodoroAutoStartFocus = enabled
                            )
                        )
                    }
                }
            )
        }

        OverlayToolSettingsSection(
            showButtons = config.pomodoroOverlayShowButtons,
            showHours = config.pomodoroOverlayShowHours,
            showSeconds = config.pomodoroOverlayShowSeconds,
            scale = config.pomodoroOverlayScale,
            cornerRadius = config.pomodoroOverlayCornerRadius,
            customColor = config.pomodoroOverlayCustomColor,
            customTextColor = config.pomodoroOverlayCustomTextColor,
            onShowButtonsChange = { value ->
                scope.launch { dataStore.updateConfig(config.copy(pomodoroOverlayShowButtons = value)) }
            },
            onShowHoursChange = { value ->
                scope.launch { dataStore.updateConfig(config.copy(pomodoroOverlayShowHours = value)) }
            },
            onShowSecondsChange = { value ->
                scope.launch { dataStore.updateConfig(config.copy(pomodoroOverlayShowSeconds = value)) }
            },
            onScaleChange = { value ->
                scope.launch { dataStore.updateConfig(config.copy(pomodoroOverlayScale = value)) }
            },
            onCornerRadiusChange = { value ->
                scope.launch { dataStore.updateConfig(config.copy(pomodoroOverlayCornerRadius = value)) }
            },
            onCustomColorChange = { value ->
                scope.launch { dataStore.updateConfig(config.copy(pomodoroOverlayCustomColor = value)) }
            },
            onCustomTextColorChange = { value ->
                scope.launch { dataStore.updateConfig(config.copy(pomodoroOverlayCustomTextColor = value)) }
            }
        )
    }

    val currentEditing = editingPreset
    if (currentEditing != null) {
        PomodoroPresetEditorDialog(
            initialPreset = currentEditing,
            selectedFont = config.overlayFontFamily,
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
                            pomodoroCustomPhasesSpec = saved.phases.joinToString(";") { phase ->
                                "${phase.label}|${(phase.totalSeconds / 60L).coerceAtLeast(1L)}|${phase.color.toLong()}|${phase.soundType}"
                            }
                        )
                    )
                }
                editingPreset = null
            }
        )
    }
}

@Composable
private fun PomodoroPresetEditorDialog(
    initialPreset: PomodoroPresetConfig,
    selectedFont: String,
    onDismiss: () -> Unit,
    onSave: (PomodoroPresetConfig) -> Unit
) {
    var name by rememberSaveable(initialPreset.id) { mutableStateOf(initialPreset.name) }
    var cycles by rememberSaveable(initialPreset.id) { mutableStateOf(initialPreset.cycles.coerceAtLeast(1)) }
    val intervalLabelTemplate = stringResource(R.string.pomodoro_interval_default_label)
    val phases = remember(initialPreset.id) {
        mutableStateListOf<PomodoroPhaseConfig>().apply {
            addAll(PomodoroPresetCatalog.ensureRequiredPhases(initialPreset.phases))
        }
    }
    var editingPhaseIndex by remember { mutableStateOf<Int?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val safeName = name.trim().ifBlank { initialPreset.name }.take(50)
                    onSave(
                        initialPreset.copy(
                            name = safeName,
                            cycles = cycles.coerceAtLeast(1),
                            phases = PomodoroPresetCatalog.ensureRequiredPhases(phases.toList())
                        )
                    )
                }
            ) {
                Text(stringResource(R.string.action_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        },
        title = { Text(stringResource(R.string.pomodoro_preset_dialog_title)) },
        text = {
            androidx.compose.foundation.layout.Column(
                verticalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.sm)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { value -> name = value.take(50) },
                    label = { Text(stringResource(R.string.pomodoro_preset_label_name)) },
                    modifier = Modifier.fillMaxWidth()
                )
                AppearanceSlider(
                    label = stringResource(R.string.pomodoro_custom_cycles),
                    value = cycles.toFloat(),
                    minLabel = stringResource(R.string.settings_value_one),
                    maxLabel = stringResource(R.string.settings_value_twelve),
                    range = 1f..12f,
                    display = cycles.toString(),
                    onChange = { cycles = it.toInt() }
                )
                phases.forEachIndexed { index, phase ->
                    SettingsDivider()
                    SettingsRow(
                        title = phase.label.ifBlank { stringResource(R.string.pomodoro_interval_default_label, index + 1) },
                        subtitle = formatAsHhMmSs(phase.totalSeconds),
                        leadingIcon = KronoIcons.Feature.HourglassBottom,
                        trailing = {
                            Icon(
                                imageVector = KronoIcons.Action.Settings,
                                contentDescription = stringResource(R.string.action_edit)
                            )
                        },
                        onClick = { editingPhaseIndex = index }
                    )
                }
                TextButton(
                    onClick = {
                        phases.add(
                            PomodoroPhaseConfig(
                                id = "p${phases.size + 1}",
                                label = String.format(intervalLabelTemplate, phases.size + 1),
                                totalSeconds = 5 * 60L,
                                color = 0xFF60A5FA.toInt(),
                                soundType = "BREAK_A"
                            )
                        )
                    }
                ) {
                    Icon(
                        imageVector = KronoIcons.Action.AddCircle,
                        contentDescription = null
                    )
                    Text(stringResource(R.string.pomodoro_interval_add))
                }
            }
        }
    )

    val currentPhaseIndex = editingPhaseIndex
    if (currentPhaseIndex != null) {
        val phase = phases[currentPhaseIndex]
        PomodoroPhaseEditorDialog(
            initialPhase = phase,
            selectedFont = selectedFont,
            onDismiss = { editingPhaseIndex = null },
            onSave = { updated ->
                phases[currentPhaseIndex] = updated
                editingPhaseIndex = null
            }
        )
    }
}

@Composable
private fun PomodoroPhaseEditorDialog(
    initialPhase: PomodoroPhaseConfig,
    selectedFont: String,
    onDismiss: () -> Unit,
    onSave: (PomodoroPhaseConfig) -> Unit
) {
    var label by rememberSaveable(initialPhase.id) { mutableStateOf(initialPhase.label) }
    var seconds by rememberSaveable(initialPhase.id) { mutableStateOf(initialPhase.totalSeconds.coerceAtLeast(1L)) }
    var color by rememberSaveable(initialPhase.id) { mutableStateOf(initialPhase.color) }
    var soundType by rememberSaveable(initialPhase.id) { mutableStateOf(initialPhase.soundType) }
    var showColorPicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        initialPhase.copy(
                            label = label.trim().ifBlank { initialPhase.label }.take(50),
                            totalSeconds = seconds.coerceAtLeast(1L),
                            color = color,
                            soundType = soundType
                        )
                    )
                }
            ) {
                Text(stringResource(R.string.action_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        },
        title = { Text(stringResource(R.string.pomodoro_interval_edit_title)) },
        text = {
            androidx.compose.foundation.layout.Column(
                verticalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.sm)
            ) {
                OutlinedTextField(
                    value = label,
                    onValueChange = { value -> label = value.take(50) },
                    label = { Text(stringResource(R.string.pomodoro_preset_label_name)) },
                    modifier = Modifier.fillMaxWidth()
                )
                CountdownScreenWheelPicker(
                    totalSeconds = seconds,
                    numberFontSize = KronoTokens.Typography.timerCard,
                    fontFamily = timerFontFamily(selectedFont),
                    onValueChange = { value -> seconds = value.coerceAtLeast(1L) },
                    modifier = Modifier.fillMaxWidth()
                )
                SettingsRow(
                    title = stringResource(R.string.pomodoro_custom_phase_color),
                    subtitle = "#%06X".format(color and 0xFFFFFF),
                    leadingIcon = KronoIcons.Action.FormatPaint,
                    onClick = { showColorPicker = true }
                )
                KronoDropdown(
                    value = soundType,
                    onValueChange = { soundType = it },
                    options = PHASE_SOUND_OPTIONS,
                    label = stringResource(R.string.pomodoro_interval_alert_sound_label),
                    leadingIcon = KronoIcons.Action.Volume
                )
            }
        }
    )

    if (showColorPicker) {
        ColorPickerDialog(
            title = stringResource(R.string.pomodoro_custom_phase_color),
            initialColor = Color(color),
            initialOpacity = 1f,
            onPreview = { _, _ -> },
            onConfirm = { updatedColor, _ ->
                color = updatedColor.toArgb()
                showColorPicker = false
            },
            onDismiss = { showColorPicker = false }
        )
    }
}

private fun nextUserPresetIndex(presets: List<PomodoroPresetConfig>): Int {
    var index = 1
    while (presets.any { it.id == "USR_$index" }) index++
    return index
}

private fun upsertPreset(
    presets: List<PomodoroPresetConfig>,
    preset: PomodoroPresetConfig
): List<PomodoroPresetConfig> {
    val mapped = presets.map {
        if (it.id == preset.id) preset else it
    }.toMutableList()
    if (mapped.none { it.id == preset.id }) mapped.add(preset)
    return mapped.sortedBy {
        when (it.id) {
            PomodoroPresetCatalog.DEFAULT_ID -> 0
            PomodoroPresetCatalog.LONG_ID -> 1
            PomodoroPresetCatalog.SHORT_ID -> 2
            else -> 100
        }
    }
}

private fun formatAsHhMmSs(totalSeconds: Long): String {
    val safe = totalSeconds.coerceAtLeast(0L)
    val hh = (safe / 3600L).toInt()
    val mm = ((safe % 3600L) / 60L).toInt()
    val ss = (safe % 60L).toInt()
    return "%02d:%02d:%02d".format(hh, mm, ss)
}
