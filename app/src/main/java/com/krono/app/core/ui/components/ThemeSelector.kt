package com.krono.app.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.krono.app.R
import com.krono.app.core.ui.theme.KronoThemeOption

@Composable
fun ThemeSelector(
    selectedTheme: String,
    onChange: (String) -> Unit,
    leadingIcon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    val current = KronoThemeOption.entries.find { it.name == selectedTheme } ?: KronoThemeOption.AUTO
    KronoDropdown(
        value = current.name,
        onValueChange = onChange,
        options = KronoThemeOption.entries.map { it.name },
        label = stringResource(R.string.label_theme),
        leadingIcon = leadingIcon,
        modifier = modifier,
        textMapping = { key -> KronoThemeOption.entries.find { it.name == key }?.label ?: key }
    )
}
