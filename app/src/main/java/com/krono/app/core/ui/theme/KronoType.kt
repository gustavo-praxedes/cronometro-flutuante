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
val Chivo         = FontFamily(
    ResourceFont(R.font.chivo_light, weight = FontWeight.Light),
    ResourceFont(R.font.chivo_regular, weight = FontWeight.Normal)
)
val ChivoLight    = FontFamily(
    ResourceFont(R.font.chivo_light, weight = FontWeight.Normal)
)
val AzeretMono    = FontFamily(Font(googleFont = azeretMonoFont,    fontProvider = provider, weight = FontWeight.Normal))

// ── Tipografia Padrão ────────────────────────────────────────
private val BaseTypography = Typography()

private fun TextStyle.withAppFont(fontFamily: FontFamily): TextStyle = copy(
    fontFamily = fontFamily,
    letterSpacing = 0.sp
)

fun appTypography(selectedFont: String): Typography {
    val fontFamily = appFontFamily(selectedFont)
    return BaseTypography.copy(
        displayLarge = BaseTypography.displayLarge.withAppFont(fontFamily),
        displayMedium = BaseTypography.displayMedium.withAppFont(fontFamily),
        displaySmall = BaseTypography.displaySmall.withAppFont(fontFamily),
        headlineLarge = BaseTypography.headlineLarge.withAppFont(fontFamily),
        headlineMedium = BaseTypography.headlineMedium.withAppFont(fontFamily),
        headlineSmall = BaseTypography.headlineSmall.withAppFont(fontFamily),
        titleLarge = BaseTypography.titleLarge.withAppFont(fontFamily),
        titleMedium = BaseTypography.titleMedium.withAppFont(fontFamily),
        titleSmall = BaseTypography.titleSmall.withAppFont(fontFamily),
        bodyLarge = BaseTypography.bodyLarge.copy(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.sp,
        ),
        bodyMedium = BaseTypography.bodyMedium.withAppFont(fontFamily),
        bodySmall = BaseTypography.bodySmall.withAppFont(fontFamily),
        labelLarge = BaseTypography.labelLarge.withAppFont(fontFamily),
        labelMedium = BaseTypography.labelMedium.withAppFont(fontFamily),
        labelSmall = BaseTypography.labelSmall.withAppFont(fontFamily)
    )
}

fun appFontFamily(selectedFont: String): FontFamily =
    when (KronoFontOption.entries.find { it.name == selectedFont } ?: KronoFontOption.CHIVO) {
        KronoFontOption.CHIVO          -> Chivo
        KronoFontOption.CHIVO_LIGHT    -> ChivoLight
        KronoFontOption.JETBRAINS_MONO -> JetBrainsMono
        KronoFontOption.FIRA_CODE      -> FiraCode
        KronoFontOption.ANONYMOUS_PRO  -> AnonymousPro
        KronoFontOption.ROBOTO_MONO    -> RobotoMono
        KronoFontOption.COMMIT_MONO    -> SpaceMono
        KronoFontOption.AZERET_MONO    -> AzeretMono
        KronoFontOption.CHIVO_MONO     -> ChivoMono
        else -> Chivo
    }

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


