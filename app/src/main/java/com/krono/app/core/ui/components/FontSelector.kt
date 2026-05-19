package com.krono.app.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.krono.app.R
import com.krono.app.core.ui.theme.KronoFontOption

@Composable
fun FontSelector(
    selectedFont: String,
    onChange: (String) -> Unit,
    leadingIcon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    val current = KronoFontOption.entries.find { it.name == selectedFont } ?: KronoFontOption.SYSTEM_DEFAULT
    KronoDropdown(
        value = current.name,
        onValueChange = onChange,
        options = KronoFontOption.entries.map { it.name },
        label = stringResource(R.string.settings_overlay_font_label),
        leadingIcon = leadingIcon,
        modifier = modifier,
        textMapping = { key -> KronoFontOption.entries.find { it.name == key }?.label ?: key }
    )
}
