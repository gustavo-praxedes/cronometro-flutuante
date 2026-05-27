package com.krono.app.core.ui.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import com.krono.app.core.ui.theme.KronoTokens

@Composable
fun SettingsWatermarkPanel(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(KronoTokens.StateIcon.emptyLarge),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = KronoTokens.Settings.emptyStateIconAlpha)
            )
            Spacer(modifier = Modifier.height(KronoTokens.Spacing.lg))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = KronoTokens.Typography.bodyText
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = KronoTokens.Settings.emptyStateTextAlpha),
                textAlign = TextAlign.Center
            )
            if (!subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(KronoTokens.Spacing.xs))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = KronoTokens.Settings.emptyStateTextAlpha),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
