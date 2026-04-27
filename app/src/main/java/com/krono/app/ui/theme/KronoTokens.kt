package com.krono.app.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * KronoTokens — Design Token System
 * Sistema centralizado de estilos para todos os componentes do app.
 */
object KronoTokens {

    // ── Formas e Arredondamentos ─────────────────────────────
    object Shape {
        val dialog         = RoundedCornerShape(24.dp)
        val button         = RoundedCornerShape(16.dp)
        val buttonSmall    = RoundedCornerShape(12.dp)
        val card           = RoundedCornerShape(16.dp)
        val input          = RoundedCornerShape(12.dp)
        val badge          = RoundedCornerShape(8.dp)
        val progressBar    = RoundedCornerShape(50)
        val iconContainer  = RoundedCornerShape(12.dp)
    }

    // ── Tamanhos de Botões ───────────────────────────────────
    object Button {
        val height         = 56.dp
        val heightSmall    = 44.dp
        val iconSize       = 20.dp
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

        val dialogPadding   = 20.dp
        val dialogWidthFrac = 0.92f
        val maxDialogWidth  = 440.dp
        val listItemGap     = 10.dp
        val listIconGap     = 12.dp
        val sectionGap      = 20.dp
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
    }

    // ── Tamanhos de Ícones ───────────────────────────────────
    object Icon {
        val listItem      = 20.dp
        val dialogHeader  = 24.dp
        val status        = 18.dp
        val small         = 16.dp
        val button        = 20.dp
        val close         = 32.dp
    }

    // ── Elevação e Sombras ───────────────────────────────────
    object Elevation {
        val dialog    = 6.dp
        val card      = 2.dp
        val flat      = 0.dp
    }

    // ── Espessuras de Linha ──────────────────────────────────
    object Stroke {
        val progressBar  = 8.dp
        val circularIndicator = 2.dp
        val divider      = 1.dp
        val cardBorder   = 1.dp
    }

    // ── Animações ────────────────────────────────────────────
    object Animation {
        val fadeDurationMs     = 200
        val toastDurationMs    = 3_000
        val menuAutoDismissMs  = 5_000
    }

     // ── Opacidades ───────────────────────────────────────────
     object Alpha {
         val divider   = 0.5f
         val disabled  = 0.38f
         val scrim     = 0.6f
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

    // ── Tamanhos de Componentes Específicos ─────────────────
    object Component {
        val inlineSpinner  = 18.dp
        val buttonSpinner  = 20.dp
        val listItemHeight = 48.dp
    }

    // ── Overlay (Widget Flutuante) ──────────────────────────
    object Overlay {
        const val maxCornerRadiusFloat = 80f
        val defaultCornerRadius = 24.dp
        val minWidth       = 172.dp
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
}
