package com.krono.app.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font as ResourceFont
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
private val azeretMonoFont    = GoogleFont("Azeret Mono")

val JetBrainsMono = FontFamily(Font(googleFont = jetbrainsMonoFont, fontProvider = provider, weight = FontWeight.Normal))
val FiraCode      = FontFamily(Font(googleFont = firaCodeFont,      fontProvider = provider, weight = FontWeight.Normal))
val AnonymousPro  = FontFamily(Font(googleFont = anonymousProFont,  fontProvider = provider, weight = FontWeight.Normal))
val RobotoMono    = FontFamily(Font(googleFont = robotoMonoFont,    fontProvider = provider, weight = FontWeight.Normal))
val SpaceMono     = FontFamily(Font(googleFont = spaceMonoFont,     fontProvider = provider, weight = FontWeight.Normal))
val ChivoMono     = FontFamily(ResourceFont(R.font.chivo_mono_regular, weight = FontWeight.Normal))
val AzeretMono    = FontFamily(Font(googleFont = azeretMonoFont,    fontProvider = provider, weight = FontWeight.Normal))

// ── Tipografia Padrão ────────────────────────────────────────
private val BaseTypography = Typography()

private fun TextStyle.withChivoMono(): TextStyle = copy(
    fontFamily = ChivoMono,
    letterSpacing = 0.sp
)

val AppTypography = BaseTypography.copy(
    displayLarge = BaseTypography.displayLarge.withChivoMono(),
    displayMedium = BaseTypography.displayMedium.withChivoMono(),
    displaySmall = BaseTypography.displaySmall.withChivoMono(),
    headlineLarge = BaseTypography.headlineLarge.withChivoMono(),
    headlineMedium = BaseTypography.headlineMedium.withChivoMono(),
    headlineSmall = BaseTypography.headlineSmall.withChivoMono(),
    titleLarge = BaseTypography.titleLarge.withChivoMono(),
    titleMedium = BaseTypography.titleMedium.withChivoMono(),
    titleSmall = BaseTypography.titleSmall.withChivoMono(),
    bodyLarge = BaseTypography.bodyLarge.copy(
        fontFamily = ChivoMono,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
    ),
    bodyMedium = BaseTypography.bodyMedium.withChivoMono(),
    bodySmall = BaseTypography.bodySmall.withChivoMono(),
    labelLarge = BaseTypography.labelLarge.withChivoMono(),
    labelMedium = BaseTypography.labelMedium.withChivoMono(),
    labelSmall = BaseTypography.labelSmall.withChivoMono()
)

/**
 * Retorna a FontFamily baseada na opção selecionada.
 * Agora as fontes são carregadas dinamicamente do Google Fonts.
 */
fun timerFontFamily(selectedFont: String): FontFamily {
    return when (KronoFontOption.entries.find { it.name == selectedFont } ?: KronoFontOption.CHIVO_MONO) {
        KronoFontOption.JETBRAINS_MONO -> JetBrainsMono
        KronoFontOption.FIRA_CODE      -> FiraCode
        KronoFontOption.ANONYMOUS_PRO  -> AnonymousPro
        KronoFontOption.ROBOTO_MONO    -> RobotoMono
        KronoFontOption.COMMIT_MONO    -> SpaceMono
        KronoFontOption.CHIVO_MONO     -> ChivoMono
        KronoFontOption.AZERET_MONO    -> AzeretMono
        else -> ChivoMono
    }
}


