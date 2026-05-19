package com.krono.app.core.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.krono.app.R
import com.krono.app.core.ui.components.KronoDropdown

@Composable
fun AppFontSizeSelector(
    selected: String,
    onChange: (String) -> Unit,
    leadingIcon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    val options = listOf(
        "NORMAL" to stringResource(R.string.settings_font_size_normal),
        "LARGE" to stringResource(R.string.settings_font_size_large)
    )
    KronoDropdown(
        value = selected,
        onValueChange = onChange,
        options = options.map { it.first },
        label = stringResource(R.string.settings_font_size_label),
        leadingIcon = leadingIcon,
        modifier = modifier,
        textMapping = { key -> options.firstOrNull { it.first == key }?.second ?: key }
    )
}
