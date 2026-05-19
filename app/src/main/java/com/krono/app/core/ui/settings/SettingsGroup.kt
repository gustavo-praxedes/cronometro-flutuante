package com.krono.app.core.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.border
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krono.app.core.ui.theme.KronoTokens

@Composable
fun SettingsGroup(
    title: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = KronoTokens.Typography.statusLabel,
                letterSpacing = 1.2.sp
            ),
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(
                top = KronoTokens.Settings.groupTitleTop,
                bottom = KronoTokens.Settings.groupTitleBottom
            )
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = KronoTokens.Shape.card,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp
        ) {
            Column(
                modifier = Modifier
                    .border(
                        width = KronoTokens.Settings.dividerThickness,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = KronoTokens.Settings.dividerAlpha),
                        shape = KronoTokens.Shape.card
                    )
                    .padding(KronoTokens.Spacing.none)
            ) {
                content()
            }
        }
    }
}

