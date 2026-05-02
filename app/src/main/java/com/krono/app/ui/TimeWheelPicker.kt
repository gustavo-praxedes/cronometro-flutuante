package com.krono.app.ui

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.distinctUntilChanged

private const val ITEM_H_DP = 48

/**
 * Slot-machine time picker — HH:MM:SS.
 * Hours 00-99, Minutes 00-59, Seconds 00-59.
 * [onValueChange] fires whenever the user settles on a new value.
 */
@Composable
fun TimeWheelPicker(
    totalSeconds: Long,
    onValueChange: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val initH = (totalSeconds / 3600).toInt().coerceIn(0, 99)
    val initM = ((totalSeconds % 3600) / 60).toInt()
    val initS = (totalSeconds % 60).toInt()

    // Mutable holders updated by each wheel
    val h = remember { androidx.compose.runtime.mutableIntStateOf(initH) }
    val m = remember { androidx.compose.runtime.mutableIntStateOf(initM) }
    val s = remember { androidx.compose.runtime.mutableIntStateOf(initS) }

    fun emit() {
        onValueChange(h.intValue * 3600L + m.intValue * 60L + s.intValue)
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        WheelColumn(range = 0..99, initial = initH, onSelected = { h.intValue = it; emit() })
        Separator()
        WheelColumn(range = 0..59, initial = initM, onSelected = { m.intValue = it; emit() })
        Separator()
        WheelColumn(range = 0..59, initial = initS, onSelected = { s.intValue = it; emit() })
    }
}

@Composable
private fun WheelColumn(
    range: IntRange,
    initial: Int,
    onSelected: (Int) -> Unit,
    itemH: Dp = ITEM_H_DP.dp,
    visible: Int = 3
) {
    val items = range.toList()
    // +1 because we add a padding item at start
    val initIndex = (initial - range.first).coerceIn(0, items.lastIndex)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initIndex)
    val snapBehavior = rememberSnapFlingBehavior(listState)

    // Index of item currently centred in the viewport
    val centredIndex by remember {
        derivedStateOf {
            val info = listState.layoutInfo.visibleItemsInfo
            val viewportCenter = listState.layoutInfo.viewportStartOffset +
                    listState.layoutInfo.viewportSize.height / 2
            info.minByOrNull { kotlin.math.abs(it.offset + it.size / 2 - viewportCenter) }
                ?.index
                ?.minus(1)   // compensate leading padding item
                ?.coerceIn(0, items.lastIndex)
                ?: initIndex
        }
    }

    // Emit value changes when scroll settles
    LaunchedEffect(listState) {
        snapshotFlow { centredIndex }
            .distinctUntilChanged()
            .collect { idx -> onSelected(items[idx]) }
    }

    val containerH = itemH * visible

    Box(contentAlignment = Alignment.Center) {
        // Selection rails
        HorizontalDivider(
            modifier = Modifier
                .width(56.dp)
                .align(Alignment.Center)
                .graphicsLayer { translationY = -(itemH.toPx() / 2) },
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
            thickness = 1.5.dp
        )
        HorizontalDivider(
            modifier = Modifier
                .width(56.dp)
                .align(Alignment.Center)
                .graphicsLayer { translationY = itemH.toPx() / 2 },
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
            thickness = 1.5.dp
        )

        LazyColumn(
            state = listState,
            flingBehavior = snapBehavior,
            modifier = Modifier
                .width(56.dp)
                .height(containerH),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Leading padding
            item { Box(Modifier.height(itemH)) }

            items(items.size) { index ->
                val dist = kotlin.math.abs(index - centredIndex)
                val alpha = when (dist) { 0 -> 1f; 1 -> 0.45f; else -> 0.18f }
                val scale = if (dist == 0) 1f else 0.8f

                Box(
                    modifier = Modifier
                        .height(itemH)
                        .alpha(alpha)
                        .graphicsLayer { scaleX = scale; scaleY = scale },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = items[index].toString().padStart(2, '0'),
                        fontSize = if (dist == 0) 28.sp else 20.sp,
                        fontWeight = if (dist == 0) FontWeight.Bold else FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Trailing padding
            item { Box(Modifier.height(itemH)) }
        }
    }
}

@Composable
private fun Separator() {
    Text(
        ":",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        modifier = Modifier.width(16.dp),
        textAlign = TextAlign.Center
    )
}
