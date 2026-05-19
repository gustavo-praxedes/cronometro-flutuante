package com.krono.app.core.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun ToggleRow(
    label: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit,
    subtitle: String? = null,
    leadingIcon: ImageVector? = null,
    leadingTextIcon: String? = null,
    modifier: Modifier = Modifier
) {
    SettingsRow(
        title = label,
        subtitle = subtitle,
        leadingIcon = leadingIcon,
        leadingTextIcon = leadingTextIcon,
        modifier = modifier,
        onClick = { onChange(!checked) },
        trailing = {
            KronoToggle(
                checked = checked,
                onCheckedChange = onChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    uncheckedThumbColor = MaterialTheme.colorScheme.surface,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    )
}
