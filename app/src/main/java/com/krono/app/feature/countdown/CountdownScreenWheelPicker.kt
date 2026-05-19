package com.krono.app.feature.countdown

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun CountdownScreenWheelPicker(
    totalSeconds: Long,
    numberFontSize: TextUnit,
    fontFamily: FontFamily,
    onValueChange: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val itemHeight = 100.dp
    val visibleItems = 3
    val wheelHeight = itemHeight * visibleItems
    val groupWidth = 120.dp
    val colonWidth = 18.dp

    val initH = (totalSeconds / 3600L).toInt().coerceIn(0, 99)
    val initM = ((totalSeconds % 3600L) / 60L).toInt().coerceIn(0, 59)
    val initS = (totalSeconds % 60L).toInt().coerceIn(0, 59)

    val h = remember { androidx.compose.runtime.mutableIntStateOf(initH) }
    val m = remember { androidx.compose.runtime.mutableIntStateOf(initM) }
    val s = remember { androidx.compose.runtime.mutableIntStateOf(initS) }
    val latestOnValueChange by rememberUpdatedState(onValueChange)

    fun emit() = latestOnValueChange(h.intValue * 3600L + m.intValue * 60L + s.intValue)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(wheelHeight),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        InfiniteWheelColumn(
            rangeStart = 0,
            rangeSize = 100,
            initial = initH,
            columnWidth = groupWidth,
            itemHeight = itemHeight,
            numberFontSize = numberFontSize,
            fontFamily = fontFamily,
            onSelected = { h.intValue = it; emit() }
        )
        ColonAnchor(
            itemHeight = itemHeight,
            numberFontSize = numberFontSize,
            fontFamily = fontFamily,
            width = colonWidth
        )
        InfiniteWheelColumn(
            rangeStart = 0,
            rangeSize = 60,
            initial = initM,
            columnWidth = groupWidth,
            itemHeight = itemHeight,
            numberFontSize = numberFontSize,
            fontFamily = fontFamily,
            onSelected = { m.intValue = it; emit() }
        )
        ColonAnchor(
            itemHeight = itemHeight,
            numberFontSize = numberFontSize,
            fontFamily = fontFamily,
            width = colonWidth
        )
        InfiniteWheelColumn(
            rangeStart = 0,
            rangeSize = 60,
            initial = initS,
            columnWidth = groupWidth,
            itemHeight = itemHeight,
            numberFontSize = numberFontSize,
            fontFamily = fontFamily,
            onSelected = { s.intValue = it; emit() }
        )
    }
}

@Composable
private fun InfiniteWheelColumn(
    rangeStart: Int,
    rangeSize: Int,
    initial: Int,
    columnWidth: androidx.compose.ui.unit.Dp,
    itemHeight: androidx.compose.ui.unit.Dp,
    numberFontSize: TextUnit,
    fontFamily: FontFamily,
    onSelected: (Int) -> Unit
) {
    val wheelItemCount = 20_000
    val visibleItems = 3
    val middle = wheelItemCount / 2
    val anchor = middle - (middle % rangeSize)
    val initialIndex = (anchor + (initial - rangeStart).coerceIn(0, rangeSize - 1)).coerceIn(0, wheelItemCount - 1)

    // Com 1 item de padding no topo, o item selecionado (linha central) fica no índice
    // equivalente ao firstVisibleItemIndex (em domínio raw).
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex.coerceAtLeast(0))
    val snap = rememberSnapFlingBehavior(listState)

    val centeredIndex by remember {
        // Com o item de padding no topo, o valor central coincide com o firstVisibleItemIndex.
        derivedStateOf { listState.firstVisibleItemIndex.coerceIn(0, wheelItemCount - 1) }
    }

    LaunchedEffect(listState) {
        snapshotFlow { centeredIndex }
            .distinctUntilChanged()
            .collect { idx ->
                val value = rangeStart + ((idx % rangeSize + rangeSize) % rangeSize)
                onSelected(value)
            }
    }

    LazyColumn(
        state = listState,
        flingBehavior = snap,
        modifier = Modifier
            .width(columnWidth)
            .height(itemHeight * visibleItems),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Padding visual superior para centralizar o item selecionado
        item { Box(modifier = Modifier.height(itemHeight)) }

        items(wheelItemCount) { rawIndex ->
            val value = rangeStart + ((rawIndex % rangeSize + rangeSize) % rangeSize)
            val dist = kotlin.math.abs(rawIndex - centeredIndex)
            val alpha = when (dist) {
                0 -> 1f
                1 -> 0.54f
                else -> 0.24f
            }
            val size = when (dist) {
                0 -> numberFontSize
                1 -> numberFontSize * 0.9f
                else -> numberFontSize * 0.82f
            }
            Box(
                modifier = Modifier
                    .height(itemHeight)
                    .width(columnWidth),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = value.toString().padStart(2, '0'),
                    fontSize = size,
                    fontFamily = fontFamily,
                    letterSpacing = 0.sp,
                    style = TextStyle(
                        lineHeight = size * 1.2f,
                        platformStyle = PlatformTextStyle(includeFontPadding = false)
                    ),
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer { this.alpha = alpha }
                )
            }
        }

        // Padding visual inferior para manter centralização no fim do viewport
        item { Box(modifier = Modifier.height(itemHeight)) }
    }
}

@Composable
private fun ColonAnchor(
    itemHeight: androidx.compose.ui.unit.Dp,
    numberFontSize: TextUnit,
    fontFamily: FontFamily,
    width: androidx.compose.ui.unit.Dp
) {
    Box(
        modifier = Modifier
            .height(itemHeight * 3)
            .width(width),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = ":",
            fontSize = numberFontSize,
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.sp,
            style = TextStyle(
                lineHeight = numberFontSize * 1.2f,
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            ),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
    }
}


