package com.krono.app.core.ui.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.krono.app.core.ui.theme.KronoTokens
import kotlinx.coroutines.delay

@Composable
fun OverlayExpandableMenu(
    primaryButtons: List<OverlayButton>,
    quickOptions: List<OverlayQuickOption>,
    textColor: Color,
    dimensions: OverlayDimensions,
    rowWidth: Dp,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    if (primaryButtons.isEmpty() && quickOptions.isEmpty()) return

    var expanded by remember { mutableStateOf(false) }
    var interactionTick by remember { mutableIntStateOf(0) }

    LaunchedEffect(expanded) {
        onExpandedChange(expanded)
    }

    LaunchedEffect(expanded, interactionTick) {
        if (expanded) {
            delay(KronoTokens.Overlay.menuTimeoutMs)
            expanded = false
        }
    }

    fun resetTimer() {
        interactionTick++
    }

    Column(
        modifier = modifier.width(rowWidth),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = expanded,
            modifier = Modifier.width(rowWidth),
            enter = expandVertically(
                expandFrom = Alignment.Top,
                animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessLow)
            ) + fadeIn(),
            exit = shrinkVertically(
                shrinkTowards = Alignment.Top,
                animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMedium)
            ) + fadeOut()
        ) {
            Column(
                modifier = Modifier.width(rowWidth),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HorizontalDivider(
                    modifier = Modifier
                        .width(rowWidth)
                        .padding(top = dimensions.menuPaddingV),
                    thickness = KronoTokens.Settings.dividerThickness,
                    color = textColor.copy(alpha = KronoTokens.Overlay.dividerAlpha)
                )
                if (primaryButtons.isNotEmpty()) {
                    OverlayButtonRow(
                        buttons = primaryButtons,
                        textColor = textColor,
                        dimensions = dimensions,
                        rowWidth = rowWidth,
                        topPadding = dimensions.menuDividerButtonGap,
                        bottomPadding = if (quickOptions.isEmpty()) 0.dp else dimensions.menuDividerButtonGap,
                        onInteraction = ::resetTimer
                    )
                }
                if (quickOptions.isNotEmpty()) {
                    OverlayQuickOptionRow(
                        options = quickOptions,
                        textColor = textColor,
                        dimensions = dimensions,
                        rowWidth = rowWidth,
                        topPadding = if (primaryButtons.isEmpty()) dimensions.menuDividerButtonGap else 0.dp,
                        bottomPadding = 0.dp,
                        onInteraction = ::resetTimer
                    )
                }
            }
        }

        Box(modifier = Modifier.height(dimensions.handleTopGap))

        Box(
            modifier = Modifier
                .height(dimensions.handleTouchHeight)
                .width(rowWidth)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            expanded = !expanded
                            if (expanded) resetTimer()
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(width = dimensions.handlePillWidth, height = dimensions.handlePillHeight)
                    .background(
                        color = textColor.copy(alpha = KronoTokens.Alpha.disabled),
                        shape = RoundedCornerShape(percent = 50)
                    )
            )
        }

        Box(modifier = Modifier.height(dimensions.handleBottomGap))
    }
}

@Composable
private fun OverlayButtonRow(
    buttons: List<OverlayButton>,
    textColor: Color,
    dimensions: OverlayDimensions,
    rowWidth: Dp,
    topPadding: Dp,
    bottomPadding: Dp,
    onInteraction: () -> Unit
) {
    val buttonWidth = adaptiveOverlayButtonWidth(
        rowWidth = rowWidth,
        buttonCount = buttons.size,
        minButtonSize = dimensions.btnVisualSize,
        gap = dimensions.controlGap
    )
    Row(
        modifier = Modifier
            .width(rowWidth)
            .padding(top = topPadding, bottom = bottomPadding),
        horizontalArrangement = Arrangement.spacedBy(dimensions.controlGap),
        verticalAlignment = Alignment.CenterVertically
    ) {
        buttons.forEach { button ->
            OverlayIconButton(
                icon = button.icon,
                contentDescription = button.description,
                tint = if (button.isActive) MaterialTheme.colorScheme.primary else textColor,
                onClick = {
                    onInteraction()
                    button.onClick()
                },
                enabled = button.enabled,
                visualAlpha = button.visualAlpha,
                touchWidth = buttonWidth,
                touchHeight = dimensions.btnTouchSize,
                visualWidth = buttonWidth,
                visualHeight = dimensions.btnVisualSize,
                iconSize = dimensions.iconSize,
                isActive = button.isActive
            )
        }
    }
}

@Composable
private fun OverlayQuickOptionRow(
    options: List<OverlayQuickOption>,
    textColor: Color,
    dimensions: OverlayDimensions,
    rowWidth: Dp,
    topPadding: Dp,
    bottomPadding: Dp,
    onInteraction: () -> Unit
) {
    val buttonWidth = adaptiveOverlayButtonWidth(
        rowWidth = rowWidth,
        buttonCount = options.size,
        minButtonSize = dimensions.quickVisualSize,
        gap = dimensions.controlGap
    )
    Row(
        modifier = Modifier
            .width(rowWidth)
            .padding(top = topPadding, bottom = bottomPadding),
        horizontalArrangement = Arrangement.spacedBy(dimensions.controlGap),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEach { option ->
            OverlayIconButton(
                icon = option.icon,
                contentDescription = option.description,
                tint = if (option.isActive) {
                    MaterialTheme.colorScheme.primary
                } else {
                    textColor.copy(alpha = KronoTokens.Alpha.disabled)
                },
                onClick = {
                    onInteraction()
                    option.onClick()
                },
                enabled = true,
                visualAlpha = 1f,
                touchWidth = buttonWidth,
                touchHeight = dimensions.quickTouchSize,
                visualWidth = buttonWidth,
                visualHeight = dimensions.quickVisualSize,
                iconSize = dimensions.quickIconSize,
                isActive = option.isActive
            )
        }
    }
}
