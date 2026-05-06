package com.krono.app.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * KronoTheme: Componente principal de tema do aplicativo.
 * Gerencia a alternância entre os 10 esquemas de cores disponíveis.
 */
@Composable
fun KronoTheme(
    selectedTheme: String,
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = AppTypography,
        content     = content
    )
}

/**
 * Modificador para tornar o diálogo adaptativo: usa uma fração da tela,
 * mas limita a uma largura máxima em telas grandes (tablets/foldables).
 */
@Composable
fun Modifier.adaptiveDialogWidth(): Modifier = this
    .fillMaxWidth(KronoTokens.Spacing.dialogWidthFrac)
    .widthIn(max = KronoTokens.Spacing.maxDialogWidth)
