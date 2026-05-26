package com.krono.app.feature.pomodoro

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.krono.app.R
import com.krono.app.core.ui.components.AppearanceSlider
import com.krono.app.core.ui.components.SettingsDivider
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoTokens

@Composable
internal fun PomodoroGroupCard(
    group: PomodoroPresetItem.Group,
    rootIndex: Int,
    onUpdateGroup: (PomodoroPresetItem.Group) -> Unit,
    onDeleteGroup: () -> Unit,
    onAddPhase: () -> Unit,
    onEditPhase: (PomodoroPhaseConfig) -> Unit,
    onDeletePhase: (String) -> Unit,
    onMovePhase: (from: Int, to: Int) -> Unit,
    onMovePhaseOut: (String) -> Unit,
    modifier: Modifier = Modifier,
    rootDragDropState: DragDropState? = null
) {
    var expanded by remember(group.id) { mutableStateOf(true) }
    val haptic = LocalHapticFeedback.current
    val childListState = rememberLazyListState()
    val childDragDropState = rememberDragDropState(
        lazyListState = childListState,
        itemIdAt = { index -> group.phases.getOrNull(index)?.id },
        onMove = onMovePhase,
        onDragOut = onMovePhaseOut
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .border(
                width = KronoTokens.Stroke.divider,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = KronoTokens.Shape.card
            ),
        shape = KronoTokens.Shape.card,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.22f)
    ) {
        Column(
            modifier = Modifier.padding(vertical = KronoTokens.Spacing.xs),
            verticalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.xs)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = KronoTokens.Settings.panelHorizontalInset),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.xs)
            ) {
                if (rootDragDropState != null) {
                    IconButton(
                        onClick = {},
                        modifier = Modifier
                            .size(KronoTokens.Size.iconBox)
                            .dragHandle(
                                state = rootDragDropState,
                                index = rootIndex,
                                onDragStarted = { haptic.performHapticFeedback(HapticFeedbackType.LongPress) },
                                onDragFinished = { haptic.performHapticFeedback(HapticFeedbackType.LongPress) }
                            )
                    ) {
                        Icon(
                            imageVector = KronoIcons.Action.Drag,
                            contentDescription = stringResource(R.string.pomodoro_drag_handle)
                        )
                    }
                }
                Text(
                    text = group.label.ifBlank { stringResource(R.string.pomodoro_group_default_label, rootIndex + 1) },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDeleteGroup, modifier = Modifier.size(KronoTokens.Size.iconBox)) {
                    Icon(
                        imageVector = KronoIcons.Action.Delete,
                        contentDescription = stringResource(R.string.action_delete)
                    )
                }
                IconButton(onClick = { expanded = !expanded }, modifier = Modifier.size(KronoTokens.Size.iconBox)) {
                    Icon(
                        imageVector = if (expanded) KronoIcons.Action.ExpandLess else KronoIcons.Action.ExpandMore,
                        contentDescription = if (expanded) {
                            stringResource(R.string.pomodoro_group_collapse)
                        } else {
                            stringResource(R.string.pomodoro_group_expand)
                        }
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.padding(horizontal = KronoTokens.Spacing.sm),
                    verticalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.xs)
                ) {
                    OutlinedTextField(
                        value = group.label,
                        onValueChange = { value -> onUpdateGroup(group.copy(label = value.take(50))) },
                        label = { Text(stringResource(R.string.pomodoro_group_name_label)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    AppearanceSlider(
                        label = stringResource(R.string.pomodoro_group_cycles_inline, group.cycles),
                        value = group.cycles.toFloat(),
                        minLabel = stringResource(R.string.settings_value_one),
                        maxLabel = stringResource(R.string.settings_value_twelve),
                        range = 1f..12f,
                        display = group.cycles.toString(),
                        onChange = { onUpdateGroup(group.copy(cycles = it.toInt().coerceIn(1, 12))) }
                    )
                    group.phases.forEachIndexed { index, phase ->
                        SettingsDivider()
                        PomodoroPhaseCard(
                            phase = phase,
                            index = index,
                            showDelete = true,
                            dragDropState = childDragDropState,
                            onEdit = { onEditPhase(phase) },
                            onDelete = { onDeletePhase(phase.id) },
                            modifier = Modifier.padding(start = KronoTokens.Spacing.sm)
                        )
                    }
                    OutlinedButton(
                        onClick = onAddPhase,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    ) {
                        Icon(imageVector = KronoIcons.Action.AddCircle, contentDescription = null)
                        Text(stringResource(R.string.pomodoro_card_add))
                    }
                }
            }
        }
    }
}
