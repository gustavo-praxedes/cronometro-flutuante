package com.krono.app.feature.pomodoro

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.krono.app.R
import com.krono.app.core.ui.components.SettingsRow
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

    SettingsRow(
        title = phase.label.ifBlank { stringResource(R.string.pomodoro_interval_default_label, index + 1) },
        subtitle = formatAsHhMmSs(phase.totalSeconds),
        leadingIcon = KronoIcons.Feature.HourglassBottom,
        modifier = modifier,
        trailing = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(KronoTokens.Icon.button)
                        .border(
                            width = KronoTokens.Stroke.divider,
                            color = MaterialTheme.colorScheme.outlineVariant,
                            shape = CircleShape
                        )
                        .background(Color(phase.color), CircleShape)
                )
                if (dragDropState != null) {
                    IconButton(
                        onClick = {},
                        modifier = Modifier
                            .size(KronoTokens.Size.iconBox)
                            .dragHandle(
                                state = dragDropState,
                                index = index,
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
                if (showDelete) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(KronoTokens.Size.iconBox)
                    ) {
                        Icon(
                            imageVector = KronoIcons.Action.Delete,
                            contentDescription = stringResource(R.string.action_delete)
                        )
                    }
                }
                Icon(
                    imageVector = KronoIcons.Navigation.ChevronRight,
                    contentDescription = stringResource(R.string.action_edit)
                )
            }
        },
        onClick = onEdit
    )
}
