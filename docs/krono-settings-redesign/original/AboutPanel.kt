package com.krono.app.core.ui.settings

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
import com.krono.app.core.ui.theme.KronoIcons.Navigation.ChevronRight
import com.krono.app.core.ui.theme.KronoIcons.Status.Info
import com.krono.app.core.ui.theme.KronoTokens
import com.krono.app.core.ui.dialogs.BugReportDialog
import com.krono.app.core.util.UpdateInfo
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.unit.dp

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

        Spacer(Modifier.height(KronoTokens.Spacing.lg))

        SettingsGroup(title = "Ações") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSupportClick() }
                    .padding(horizontal = KronoTokens.Spacing.lg, vertical = KronoTokens.Spacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector        = KronoIcons.Status.Favorite,
                    contentDescription = null,
                    modifier           = Modifier.size(KronoTokens.Icon.rowIcon),
                    tint               = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(KronoTokens.Spacing.md))
                Text(
                    text       = "Apoiar",
                    style      = MaterialTheme.typography.bodyMedium,
                    modifier   = Modifier.weight(1f)
                )
                Icon(
                    imageVector        = ChevronRight,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier           = Modifier.size(KronoTokens.Icon.rowTrailing)
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = KronoTokens.Spacing.lg),
                color    = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showBugReport = true }
                    .padding(horizontal = KronoTokens.Spacing.lg, vertical = KronoTokens.Spacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector        = KronoIcons.Status.Bug,
                    contentDescription = null,
                    modifier           = Modifier.size(KronoTokens.Icon.rowIcon),
                    tint               = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(KronoTokens.Spacing.md))
                Text(
                    text       = "Relatar Bug",
                    style      = MaterialTheme.typography.bodyMedium,
                    modifier   = Modifier.weight(1f)
                )
                Icon(
                    imageVector        = ChevronRight,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier           = Modifier.size(KronoTokens.Icon.rowTrailing)
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = KronoTokens.Spacing.lg),
                color    = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        context.startActivity(
                            Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_URL))
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    }
                    .padding(horizontal = KronoTokens.Spacing.lg, vertical = KronoTokens.Spacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector        = KronoIcons.Status.Source,
                    contentDescription = null,
                    modifier           = Modifier.size(KronoTokens.Icon.rowIcon),
                    tint               = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(KronoTokens.Spacing.md))
                Text(
                    text       = "Código Fonte",
                    style      = MaterialTheme.typography.bodyMedium,
                    modifier   = Modifier.weight(1f)
                )
                Icon(
                    imageVector        = KronoIcons.Navigation.OpenExternal,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier           = Modifier.size(KronoTokens.Icon.rowTrailing)
                )
            }
        }

        Spacer(Modifier.height(KronoTokens.Spacing.lg))

        SettingsGroup(title = "Informações") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onShowChangelog(localUpdateInfo) }
                    .padding(horizontal = KronoTokens.Spacing.lg, vertical = KronoTokens.Spacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector        = Info,
                    contentDescription = null,
                    modifier           = Modifier.size(KronoTokens.Icon.rowIcon),
                    tint               = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(KronoTokens.Spacing.md))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text       = "Versão ${BuildConfig.VERSION_NAME}",
                        style      = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text  = "Ver Novidades",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Icon(
                    imageVector        = ChevronRight,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier           = Modifier.size(KronoTokens.Icon.rowTrailing)
                )
            }
        }

        Spacer(Modifier.height(KronoTokens.Spacing.xxl))
    }

    if (showBugReport) {
        BugReportDialog(onDismiss = { showBugReport = false })
    }
}