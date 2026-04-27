package com.krono.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.krono.app.R

/**
 * Provedor de fontes do Google para baixar fontes automaticamente.
 */
private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

// ── Definição das Fontes via Google Fonts ──────────────────
private val jetbrainsMonoFont = GoogleFont("JetBrains Mono")
private val firaCodeFont      = GoogleFont("Fira Code")
private val anonymousProFont  = GoogleFont("Anonymous Pro")
private val robotoMonoFont    = GoogleFont("Roboto Mono")
private val spaceMonoFont     = GoogleFont("Space Mono")

val JetBrainsMono = FontFamily(Font(googleFont = jetbrainsMonoFont, fontProvider = provider, weight = FontWeight.Bold))
val FiraCode      = FontFamily(Font(googleFont = firaCodeFont,      fontProvider = provider, weight = FontWeight.Bold))
val AnonymousPro  = FontFamily(Font(googleFont = anonymousProFont,  fontProvider = provider, weight = FontWeight.Bold))
val RobotoMono    = FontFamily(Font(googleFont = robotoMonoFont,    fontProvider = provider, weight = FontWeight.Bold))
val SpaceMono     = FontFamily(Font(googleFont = spaceMonoFont,     fontProvider = provider, weight = FontWeight.Bold))

// ── Tipografia Padrão ────────────────────────────────────────
val AppTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    )
)

/**
 * Retorna a FontFamily baseada na opção selecionada.
 * Agora as fontes são carregadas dinamicamente do Google Fonts.
 */
fun timerFontFamily(selectedFont: String): FontFamily {
    return when (KronoFontOption.entries.find { it.name == selectedFont } ?: KronoFontOption.SYSTEM_DEFAULT) {
        KronoFontOption.JETBRAINS_MONO -> JetBrainsMono
        KronoFontOption.FIRA_CODE      -> FiraCode
        KronoFontOption.ANONYMOUS_PRO  -> AnonymousPro
        KronoFontOption.ROBOTO_MONO    -> RobotoMono
        KronoFontOption.COMMIT_MONO    -> SpaceMono
        else -> FontFamily.Monospace
    }
}

/**
 * Estilo base para o display do cronômetro.
 */
val TimerDisplayStyle = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontWeight = FontWeight.Bold,
    fontSize = 40.sp,
    letterSpacing = 0.sp
)
