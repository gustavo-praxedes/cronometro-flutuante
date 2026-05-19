package com.krono.app.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.krono.app.R
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoTokens

@Composable
fun KronoControlButtons(
    isRunning: Boolean,
    isAtLimit: Boolean,
    scale: Float,
    currentScale: Float,
    textColor: Color,
    onStartPause: () -> Unit,
    onReset: () -> Unit,
    onSettings: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconSizeDp = (KronoTokens.Overlay.iconSize.value * scale * currentScale).dp
    val btnSize = (KronoTokens.Overlay.buttonSize.value * scale * currentScale).dp
    val btnTopPadding = (KronoTokens.Overlay.btnTopPadding.value * scale * currentScale).dp

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = btnTopPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedIconButton(
            onClick = onStartPause,
            enabled = !isAtLimit,
            modifier = Modifier.size(btnSize)
        ) {
            Icon(
                imageVector = if (isRunning) KronoIcons.Action.Pause else KronoIcons.Action.Play,
                contentDescription = if (isRunning) stringResource(R.string.action_pause) else stringResource(R.string.action_play),
                tint = if (isRunning) MaterialTheme.colorScheme.primary else textColor,
                modifier = Modifier.size(iconSizeDp)
            )
        }

        AnimatedIconButton(
            onClick = onReset,
            modifier = Modifier.size(btnSize)
        ) {
            Icon(KronoIcons.Action.StopFilled, stringResource(R.string.action_reset), tint = textColor, modifier = Modifier.size(iconSizeDp))
        }

        AnimatedIconButton(
            onClick = onSettings,
            modifier = Modifier.size(btnSize)
        ) {
            Icon(KronoIcons.Navigation.Menu, stringResource(R.string.settings_title), tint = textColor, modifier = Modifier.size(iconSizeDp))
        }

        AnimatedIconButton(
            onClick = onClose,
            modifier = Modifier.size(btnSize)
        ) {
            Icon(KronoIcons.Navigation.Close, stringResource(R.string.action_close), tint = textColor, modifier = Modifier.size(iconSizeDp))
        }
    }
}

