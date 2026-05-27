package com.krono.app.feature.pomodoro

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.krono.app.R
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
    var menuExpanded by remember { mutableStateOf(false) }
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
            modifier = Modifier.padding(vertical = KronoTokens.PresetEditor.innerGap),
            verticalArrangement = Arrangement.spacedBy(KronoTokens.PresetEditor.innerGap)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(KronoTokens.PresetEditor.rowHeight)
                    .padding(horizontal = KronoTokens.PresetEditor.sideInset),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(KronoTokens.PresetEditor.innerGap)
            ) {
                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.size(KronoTokens.PresetEditor.menuSlot)
                ) {
                    Icon(
                        imageVector = if (expanded) KronoIcons.Action.ExpandLess else KronoIcons.Action.ExpandMore,
                        contentDescription = if (expanded) {
                            stringResource(R.string.pomodoro_group_collapse)
                        } else {
                            stringResource(R.string.pomodoro_group_expand)
                        }
                    )
                }
                if (expanded) {
                    OutlinedTextField(
                        value = group.label,
                        onValueChange = { value -> onUpdateGroup(group.copy(label = value.take(50))) },
                        label = { Text(stringResource(R.string.pomodoro_group_name_label)) },
                        singleLine = true,
                        trailingIcon = {
                            Box {
                                IconButton(
                                    onClick = { menuExpanded = true },
                                    modifier = Modifier.size(KronoTokens.PresetEditor.menuSlot)
                                ) {
                                    Icon(
                                        imageVector = KronoIcons.Action.More,
                                        contentDescription = stringResource(R.string.action_more)
                                    )
                                }
                                DropdownMenu(
                                    expanded = menuExpanded,
                                    onDismissRequest = { menuExpanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.action_delete)) },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = KronoIcons.Action.Delete,
                                                contentDescription = null
                                            )
                                        },
                                        onClick = {
                                            menuExpanded = false
                                            onDeleteGroup()
                                        }
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(KronoTokens.PresetEditor.rowHeight)
                    )
                } else {
                    Text(
                        text = group.label.ifBlank { stringResource(R.string.pomodoro_group_default_label, rootIndex + 1) },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Box {
                        IconButton(
                            onClick = { menuExpanded = true },
                            modifier = Modifier.size(KronoTokens.PresetEditor.menuSlot)
                        ) {
                            Icon(
                                imageVector = KronoIcons.Action.More,
                                contentDescription = stringResource(R.string.action_more)
                            )
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.action_delete)) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = KronoIcons.Action.Delete,
                                        contentDescription = null
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    onDeleteGroup()
                                }
                            )
                        }
                    }
                }
            }

            if (expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = KronoTokens.PresetEditor.sideInset + KronoTokens.PresetEditor.menuSlot + KronoTokens.PresetEditor.innerGap,
                            end = KronoTokens.PresetEditor.sideInset
                        ),
                    verticalArrangement = Arrangement.spacedBy(KronoTokens.PresetEditor.innerGap)
                ) {
                    group.phases.forEachIndexed { index, phase ->
                        PomodoroPhaseCard(
                            phase = phase,
                            index = index,
                            showDelete = true,
                            dragDropState = childDragDropState,
                            onEdit = { onEditPhase(phase) },
                            onDelete = { onDeletePhase(phase.id) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    OutlinedButton(
                        onClick = onAddPhase,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(KronoTokens.PresetEditor.rowHeight)
                    ) {
                        Icon(imageVector = KronoIcons.Action.AddCircle, contentDescription = null)
                        Text(stringResource(R.string.pomodoro_card_add))
                    }
                    CycleSliderHeader(
                        title = stringResource(R.string.pomodoro_section_group_cycles),
                        value = group.cycles
                    )
                    Slider(
                        value = group.cycles.toFloat(),
                        valueRange = 1f..12f,
                        steps = 10,
                        onValueChange = { onUpdateGroup(group.copy(cycles = it.toInt().coerceIn(1, 12))) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
internal fun CycleSliderHeader(
    title: String,
    value: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(KronoTokens.PresetEditor.innerGap)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.End,
            modifier = Modifier.width(KronoTokens.PresetEditor.sliderValueWidth)
        )
    }
}
