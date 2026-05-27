package com.krono.app.core.ui.settings

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource

@Composable
fun ToolEmptySettingsPanel(
    icon: ImageVector,
    @StringRes titleRes: Int,
    @StringRes subtitleRes: Int,
    modifier: Modifier = Modifier
) {
    SettingsWatermarkPanel(
        icon = icon,
        title = stringResource(titleRes),
        subtitle = stringResource(subtitleRes),
        modifier = modifier
    )
}
