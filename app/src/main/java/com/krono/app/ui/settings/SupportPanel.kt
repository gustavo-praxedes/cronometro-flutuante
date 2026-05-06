package com.krono.app.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import com.krono.app.core.data.formatLifetimeDetailed
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoTokens

private const val KOFI_URL = "https://ko-fi.com/gustavopraxedes"

@Composable
fun SupportPanel(
    totalLifetimeMs: Long,
    onDonate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val formattedTime = remember(totalLifetimeMs) { formatLifetimeDetailed(totalLifetimeMs) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = KronoTokens.Spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(KronoTokens.Spacing.xl))

        Text(
            text = "Apoie o Projeto",
            style = MaterialTheme.typography.headlineSmall.copy(
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            ),
            fontWeight = FontWeight.Bold,
            fontSize   = KronoTokens.Typography.dialogTitle
        )

        Spacer(Modifier.height(KronoTokens.Spacing.sectionGap))

        Text(
            text = buildAnnotatedString {
                append("Incrível! Você já utilizou nosso Cronômetro por ")
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.primary
                    )
                ) { append(formattedTime) }
                append(". Este projeto é independente e seu apoio ajuda a mantê-lo gratuito e sem anúncios.")
            },
            style = MaterialTheme.typography.bodyLarge.copy(
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            ),
            fontSize = KronoTokens.Typography.bodyText,
            textAlign = TextAlign.Center,
            color     = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(KronoTokens.Spacing.sectionGap))

        Button(
            onClick  = { openKofi(context); onDonate() },
            modifier = Modifier
                .fillMaxWidth()
                .height(KronoTokens.Button.height),
            shape  = KronoTokens.Shape.button,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor   = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(
                imageVector        = KronoIcons.Status.Coffee,
                contentDescription = null,
                modifier           = Modifier.size(KronoTokens.Icon.button)
            )
            Spacer(Modifier.width(KronoTokens.Button.iconSpacing))
            Text(
                text       = "Pagar um café",
                fontSize = KronoTokens.Typography.bodyText,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(KronoTokens.Spacing.xxl))
    }
}

private fun openKofi(context: Context) {
    context.startActivity(
        Intent(Intent.ACTION_VIEW, Uri.parse(KOFI_URL)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    )
}