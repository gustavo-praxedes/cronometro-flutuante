package com.krono.app.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.krono.app.R
import com.krono.app.core.ui.theme.KronoThemeOption
import com.krono.app.core.ui.theme.KronoTokens

@Composable
fun ThemeSelector(
    selectedTheme: String,
    onChange: (String) -> Unit
) {
    val current = KronoThemeOption.entries.find { it.name == selectedTheme } ?: KronoThemeOption.AUTO
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = KronoTokens.Spacing.md, vertical = KronoTokens.Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.sm)
    ) {
        KronoDropdown(
            value = current.name,
            onValueChange = onChange,
            options = KronoThemeOption.entries.map { it.name },
            label = stringResource(R.string.label_theme),
            textMapping = { key -> KronoThemeOption.entries.find { it.name == key }?.label ?: key }
        )
    }
}
