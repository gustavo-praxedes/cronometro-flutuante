package com.krono.app.core.ui.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.krono.app.R
import com.krono.app.core.ui.theme.KronoTokens

@Composable
fun ToolEmptySettingsPanel(
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    SettingsPanelLayout(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = KronoTokens.Spacing.xxxl),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.padding(horizontal = KronoTokens.Settings.panelHorizontalInset),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(KronoTokens.StateIcon.emptyLarge),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = KronoTokens.Alpha.disabled)
                )
                Spacer(Modifier.height(KronoTokens.Spacing.md))
                Text(
                    text = stringResource(R.string.tool_settings_empty_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(KronoTokens.Spacing.xs))
                Text(
                    text = stringResource(R.string.tool_settings_empty_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
