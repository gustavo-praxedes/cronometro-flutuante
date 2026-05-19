package com.krono.app.core.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.krono.app.core.ui.theme.KronoTokens

@Composable
fun SettingsDivider(
    withIconInset: Boolean = true,
    modifier: Modifier = Modifier
) {
    val inset = if (withIconInset) {
        KronoTokens.Settings.panelHorizontalInset + KronoTokens.Size.iconBox + KronoTokens.Spacing.rowInner
    } else {
        KronoTokens.Settings.panelHorizontalInset
    }

    HorizontalDivider(
        modifier = modifier.padding(start = inset, end = KronoTokens.Settings.panelHorizontalInset),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = KronoTokens.Settings.dividerAlpha),
        thickness = KronoTokens.Settings.dividerThickness
    )
}

