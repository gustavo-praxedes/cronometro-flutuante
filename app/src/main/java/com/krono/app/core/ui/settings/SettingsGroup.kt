package com.krono.app.core.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.krono.app.core.ui.theme.KronoTokens

@Composable
fun SettingsGroup(
    title: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(
                start = KronoTokens.Spacing.xs,
                bottom = KronoTokens.Spacing.sm
            )
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = KronoTokens.Shape.card,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            tonalElevation = 1.dp
        ) {
            Column(
                modifier = Modifier.padding(KronoTokens.Spacing.none)
            ) {
                content()
            }
        }
    }
}