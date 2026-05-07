package com.krono.app.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.krono.app.core.ui.theme.KronoTokens

@Composable
fun ToggleRow(
    label   : String,
    checked : Boolean,
    onChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier              = modifier
            .fillMaxWidth()
            .clickable { onChange(!checked) }
            .padding(
                horizontal = KronoTokens.Spacing.lg,
                vertical = KronoTokens.Spacing.md
            ),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text     = label,
            style    = MaterialTheme.typography.bodyMedium,
            color    = MaterialTheme.colorScheme.onSurface
        )

        Switch(
            checked = checked,
            onCheckedChange = onChange
        )
    }
}