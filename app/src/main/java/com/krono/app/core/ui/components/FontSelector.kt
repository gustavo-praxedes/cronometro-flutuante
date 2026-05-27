package com.krono.app.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.krono.app.R
import com.krono.app.core.ui.theme.KronoFontCatalog
import com.krono.app.core.ui.theme.KronoFontOption

@Composable
fun FontSelector(
    selectedFont: String,
    availableGoogleFonts: Set<String>,
    onChange: (String) -> Unit,
    leadingIcon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    val availableOptions = remember(availableGoogleFonts) { KronoFontCatalog.overlayOptions(availableGoogleFonts) }
    val current = availableOptions.find { it.name == selectedFont } ?: KronoFontOption.CHIVO_MONO
    KronoDropdown(
        value = current.name,
        onValueChange = onChange,
        options = availableOptions.map { it.name },
        label = stringResource(R.string.settings_overlay_font_label),
        leadingIcon = leadingIcon,
        modifier = modifier,
        textMapping = { key -> availableOptions.find { it.name == key }?.label ?: key }
    )
}

@Composable
fun AppFontSelector(
    selectedFont: String,
    availableGoogleFonts: Set<String>,
    onChange: (String) -> Unit,
    leadingIcon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    val availableOptions = remember(availableGoogleFonts) { KronoFontCatalog.appOptions(availableGoogleFonts) }
    val current = availableOptions.find { it.name == selectedFont } ?: KronoFontOption.CHIVO
    KronoDropdown(
        value = current.name,
        onValueChange = onChange,
        options = availableOptions.map { it.name },
        label = stringResource(R.string.settings_app_font_label),
        leadingIcon = leadingIcon,
        modifier = modifier,
        textMapping = { key -> availableOptions.find { it.name == key }?.label ?: key }
    )
}
