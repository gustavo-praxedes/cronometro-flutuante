package com.krono.app.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.krono.app.BuildConfig
import com.krono.app.R
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoTokens
import com.krono.app.core.ui.dialogs.BugReportDialog
import com.krono.app.core.util.UpdateInfo

private const val GITHUB_URL = "https://github.com/gustavo-praxedes/krono"

@Composable
fun AboutPanel(
    onSupportClick: () -> Unit,
    onShowChangelog: (UpdateInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showBugReport by remember { mutableStateOf(false) }

    val localChangelog = remember {
        try {
            context.resources.openRawResource(R.raw.changelog).bufferedReader().readText()
        } catch (_: Exception) { "" }
    }

    val localUpdateInfo = remember {
        UpdateInfo(
            tagName     = BuildConfig.VERSION_NAME,
            changelog   = localChangelog,
            releaseUrl  = GITHUB_URL,
            downloadUrl = null
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = KronoTokens.Spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(KronoTokens.Spacing.xl))

        Text(
            text       = "Krono",
            style      = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            fontSize   = KronoTokens.Typography.dialogTitle
        )

        Spacer(Modifier.height(KronoTokens.Spacing.sectionGap))

        Text(
            text      = "O widget que flutua sobre qualquer app. Gratuito, sem anúncios e de código aberto.",
            style     = MaterialTheme.typography.bodyMedium,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            fontSize  = KronoTokens.Typography.bodyText
        )

        Spacer(Modifier.height(KronoTokens.Spacing.sectionGap))

        Button(
            onClick  = onSupportClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(KronoTokens.Button.height),
            shape  = KronoTokens.Shape.button
        ) {
            Icon(
                imageVector        = KronoIcons.Status.Favorite,
                contentDescription = null,
                modifier           = Modifier.size(KronoTokens.Icon.button)
            )
            Spacer(Modifier.width(KronoTokens.Button.iconSpacing))
            Text(
                text       = "Apoiar",
                fontWeight = FontWeight.SemiBold,
                fontSize   = KronoTokens.Typography.buttonLabel
            )
        }

        Spacer(Modifier.height(KronoTokens.Spacing.sm))

        OutlinedButton(
            onClick  = { showBugReport = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(KronoTokens.Button.height),
            shape = KronoTokens.Shape.button
        ) {
            Icon(
                imageVector        = KronoIcons.Status.Bug,
                contentDescription = null,
                modifier           = Modifier.size(KronoTokens.Icon.button)
            )
            Spacer(Modifier.width(KronoTokens.Button.iconSpacing))
            Text(
                text       = "Relatar Bug",
                fontWeight = FontWeight.SemiBold,
                fontSize   = KronoTokens.Typography.buttonLabel
            )
        }

        Spacer(Modifier.height(KronoTokens.Spacing.sm))

        OutlinedButton(
            onClick  = {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_URL))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(KronoTokens.Button.height),
            shape = KronoTokens.Shape.button
        ) {
            Icon(
                imageVector        = KronoIcons.Status.Source,
                contentDescription = null,
                modifier           = Modifier.size(KronoTokens.Icon.button)
            )
            Spacer(Modifier.width(KronoTokens.Button.iconSpacing))
            Text(
                text       = "Código Fonte",
                fontWeight = FontWeight.SemiBold,
                fontSize   = KronoTokens.Typography.buttonLabel
            )
        }

        Spacer(Modifier.height(KronoTokens.Spacing.sectionGap))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onShowChangelog(localUpdateInfo) }
                .padding(vertical = KronoTokens.Spacing.sm),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = "Versão ${BuildConfig.VERSION_NAME}",
                    style      = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text  = "Ver Novidades",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Icon(
                imageVector        = KronoIcons.Navigation.OpenExternal,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.primary,
                modifier           = Modifier.size(KronoTokens.Icon.dialogHeader)
            )
        }

        Spacer(Modifier.height(KronoTokens.Spacing.xxl))
    }

    if (showBugReport) {
        BugReportDialog(onDismiss = { showBugReport = false })
    }
}