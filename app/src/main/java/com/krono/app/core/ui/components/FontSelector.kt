package com.krono.app.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.krono.app.R
import com.krono.app.core.ui.theme.KronoFontOption
import com.krono.app.core.ui.theme.KronoTokens

@Composable
fun FontSelector(
    selectedFont: String,
    onChange: (String) -> Unit
) {
    val current = KronoFontOption.entries.find { it.name == selectedFont } ?: KronoFontOption.SYSTEM_DEFAULT
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = KronoTokens.Spacing.md, vertical = KronoTokens.Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.sm)
    ) {
        KronoDropdown(
            value = current.name,
            onValueChange = onChange,
            options = KronoFontOption.entries.map { it.name },
            label = stringResource(R.string.settings_font_label),
            textMapping = { key -> KronoFontOption.entries.find { it.name == key }?.label ?: key }
        )
    }
}
