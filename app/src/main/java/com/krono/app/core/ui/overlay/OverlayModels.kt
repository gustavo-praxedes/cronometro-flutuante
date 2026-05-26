package com.krono.app.core.ui.overlay

import androidx.compose.ui.graphics.vector.ImageVector

data class OverlayButton(
    val icon: ImageVector,
    val description: String,
    val isActive: Boolean = false,
    val enabled: Boolean = true,
    val visualAlpha: Float = 1f,
    val onClick: () -> Unit
)

data class OverlayQuickOption(
    val icon: ImageVector,
    val description: String,
    val isActive: Boolean,
    val onClick: () -> Unit
)
