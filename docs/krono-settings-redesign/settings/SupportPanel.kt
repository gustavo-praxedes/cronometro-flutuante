package com.krono.app.core.ui.settings

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
import androidx.compose.ui.unit.dp
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
            .padding(horizontal = KronoTokens.Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(KronoTokens.Spacing.xl))

        // Hero card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = KronoTokens.Shape.card,
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
            tonalElevation = 0.dp
        ) {
            Column(
                modifier = Modifier.padding(KronoTokens.Spacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = KronoIcons.Status.Coffee,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(KronoTokens.Spacing.md))

                Text(
                    text = "Apoie o Projeto",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(KronoTokens.Spacing.sm))

                Text(
                    text = buildAnnotatedString {
                        append("Você já usou o Krono por ")
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        ) { append(formattedTime) }
                        append(". Projeto independente — seu apoio mantém tudo gratuito e sem anúncios.")
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        platformStyle = PlatformTextStyle(includeFontPadding = false)
                    ),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                )
            }
        }

        Spacer(Modifier.height(KronoTokens.Spacing.xl))

        Button(
            onClick = { openKofi(context); onDonate() },
            modifier = Modifier
                .fillMaxWidth()
                .height(KronoTokens.Button.height),
            shape = KronoTokens.Shape.button,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(
                imageVector = KronoIcons.Status.Coffee,
                contentDescription = null,
                modifier = Modifier.size(KronoTokens.Icon.button)
            )
            Spacer(Modifier.width(KronoTokens.Button.iconSpacing))
            Text(
                text = "Pagar um café ☕",
                fontSize = KronoTokens.Typography.buttonLabel,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(KronoTokens.Spacing.sm))

        Text(
            text = "Abre o Ko-fi em seu navegador",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

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
