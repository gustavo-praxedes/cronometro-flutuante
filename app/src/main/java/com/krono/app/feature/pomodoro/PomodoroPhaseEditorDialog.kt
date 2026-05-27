package com.krono.app.feature.pomodoro

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.krono.app.R
import com.krono.app.core.audio.SoundTimingPolicy
import com.krono.app.core.data.OverlayConfig
import com.krono.app.core.ui.components.KronoDropdown
import com.krono.app.core.ui.components.SettingsRow
import com.krono.app.core.ui.dialogs.ColorPickerDialog
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoTokens
import com.krono.app.core.ui.theme.adaptiveDialogWidth
import com.krono.app.core.util.NotificationSoundOption
import com.krono.app.core.util.SOUND_NONE
import com.krono.app.core.util.normalizeNotificationSound
import com.krono.app.core.util.previewPomodoroNotificationSound
import com.krono.app.core.util.stopSoundPreview
import com.krono.app.feature.countdown.TimeWheelPicker

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
    var seconds by rememberSaveable(initialPhase.id) { mutableStateOf(initialPhase.totalSeconds.coerceAtLeast(0L)) }
    var color by rememberSaveable(initialPhase.id) { mutableStateOf(initialPhase.color) }
    var soundType by rememberSaveable(initialPhase.id) { mutableStateOf(normalizeNotificationSound(initialPhase.soundType)) }
    var showColorPicker by remember { mutableStateOf(false) }
    val dialogColor = MaterialTheme.colorScheme.surface
    val soundLabels = remember(notificationSoundOptions, noneSoundLabel) {
        notificationSoundOptions.associate { option ->
            option.uriString to if (option.uriString == SOUND_NONE) noneSoundLabel else option.label
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .adaptiveDialogWidth()
                .wrapContentHeight(),
            shape = KronoTokens.Shape.dialog,
            color = dialogColor,
            tonalElevation = 0.dp,
            shadowElevation = KronoTokens.Elevation.dialog
        ) {
            Column(
                modifier = Modifier
                    .padding(KronoTokens.Spacing.dialogPadding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.pomodoro_interval_edit_title),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                        ),
                        fontWeight = FontWeight.Normal,
                        fontSize = KronoTokens.Typography.dialogTitle,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = KronoTokens.Spacing.dialogPadding)
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(KronoTokens.Icon.close)
                            .align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            imageVector = KronoIcons.Navigation.Close,
                            contentDescription = stringResource(R.string.action_close),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(KronoTokens.Spacing.sectionGap))

                OutlinedTextField(
                    value = label,
                    onValueChange = { value -> label = value.take(50) },
                    label = { Text(stringResource(R.string.pomodoro_preset_label_name)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(KronoTokens.Spacing.sectionGap))

                TimeWheelPicker(
                    totalSeconds = seconds,
                    onValueChange = { value -> seconds = value.coerceAtLeast(0L) },
                    modifier = Modifier.fillMaxWidth(),
                    fadeColor = dialogColor
                )

                Spacer(Modifier.height(KronoTokens.Spacing.sectionGap))

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

                Spacer(Modifier.height(KronoTokens.Spacing.sm))

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
                        previewPomodoroNotificationSound(
                            context,
                            pomodoroVolume,
                            normalized,
                            SoundTimingPolicy.profile(normalized).startDelayMs
                        )
                    },
                    onDismiss = { stopSoundPreview() },
                    textMapping = { sound ->
                        soundLabels[normalizeNotificationSound(sound)]
                            ?: notificationSoundOptions.firstOrNull { it.uriString != SOUND_NONE }?.label
                            ?: noneSoundLabel
                    }
                )

                Spacer(Modifier.height(KronoTokens.Spacing.sectionGap))

                Button(
                    onClick = {
                        onSave(
                            initialPhase.copy(
                                label = label.trim().ifBlank { initialPhase.label }.take(50),
                                totalSeconds = seconds.coerceAtLeast(0L),
                                color = color,
                                soundType = soundType
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(KronoTokens.Button.height),
                    shape = KronoTokens.Shape.button
                ) {
                    Text(
                        text = stringResource(R.string.action_save),
                        fontSize = KronoTokens.Typography.buttonLabel,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }

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
