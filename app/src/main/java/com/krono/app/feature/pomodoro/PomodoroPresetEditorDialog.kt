package com.krono.app.feature.pomodoro

import androidx.compose.animation.animateContentSize
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.krono.app.R
import com.krono.app.core.data.OverlayConfig
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoTokens
import com.krono.app.core.ui.theme.adaptiveDialogWidth
import com.krono.app.core.util.NotificationSoundOption

@Composable
internal fun PomodoroPresetEditorDialog(
    initialPreset: PomodoroPresetConfig,
    selectedFont: String,
    pomodoroVolume: Float,
    notificationSoundOptions: List<NotificationSoundOption>,
    config: OverlayConfig,
    onDismiss: () -> Unit,
    onSave: (PomodoroPresetConfig) -> Unit
) {
    val state = remember(initialPreset.id) { PomodoroPresetEditorState(initialPreset) }
    var editingPhase by remember { mutableStateOf<EditingPhase?>(null) }
    val defaultCardColor = MaterialTheme.colorScheme.surface.toArgb()
    val dialogColor = MaterialTheme.colorScheme.surface

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
                    .verticalScroll(rememberScrollState())
                    .animateContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.pomodoro_preset_dialog_title),
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

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.sm)
                ) {
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = { value -> state.name = value.take(50) },
                        label = { Text(stringResource(R.string.pomodoro_preset_label_name)) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(KronoTokens.PresetEditor.rowHeight)
                    )
                }

                Spacer(Modifier.height(KronoTokens.Spacing.sectionGap))

                PomodoroPresetItemList(
                    items = state.items,
                    cycles = state.cycles,
                    onCyclesChange = { state.cycles = it },
                    onAddCard = { state.addCard(defaultCardColor) },
                    onAddGroup = state::addGroup,
                    onRemoveItem = state::removeItem,
                    onMoveItem = state::moveItem,
                    onMoveRootCardToGroup = { cardId, groupId ->
                        val group = state.items.filterIsInstance<PomodoroPresetItem.Group>().firstOrNull { it.id == groupId }
                        state.moveCardToGroup(cardId, groupId, group?.phases?.size ?: 0)
                    },
                    onUpdateGroup = state::updateGroup,
                    onAddPhaseToGroup = { groupId -> state.addPhaseToGroup(groupId, defaultCardColor) },
                    onRemovePhaseFromGroup = state::removePhaseFromGroup,
                    onMovePhaseInGroup = state::movePhaseInGroup,
                    onMovePhaseOutOfGroup = state::moveCardOutOfGroup,
                    onEditPhase = { groupId, phase -> editingPhase = EditingPhase(groupId, phase.id) }
                )

                Spacer(Modifier.height(KronoTokens.Spacing.sectionGap))

                Button(
                    onClick = { onSave(state.toPresetConfig(initialPreset)) },
                    enabled = state.canSave,
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

    val currentEditing = editingPhase
    val currentPhase = currentEditing?.let { state.findPhase(it.groupId, it.phaseId) }
    if (currentEditing != null && currentPhase != null) {
        PomodoroPhaseEditorDialog(
            initialPhase = currentPhase,
            selectedFont = selectedFont,
            pomodoroVolume = pomodoroVolume,
            notificationSoundOptions = notificationSoundOptions,
            config = config,
            onDismiss = { editingPhase = null },
            onSave = { updated ->
                state.updatePhase(currentEditing.groupId, updated)
                editingPhase = null
            }
        )
    }
}

private data class EditingPhase(val groupId: String?, val phaseId: String)

private fun PomodoroPresetEditorState.findPhase(groupId: String?, phaseId: String): PomodoroPhaseConfig? {
    if (groupId == null) {
        return items.filterIsInstance<PomodoroPresetItem.Card>().firstOrNull { it.phase.id == phaseId }?.phase
    }
    return items.filterIsInstance<PomodoroPresetItem.Group>()
        .firstOrNull { it.id == groupId }
        ?.phases
        ?.firstOrNull { it.id == phaseId }
}
