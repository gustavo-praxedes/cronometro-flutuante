package com.krono.app.feature.pomodoro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.krono.app.R
import com.krono.app.core.ui.components.AppearanceSlider
import com.krono.app.core.ui.components.SettingsDivider
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoTokens

@Composable
internal fun PomodoroPresetItemList(
    items: List<PomodoroPresetItem>,
    cycles: Int,
    onCyclesChange: (Int) -> Unit,
    onAddCard: () -> Unit,
    onAddGroup: () -> Unit,
    onRemoveItem: (String) -> Unit,
    onMoveItem: (from: Int, to: Int) -> Unit,
    onMoveRootCardToGroup: (cardId: String, groupId: String) -> Unit,
    onUpdateGroup: (PomodoroPresetItem.Group) -> Unit,
    onAddPhaseToGroup: (String) -> Unit,
    onRemovePhaseFromGroup: (String, String) -> Unit,
    onMovePhaseInGroup: (String, Int, Int) -> Unit,
    onMovePhaseOutOfGroup: (String, String, Int) -> Unit,
    onEditPhase: (groupId: String?, phase: PomodoroPhaseConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val rootDragDropState = rememberDragDropState(
        lazyListState = listState,
        itemIdAt = { index -> items.getOrNull(index)?.id },
        onMove = onMoveItem,
        onDrop = { itemId, _, atIndex ->
            val dragged = items.firstOrNull { it.id == itemId }
            if (dragged is PomodoroPresetItem.Card) {
                val target = items.getOrNull(atIndex + 1) as? PomodoroPresetItem.Group
                    ?: items.getOrNull(atIndex - 1) as? PomodoroPresetItem.Group
                if (target != null) onMoveRootCardToGroup(itemId, target.id)
            }
        }
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.sm)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 420.dp),
            verticalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.sm)
        ) {
            itemsIndexed(items, key = { _, item -> item.id }) { index, item ->
                when (item) {
                    is PomodoroPresetItem.Card -> {
                        PomodoroPhaseCard(
                            phase = item.phase,
                            index = index,
                            showDelete = true,
                            dragDropState = rootDragDropState,
                            onEdit = { onEditPhase(null, item.phase) },
                            onDelete = { onRemoveItem(item.id) }
                        )
                    }
                    is PomodoroPresetItem.Group -> {
                        PomodoroGroupCard(
                            group = item,
                            rootIndex = index,
                            rootDragDropState = rootDragDropState,
                            onUpdateGroup = onUpdateGroup,
                            onDeleteGroup = { onRemoveItem(item.id) },
                            onAddPhase = { onAddPhaseToGroup(item.id) },
                            onEditPhase = { phase -> onEditPhase(item.id, phase) },
                            onDeletePhase = { phaseId -> onRemovePhaseFromGroup(item.id, phaseId) },
                            onMovePhase = { from, to -> onMovePhaseInGroup(item.id, from, to) },
                            onMovePhaseOut = { phaseId -> onMovePhaseOutOfGroup(item.id, phaseId, index + 1) }
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.sm)
        ) {
            OutlinedButton(onClick = onAddCard, modifier = Modifier.weight(1f)) {
                Icon(imageVector = KronoIcons.Action.AddCircle, contentDescription = null)
                Text(stringResource(R.string.pomodoro_card_add))
            }
            OutlinedButton(onClick = onAddGroup, modifier = Modifier.weight(1f)) {
                Icon(imageVector = KronoIcons.Action.ListAltAdd, contentDescription = null)
                Text(stringResource(R.string.pomodoro_group_add))
            }
        }

        SettingsDivider()
        AppearanceSlider(
            label = stringResource(R.string.pomodoro_custom_cycles_inline, cycles),
            value = cycles.toFloat(),
            minLabel = stringResource(R.string.settings_value_one),
            maxLabel = stringResource(R.string.settings_value_twelve),
            range = 1f..12f,
            display = cycles.toString(),
            onChange = { onCyclesChange(it.toInt().coerceIn(1, 12)) }
        )
    }
}
