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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

/**
 * Slot-machine style time picker.
 * Returns selected value as totalSeconds via [onValueChange].
 *
 * Hours: 0–99, Minutes: 0–59, Seconds: 0–59
 */
@Composable
fun TimeWheelPicker(
    totalSeconds: Long,
    onValueChange: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val hours = (totalSeconds / 3600).toInt().coerceIn(0, 99)
    val minutes = ((totalSeconds % 3600) / 60).toInt()
    val seconds = (totalSeconds % 60).toInt()

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Hours (0-99, 2 digits)
        WheelColumn(
            items = (0..99).toList(),
            selected = hours,
            label = "hh",
            onSelected = { h -> onValueChange(h * 3600L + minutes * 60L + seconds) }
        )

        WheelSeparator()

        // Minutes (0-59)
        WheelColumn(
            items = (0..59).toList(),
            selected = minutes,
            label = "mm",
            onSelected = { m -> onValueChange(hours * 3600L + m * 60L + seconds) }
        )

        WheelSeparator()

        // Seconds (0-59)
        WheelColumn(
            items = (0..59).toList(),
            selected = seconds,
            label = "ss",
            onSelected = { s -> onValueChange(hours * 3600L + minutes * 60L + s) }
        )
    }
}

@Composable
private fun WheelColumn(
    items: List<Int>,
    selected: Int,
    label: String,
    onSelected: (Int) -> Unit,
    itemHeightDp: Dp = 48.dp,
    visibleItems: Int = 3
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = selected)
    val snapBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    val centeredIndex by remember {
        derivedStateOf {
            val offset = listState.firstVisibleItemScrollOffset
            val height = listState.layoutInfo.visibleItemsInfo.firstOrNull()?.size ?: 1
            val idx = listState.firstVisibleItemIndex
            if (offset > height / 2) idx + 1 else idx
        }
    }

    LaunchedEffect(centeredIndex) {
        if (centeredIndex in items.indices) {
            onSelected(items[centeredIndex])
        }
    }

    // Scroll to selected when component first appears
    LaunchedEffect(selected) {
        if (!listState.isScrollInProgress) {
            listState.scrollToItem(selected)
        }
    }

    val containerHeight = itemHeightDp * visibleItems

    Box(contentAlignment = Alignment.Center) {
        // Selection indicators
        HorizontalDivider(
            modifier = Modifier
                .width(64.dp)
                .align(Alignment.Center)
                .graphicsLayer { translationY = -itemHeightDp.toPx() / 2 },
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
        HorizontalDivider(
            modifier = Modifier
                .width(64.dp)
                .align(Alignment.Center)
                .graphicsLayer { translationY = itemHeightDp.toPx() / 2 },
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )

        LazyColumn(
            state = listState,
            flingBehavior = snapBehavior,
            modifier = Modifier
                .width(64.dp)
                .height(containerHeight),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Padding items so first/last are centerable
            item { Box(modifier = Modifier.height(itemHeightDp)) }

            items(items.size) { index ->
                val distance = abs(index - centeredIndex)
                val alpha = when (distance) {
                    0 -> 1f
                    1 -> 0.5f
                    else -> 0.2f
                }
                val scale = if (distance == 0) 1f else 0.85f

                Box(
                    modifier = Modifier
                        .height(itemHeightDp)
                        .alpha(alpha)
                        .graphicsLayer { scaleX = scale; scaleY = scale },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = items[index].toString().padStart(2, '0'),
                        fontSize = if (distance == 0) 26.sp else 20.sp,
                        fontWeight = if (distance == 0) FontWeight.Bold else FontWeight.Normal,
                        color = if (distance == 0)
                            MaterialTheme.colorScheme.onSurface
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
                        textAlign = TextAlign.Center
                    )
                }
            }

            item { Box(modifier = Modifier.height(itemHeightDp)) }
        }
    }
}

@Composable
private fun WheelSeparator() {
    Text(
        text = ":",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        modifier = Modifier.width(16.dp),
        textAlign = TextAlign.Center
    )
}
