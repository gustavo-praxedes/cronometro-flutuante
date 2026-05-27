package com.krono.app.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Typography

/**
 * KronoTheme: Componente principal de tema do aplicativo.
 * Gerencia a alternância entre os 10 esquemas de cores disponíveis.
 */
@Composable
fun KronoTheme(
    selectedTheme: String,
    selectedFont: String = "CHIVO",
    appFontSize: String = "NORMAL",
    content: @Composable () -> Unit
) {
    val systemIsDark = isSystemInDarkTheme()

    val colorScheme = when (KronoThemeOption.entries.find { it.name == selectedTheme } ?: KronoThemeOption.AUTO) {
        KronoThemeOption.DARK_MODERN      -> DarkModernColors
        KronoThemeOption.SOLARIZED_DARK   -> SolarizedDarkColors
        KronoThemeOption.MIDNIGHT_SLATE   -> MidnightSlateColors
        KronoThemeOption.RUBY_NIGHT       -> RubyNightColors
        KronoThemeOption.FOREST_DEEP      -> ForestDeepColors
        KronoThemeOption.LIGHT_MODERN     -> LightModernColors
        KronoThemeOption.SOLARIZED_LIGHT  -> SolarizedLightColors
        KronoThemeOption.PORCELAIN        -> PorcelainColors
        KronoThemeOption.AMBER_GLOW       -> AmberGlowColors
        KronoThemeOption.ARCTIC           -> ArcticColors
        KronoThemeOption.AUTO             -> if (systemIsDark) DarkModernColors else LightModernColors
    }

    val fontScale = when (appFontSize) {
        "LARGE" -> 1.15f
        else -> 1f
    }
    val scaledTypography = appTypography(selectedFont).scaled(fontScale)

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = scaledTypography,
        content     = content
    )
}

private fun Typography.scaled(scale: Float): Typography = copy(
    bodyLarge = bodyLarge.copy(fontSize = bodyLarge.fontSize * scale, lineHeight = bodyLarge.lineHeight * scale),
    bodyMedium = bodyMedium.copy(fontSize = bodyMedium.fontSize * scale, lineHeight = bodyMedium.lineHeight * scale),
    bodySmall = bodySmall.copy(fontSize = bodySmall.fontSize * scale, lineHeight = bodySmall.lineHeight * scale),
    titleLarge = titleLarge.copy(fontSize = titleLarge.fontSize * scale, lineHeight = titleLarge.lineHeight * scale),
    titleMedium = titleMedium.copy(fontSize = titleMedium.fontSize * scale, lineHeight = titleMedium.lineHeight * scale),
    titleSmall = titleSmall.copy(fontSize = titleSmall.fontSize * scale, lineHeight = titleSmall.lineHeight * scale),
    labelLarge = labelLarge.copy(fontSize = labelLarge.fontSize * scale, lineHeight = labelLarge.lineHeight * scale),
    labelMedium = labelMedium.copy(fontSize = labelMedium.fontSize * scale, lineHeight = labelMedium.lineHeight * scale),
    labelSmall = labelSmall.copy(fontSize = labelSmall.fontSize * scale, lineHeight = labelSmall.lineHeight * scale)
)

/**
 * Modificador para tornar o diálogo adaptativo: usa uma fração da tela,
 * mas limita a uma largura máxima em telas grandes (tablets/foldables).
 */
@Composable
fun Modifier.adaptiveDialogWidth(): Modifier = this
    .fillMaxWidth(KronoTokens.Spacing.dialogWidthFrac)
    .widthIn(max = KronoTokens.Spacing.maxDialogWidth)

