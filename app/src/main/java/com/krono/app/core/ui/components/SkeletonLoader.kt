package com.krono.app.core.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.krono.app.core.ui.theme.KronoTokens

object SkeletonLoader {

    @Composable
    fun SkeletonText(
        modifier: Modifier = Modifier,
        height: Dp = 16.dp,
        width: Dp = 100.dp
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "skeleton_text")
        val progress by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 1500,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "progress"
        )

        val bgColor = if (isSystemInDarkTheme()) {
            MaterialTheme.colorScheme.surfaceVariant
        } else {
            MaterialTheme.colorScheme.surface
        }

        val shimmerColor = Color.White.copy(alpha = 0.3f)

        val density = LocalDensity.current
        val widthPx = with(density) { width.toPx() }
        val shimmerX = (progress * 2f - 1f) * (widthPx * 3f)

        Box(
            modifier = modifier
                .height(height)
                .width(width)
                .background(bgColor)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(
                    color = shimmerColor,
                    topLeft = Offset(x = shimmerX, y = 0f),
                    size = Size(width = widthPx, height = size.height)
                )
            }
        }
    }

    @Composable
    fun SkeletonButton(
        modifier: Modifier = Modifier,
        height: Dp = KronoTokens.Button.height,
        width: Dp = 100.dp
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "skeleton_button")
        val progress by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 1500,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "progress"
        )

        val bgColor = if (isSystemInDarkTheme()) {
            MaterialTheme.colorScheme.surfaceVariant
        } else {
            MaterialTheme.colorScheme.surface
        }

        val shimmerColor = Color.White.copy(alpha = 0.3f)

        val density = LocalDensity.current
        val widthPx = with(density) { width.toPx() }
        val shimmerX = (progress * 2f - 1f) * (widthPx * 3f)

        Box(
            modifier = modifier
                .height(height)
                .width(width)
                .clip(KronoTokens.Shape.button)
                .background(bgColor)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(
                    color = shimmerColor,
                    topLeft = Offset(x = shimmerX, y = 0f),
                    size = Size(width = widthPx, height = size.height)
                )
            }
        }
    }

    @Composable
    fun SkeletonContainer(
        modifier: Modifier = Modifier,
        height: Dp = 100.dp,
        cornerRadius: Dp = 16.dp
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "skeleton_container")
        val progress by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 1500,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "progress"
        )

        val bgColor = if (isSystemInDarkTheme()) {
            MaterialTheme.colorScheme.surfaceVariant
        } else {
            MaterialTheme.colorScheme.surface
        }

        val shimmerColor = Color.White.copy(alpha = 0.3f)

        Box(
            modifier = modifier
                .height(height)
                .clip(RoundedCornerShape(cornerRadius))
                .background(bgColor)
        ) {
            Canvas(modifier = Modifier.fillMaxWidth().height(height)) {
                drawRect(
                    color = shimmerColor,
                    topLeft = Offset(x = progress * 2f * size.width - size.width, y = 0f),
                    size = Size(width = size.width, height = size.height)
                )
            }
        }
    }
}