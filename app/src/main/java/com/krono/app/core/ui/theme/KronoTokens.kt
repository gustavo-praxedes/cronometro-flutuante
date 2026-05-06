package com.krono.app.core.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * KronoTokens — Design Token System
 * Sistema centralizado de estilos para todos os componentes do app.
 */
object KronoTokens {

    // ── Cores (Fixas/Utilitárias) ───────────────────────────
    object Colors {
        val checkerLight = Color(0xFFCCCCCC)
        val checkerDark  = Color(0xFF999999)
    }

    // ── Formas e Arredondamentos ─────────────────────────────
    object Shape {
        val dialog         = RoundedCornerShape(24.dp)
        val button         = RoundedCornerShape(16.dp)
        val buttonSmall    = RoundedCornerShape(12.dp)
        val card           = RoundedCornerShape(20.dp)
        val input          = RoundedCornerShape(12.dp)
        val badge          = RoundedCornerShape(6.dp)
        val progressBar    = RoundedCornerShape(50)
        val iconContainer  = RoundedCornerShape(12.dp)
    }

    // ── Tamanhos de Botões ───────────────────────────────────
    object Button {
        val height         = 56.dp
        val heightSmall    = 44.dp
        val iconSize       = 24.dp
        val iconSpacing    = 10.dp
        val paddingH       = 24.dp
    }

    // ── Espaçamentos ─────────────────────────────────────────
    object Spacing {
        val xs   = 4.dp
        val sm   = 8.dp
        val md   = 12.dp
        val lg   = 16.dp
        val xl   = 20.dp
        val xxl  = 24.dp
        val xxxl = 32.dp

        val listBottomPadding = 100.dp
        val dialogPadding   = 20.dp
        val dialogWidthFrac = 0.92f
        val maxDialogWidth  = 440.dp
        val listItemGap     = 10.dp
        val listIconGap     = 12.dp
        val sectionGap      = 20.dp
        val cardPaddingH    = 18.dp
        val cardPaddingV    = 14.dp
    }

    // ── Tipografia (Valores Base) ──────────────────────────
    object Typography {
        val dialogTitle      = 22.sp
        val dialogSubtitle   = 14.sp
        val listItem         = 14.sp
        val buttonLabel      = 16.sp
        val buttonLabelSmall = 14.sp
        val statusLabel      = 12.sp
        val errorLabel       = 12.sp
        val bodyText         = 16.sp
        val timerCard        = 34.sp
        val timerOverlay     = 40.sp
    }

    // ── Tamanhos de Ícones ───────────────────────────────────
    object Icon {
        val listItem      = 20.dp
        val dialogHeader  = 24.dp
        val status        = 18.dp
        val small         = 16.dp
        val button        = 22.dp
        val close         = 32.dp
        val cardAction    = 24.dp
    }

    // ── Elevação e Sombras ───────────────────────────────────
    object Elevation {
        val dialog    = 6.dp
        val card      = 3.dp
        val flat      = 0.dp
    }

    // ── Espessuras de Linha ──────────────────────────────────
    object Stroke {
        val progressBar  = 8.dp
        val circularIndicator = 2.dp
        val divider      = 1.dp
        val cardBorder   = 1.dp
        val overlayBorder = 0.86.dp
    }

    // ── Animações ────────────────────────────────────────────
    object Animation {
        val fadeDurationMs     = 200
        val toastDurationMs    = 3_000
        val menuAutoDismissMs  = 5_000
    }

     // ── Opacidades ───────────────────────────────────────────
     object Alpha {
         val divider      = 0.5f
         val disabled     = 0.38f
         val scrim        = 0.6f
         val low          = 0.10f
         val medium       = 0.75f
         val highlight    = 0.12f
         val glass        = 0.08f
         val glassBorder  = 0.15f
         val label        = 0.6f
         val separator    = 0.3f
         val iconDisabled = 0.45f
         val iconEnabled  = 1.0f
         val entranceInitialScale = 0.88f
         val dragScaleTarget      = 0.96f
         val overlayTimerScale    = 0.72f
         val overlayLuminanceThreshold = 0.45f
     }

     // ── Animações Avançadas ───────────────────────────────────
     object Motion {
         val durationFast   = 150
         val durationNormal = 250
         val durationSlow   = 400

         val easingFast    = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1f)
         val easingNormal  = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1f)
         val easingSlow    = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)
     }

    // ── Floating Action Button (FAB) ────────────────────────
    object FAB {
        val size           = 68.dp
        val iconSize       = 30.dp
    }

    // ── Ícones de Estado ────────────────────────────────────
    object StateIcon {
        val emptyLarge     = 88.dp
        val emptyMedium    = 52.dp
    }

    // ── Tamanhos de Componentes Específicos ─────────────────
    object Component {
        val inlineSpinner  = 18.dp
        val buttonSpinner  = 20.dp
        val listItemHeight = 48.dp
        val colorSwatch    = 36.dp
        val colorPreview   = 120.dp
        val colorInputWidth = 180.dp
        val sliderLabelWidth = 92.dp
        val sliderValueWidth = 52.dp
        const val descriptionMaxLen = 40
    }

    // ── Roda de Seleção (Wheel Picker) ──────────────────────
    object Wheel {
        val itemHeight       = 48.dp
        val columnWidth      = 80.dp
        val columnInnerWidth = 64.dp
        val columnFocusWidth = 56.dp
        val separatorWidth   = 16.dp
        
        val labelFontSize     = 10.sp
        val selectedFontSize  = 26.sp
        val separatorFontSize = 22.sp

        const val rotationFactor = 20f
        const val cameraDistance = 8f
        
        val scaleSelected    = 1.15f
        val scaleMedium      = 0.85f
        val scaleSmall       = 0.7f

        val alphaSelected    = 1.0f
        val alphaMedium      = 0.4f
        val alphaSmall       = 0.15f
    }

    // ── Overlay (Widget Flutuante) ──────────────────────────
    object Overlay {
        const val maxCornerRadiusFloat = 80f
        val defaultCornerRadius = 24.dp
        val minWidth       = 172.dp
        val maxWidth       = 270.dp
        val paddingH       = 16.dp
        val paddingV       = 16.dp
        val btnSpacing     = 10.dp
        val btnTopPadding  = 8.dp
        val menuPaddingV   = 8.dp
        val timerFontSize  = 40.sp
        val iconSize       = 28.dp
        val buttonSize     = 28.dp
        val quickBtnSize   = 34.dp
        val quickIconSize  = 26.dp
        val menuTimeoutMs  = 5000L
    }

    // ── Bottom Bar (Navegação) ──────────────────────────────
    object BottomBar {
        val height         = 78.dp
        val iconSize       = 24.dp
        val labelSize      = 12.sp
        val alphaContainer = 0.20f
        val alphaSelected  = 0.46f
    }
}
