package com.krono.app.feature.pomodoro

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.krono.app.R
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoTokens

@Composable
internal fun PomodoroPhaseCard(
    phase: PomodoroPhaseConfig,
    index: Int,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    showDelete: Boolean = true,
    dragDropState: DragDropState? = null
) {
    val haptic = LocalHapticFeedback.current
    var menuExpanded by remember { mutableStateOf(false) }
    val isDragging = dragDropState?.draggingItemIndex == index
    val dragModifier = if (dragDropState != null) {
        Modifier.dragSource(
            state = dragDropState,
            index = index,
            onDragStarted = { haptic.performHapticFeedback(HapticFeedbackType.LongPress) },
            onDragFinished = { haptic.performHapticFeedback(HapticFeedbackType.LongPress) }
        )
    } else {
        Modifier
    }

    Surface(
        onClick = onEdit,
        shape = KronoTokens.Shape.card,
        tonalElevation = 0.dp,
        color = Color(phase.color),
        modifier = modifier
            .fillMaxWidth()
            .scale(if (isDragging) 0.97f else 1f)
            .then(dragModifier)
            .height(KronoTokens.PresetEditor.rowHeight)
            .border(
                width = KronoTokens.Stroke.divider,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = KronoTokens.Shape.card
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(KronoTokens.PresetEditor.rowHeight)
                .padding(horizontal = KronoTokens.PresetEditor.sideInset),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(KronoTokens.PresetEditor.innerGap)
        ) {
            Text(
                text = phase.label.ifBlank { stringResource(R.string.pomodoro_interval_default_label, index + 1) },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = formatAsHhMmSs(phase.totalSeconds),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                modifier = Modifier.width(KronoTokens.PresetEditor.timeWidth)
            )
            if (showDelete) {
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
                                onDelete()
                            }
                        )
                    }
                }
            }
        }
    }
}
