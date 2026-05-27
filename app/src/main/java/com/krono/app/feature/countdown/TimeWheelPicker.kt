package com.krono.app.feature.countdown

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krono.app.core.ui.theme.KronoTokens
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun TimeWheelPicker(
    totalSeconds: Long,
    onValueChange: (Long) -> Unit,
    modifier: Modifier = Modifier,
    fadeColor: Color = MaterialTheme.colorScheme.background
) {
    val initH = (totalSeconds / 3600).toInt().coerceIn(0, 99)
    val initM = ((totalSeconds % 3600) / 60).toInt()
    val initS = (totalSeconds % 60).toInt()

    val h = remember { androidx.compose.runtime.mutableIntStateOf(initH) }
    val m = remember { androidx.compose.runtime.mutableIntStateOf(initM) }
    val s = remember { androidx.compose.runtime.mutableIntStateOf(initS) }

    fun emit() {
        onValueChange(h.intValue * 3600L + m.intValue * 60L + s.intValue)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = KronoTokens.Spacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = KronoTokens.Spacing.sm),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.width(KronoTokens.Wheel.columnWidth), contentAlignment = Alignment.Center) { WheelLabel("HORAS") }
            Spacer(Modifier.width(KronoTokens.Wheel.separatorWidth))
            Box(Modifier.width(KronoTokens.Wheel.columnWidth), contentAlignment = Alignment.Center) { WheelLabel("MINUTOS") }
            Spacer(Modifier.width(KronoTokens.Wheel.separatorWidth))
            Box(Modifier.width(KronoTokens.Wheel.columnWidth), contentAlignment = Alignment.Center) { WheelLabel("SEGUNDOS") }
        }

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                WheelColumn(range = 0..99, initial = initH, fadeColor = fadeColor, onSelected = { h.intValue = it; emit() })
                Separator()
                WheelColumn(range = 0..59, initial = initM, fadeColor = fadeColor, onSelected = { m.intValue = it; emit() })
                Separator()
                WheelColumn(range = 0..59, initial = initS, fadeColor = fadeColor, onSelected = { s.intValue = it; emit() })
            }
        }
    }
}

@Composable
private fun WheelLabel(text: String) {
    Text(
        text = text,
        fontSize = KronoTokens.Wheel.labelFontSize,
        fontWeight = FontWeight.Normal,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = KronoTokens.Spacing.xs),
        maxLines = 1,
        softWrap = false
    )
}

@Composable
private fun WheelColumn(
    range: IntRange,
    initial: Int,
    onSelected: (Int) -> Unit,
    fadeColor: Color,
    itemH: Dp = KronoTokens.Wheel.itemHeight,
    visible: Int = 3
) {
    val items = range.toList()
    val initIndex = (initial - range.first).coerceIn(0, items.lastIndex)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initIndex)
    val snapBehavior = rememberSnapFlingBehavior(listState)

    val centredIndex by remember {
        derivedStateOf {
            val info = listState.layoutInfo.visibleItemsInfo
            val viewportCenter = listState.layoutInfo.viewportStartOffset +
                    listState.layoutInfo.viewportSize.height / 2
            info.minByOrNull { kotlin.math.abs(it.offset + it.size / 2 - viewportCenter) }
                ?.index
                ?.minus(1)
                ?.coerceIn(0, items.lastIndex)
                ?: initIndex
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { centredIndex }
            .distinctUntilChanged()
            .collect { idx -> onSelected(items[idx]) }
    }

    val containerH = itemH * visible

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.width(KronoTokens.Wheel.columnWidth)
    ) {
        LazyColumn(
            state = listState,
            flingBehavior = snapBehavior,
            modifier = Modifier
                .width(KronoTokens.Wheel.columnInnerWidth)
                .height(containerH),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { Box(Modifier.height(itemH)) }

            items(items.size) { index ->
                val dist = kotlin.math.abs(index - centredIndex)
                val alpha = when (dist) { 
                    0 -> KronoTokens.Wheel.alphaSelected 
                    1 -> KronoTokens.Wheel.alphaMedium 
                    else -> KronoTokens.Wheel.alphaSmall 
                }
                val scale = when (dist) { 
                    0 -> KronoTokens.Wheel.scaleSelected 
                    1 -> KronoTokens.Wheel.scaleMedium 
                    else -> KronoTokens.Wheel.scaleSmall 
                }
                
                Box(
                    modifier = Modifier
                        .height(itemH)
                        .graphicsLayer {
                            this.alpha = alpha
                            this.scaleX = scale
                            this.scaleY = scale
                            this.rotationX = if (dist != 0) (index - centredIndex) * KronoTokens.Wheel.rotationFactor else 0f
                            this.cameraDistance = KronoTokens.Wheel.cameraDistance * density
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = items[index].toString().padStart(2, '0'),
                        fontSize = KronoTokens.Wheel.selectedFontSize,
                        fontWeight = if (dist == 0) FontWeight.Normal else FontWeight.Normal,
                        color = if (dist == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                }
            }

            item { Box(Modifier.height(itemH)) }
        }
        Box(
            modifier = Modifier
                .width(KronoTokens.Wheel.columnInnerWidth)
                .height(containerH)
                .background(
                    Brush.verticalGradient(
                        0f to fadeColor,
                        0.34f to Color.Transparent,
                        0.66f to Color.Transparent,
                        1f to fadeColor
                    )
                )
        )
    }
}

@Composable
private fun Separator() {
    Text(
        ":",
        fontSize = KronoTokens.Wheel.separatorFontSize,
        fontWeight = FontWeight.Normal,
        color = MaterialTheme.colorScheme.primary.copy(alpha = KronoTokens.Alpha.separator),
        modifier = Modifier.width(KronoTokens.Wheel.separatorWidth),
        textAlign = TextAlign.Center
    )
}
