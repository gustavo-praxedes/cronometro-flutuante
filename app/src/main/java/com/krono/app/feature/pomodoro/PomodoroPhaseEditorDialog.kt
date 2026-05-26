package com.krono.app.feature.pomodoro

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.krono.app.R
import com.krono.app.core.audio.SoundTimingPolicy
import com.krono.app.core.data.OverlayConfig
import com.krono.app.core.ui.components.KronoDropdown
import com.krono.app.core.ui.components.SettingsRow
import com.krono.app.core.ui.dialogs.ColorPickerDialog
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoTokens
import com.krono.app.core.ui.theme.timerFontFamily
import com.krono.app.core.util.NotificationSoundOption
import com.krono.app.core.util.SOUND_NONE
import com.krono.app.core.util.normalizeNotificationSound
import com.krono.app.core.util.previewPomodoroNotificationSound
import com.krono.app.core.util.stopSoundPreview
import com.krono.app.feature.countdown.CountdownScreenWheelPicker

@Composable
internal fun PomodoroPhaseEditorDialog(
    initialPhase: PomodoroPhaseConfig,
    selectedFont: String,
    pomodoroVolume: Float,
    notificationSoundOptions: List<NotificationSoundOption>,
    config: OverlayConfig,
    onDismiss: () -> Unit,
    onSave: (PomodoroPhaseConfig) -> Unit
) {
    val context = LocalContext.current
    val noneSoundLabel = stringResource(R.string.sound_none)
    var label by rememberSaveable(initialPhase.id) { mutableStateOf(initialPhase.label) }
    var seconds by rememberSaveable(initialPhase.id) { mutableStateOf(initialPhase.totalSeconds.coerceAtLeast(1L)) }
    var color by rememberSaveable(initialPhase.id) { mutableStateOf(initialPhase.color) }
    var soundType by rememberSaveable(initialPhase.id) { mutableStateOf(normalizeNotificationSound(initialPhase.soundType)) }
    var showColorPicker by remember { mutableStateOf(false) }
    val dialogColor = MaterialTheme.colorScheme.surfaceColorAtElevation(KronoTokens.Elevation.dialog)
    val soundLabels = remember(notificationSoundOptions, noneSoundLabel) {
        notificationSoundOptions.associate { option ->
            option.uriString to if (option.uriString == SOUND_NONE) noneSoundLabel else option.label
        }
    }

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
                    modifier = Modifier.fillMaxWidth(),
                    fadeColor = dialogColor
                )
                SettingsRow(
                    title = stringResource(R.string.pomodoro_custom_phase_color),
                    subtitle = "#%06X".format(color and 0xFFFFFF),
                    leadingIcon = KronoIcons.Action.FormatPaint,
                    trailing = {
                        Box(
                            modifier = Modifier
                                .size(KronoTokens.Icon.button)
                                .border(
                                    width = KronoTokens.Stroke.divider,
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    shape = CircleShape
                                )
                                .background(Color(color), CircleShape)
                        )
                    },
                    onClick = { showColorPicker = true }
                )
                KronoDropdown(
                    value = soundType,
                    onValueChange = {
                        soundType = normalizeNotificationSound(it)
                    },
                    options = notificationSoundOptions.map { it.uriString },
                    label = stringResource(R.string.pomodoro_interval_alert_sound_label),
                    leadingIcon = KronoIcons.Action.Volume,
                    optionLeadingIcon = KronoIcons.Action.Volume,
                    optionLeadingContentDescription = stringResource(R.string.settings_sound_preview),
                    optionLeadingIconVisible = { it != SOUND_NONE },
                    onOptionLeadingClick = { sound ->
                        val normalized = normalizeNotificationSound(sound)
                        previewPomodoroNotificationSound(context, pomodoroVolume, normalized, SoundTimingPolicy.profile(normalized).startDelayMs)
                    },
                    onDismiss = { stopSoundPreview() },
                    textMapping = { sound ->
                        soundLabels[normalizeNotificationSound(sound)]
                            ?: notificationSoundOptions.firstOrNull { it.uriString != SOUND_NONE }?.label
                            ?: noneSoundLabel
                    }
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
