package com.krono.app.feature.pomodoro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.krono.app.R
import com.krono.app.core.data.OverlayConfig
import com.krono.app.core.ui.theme.KronoTokens
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

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                enabled = state.canSave,
                onClick = { onSave(state.toPresetConfig(initialPreset)) }
            ) {
                Text(stringResource(R.string.action_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        },
        title = { Text(stringResource(R.string.pomodoro_preset_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.sm)) {
                OutlinedTextField(
                    value = state.name,
                    onValueChange = { value -> state.name = value.take(50) },
                    label = { Text(stringResource(R.string.pomodoro_preset_label_name)) },
                    modifier = Modifier.fillMaxWidth()
                )
                PomodoroPresetItemList(
                    items = state.items,
                    cycles = state.cycles,
                    onCyclesChange = { state.cycles = it },
                    onAddCard = state::addCard,
                    onAddGroup = state::addGroup,
                    onRemoveItem = state::removeItem,
                    onMoveItem = state::moveItem,
                    onMoveRootCardToGroup = { cardId, groupId ->
                        val group = state.items.filterIsInstance<PomodoroPresetItem.Group>().firstOrNull { it.id == groupId }
                        state.moveCardToGroup(cardId, groupId, group?.phases?.size ?: 0)
                    },
                    onUpdateGroup = state::updateGroup,
                    onAddPhaseToGroup = state::addPhaseToGroup,
                    onRemovePhaseFromGroup = state::removePhaseFromGroup,
                    onMovePhaseInGroup = state::movePhaseInGroup,
                    onMovePhaseOutOfGroup = state::moveCardOutOfGroup,
                    onEditPhase = { groupId, phase -> editingPhase = EditingPhase(groupId, phase.id) }
                )
                if (!state.canSave) {
                    Text(
                        text = stringResource(R.string.pomodoro_preset_empty_error),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    )

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
