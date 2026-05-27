package com.krono.app.feature.pomodoro

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.absoluteValue
import kotlin.math.sign

@Composable
internal fun rememberDragDropState(
    lazyListState: LazyListState,
    itemIdAt: (Int) -> String?,
    onMove: (from: Int, to: Int) -> Unit,
    onDragOut: (itemId: String) -> Unit = {},
    onDrop: (itemId: String, targetGroupId: String?, atIndex: Int) -> Unit = { _, _, _ -> }
): DragDropState {
    val currentItemIdAt by rememberUpdatedState(itemIdAt)
    val currentOnMove by rememberUpdatedState(onMove)
    val currentOnDragOut by rememberUpdatedState(onDragOut)
    val currentOnDrop by rememberUpdatedState(onDrop)
    return remember(lazyListState) {
        DragDropState(
            lazyListState = lazyListState,
            itemIdAt = { index -> currentItemIdAt(index) },
            onMove = { from, to -> currentOnMove(from, to) },
            onDragOut = { itemId -> currentOnDragOut(itemId) },
            onDrop = { itemId, targetGroupId, atIndex -> currentOnDrop(itemId, targetGroupId, atIndex) }
        )
    }
}

internal class DragDropState(
    private val lazyListState: LazyListState,
    private val itemIdAt: (Int) -> String?,
    private val onMove: (from: Int, to: Int) -> Unit,
    private val onDragOut: (itemId: String) -> Unit,
    private val onDrop: (itemId: String, targetGroupId: String?, atIndex: Int) -> Unit
) {
    var draggingItemIndex by mutableStateOf<Int?>(null)
        private set
    var draggedOverIndex by mutableStateOf<Int?>(null)
        private set
    private var dragOffset by mutableFloatStateOf(0f)
    private var dragOutOffset by mutableFloatStateOf(0f)
    private var draggingItemId by mutableStateOf<String?>(null)

    fun onDragStart(index: Int) {
        draggingItemIndex = index
        draggedOverIndex = index
        draggingItemId = itemIdAt(index)
        dragOffset = 0f
        dragOutOffset = 0f
    }

    fun onDrag(offset: Float) {
        onDrag(Offset(0f, offset))
    }

    fun onDrag(offset: Offset) {
        val current = draggingItemIndex ?: return
        dragOffset += offset.y
        dragOutOffset += offset.x
        val threshold = lazyListState.layoutInfo.visibleItemsInfo
            .firstOrNull { it.index == current }
            ?.size
            ?.let { it * 0.55f }
            ?: 48f
        if (dragOffset.absoluteValue < threshold) return

        val direction = dragOffset.sign.toInt()
        val target = (current + direction).coerceAtLeast(0)
        onMove(current, target)
        draggingItemIndex = target
        draggedOverIndex = target
        dragOffset = 0f
    }

    fun onDragEnd(targetGroupId: String? = null, atIndex: Int? = null) {
        val itemId = draggingItemId
        val index = draggingItemIndex
        if (itemId != null && index != null) {
            if (dragOutOffset.absoluteValue > 96f) {
                onDragOut(itemId)
            } else {
                onDrop(itemId, targetGroupId, atIndex ?: index)
            }
        }
        draggingItemIndex = null
        draggedOverIndex = null
        draggingItemId = null
        dragOffset = 0f
        dragOutOffset = 0f
    }
}

internal fun Modifier.dragSource(
    state: DragDropState,
    index: Int,
    onDragStarted: () -> Unit = {},
    onDragFinished: () -> Unit = {}
): Modifier =
    pointerInput(state, index) {
        detectDragGesturesAfterLongPress(
            onDragStart = {
                onDragStarted()
                state.onDragStart(index)
            },
            onDragEnd = {
                state.onDragEnd()
                onDragFinished()
            },
            onDragCancel = {
                state.onDragEnd()
                onDragFinished()
            },
            onDrag = { change, dragAmount ->
                change.consume()
                state.onDrag(dragAmount)
            }
        )
    }
