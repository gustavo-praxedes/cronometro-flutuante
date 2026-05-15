package com.krono.app.core.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.krono.app.R
import com.krono.app.core.data.TimerDisplayFormat
import com.krono.app.core.ui.components.KronoDropdown
import com.krono.app.core.ui.theme.KronoTokens

@Composable
fun TimeFormatSelector(selected: String, onChange: (String) -> Unit) {
    val current = TimerDisplayFormat.fromKey(selected)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = KronoTokens.Spacing.md, vertical = KronoTokens.Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.sm)
    ) {
        KronoDropdown(
            value = current.key,
            onValueChange = onChange,
            options = TimerDisplayFormat.entries.map { it.key },
            label = stringResource(R.string.settings_time_format_label),
            textMapping = { key -> TimerDisplayFormat.fromKey(key).label }
        )
    }
}

@Composable
fun AppFontSizeSelector(selected: String, onChange: (String) -> Unit) {
    val options = listOf(
        "NORMAL" to stringResource(R.string.settings_font_size_normal),
        "LARGE" to stringResource(R.string.settings_font_size_large)
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = KronoTokens.Spacing.md, vertical = KronoTokens.Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.sm)
    ) {
        KronoDropdown(
            value = selected,
            onValueChange = onChange,
            options = options.map { it.first },
            label = stringResource(R.string.settings_font_size_label),
            textMapping = { key -> options.firstOrNull { it.first == key }?.second ?: key }
        )
    }
}
