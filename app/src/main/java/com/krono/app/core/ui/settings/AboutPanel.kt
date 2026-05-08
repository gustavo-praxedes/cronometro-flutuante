package com.krono.app.core.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
            .padding(horizontal = KronoTokens.Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(KronoTokens.Spacing.xl))

        // App identity header
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = KronoTokens.Spacing.md),
            shape = KronoTokens.Shape.card,
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
            tonalElevation = 0.dp
        ) {
            Column(
                modifier = Modifier.padding(KronoTokens.Spacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "⏱ Krono",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = KronoTokens.Typography.dialogTitle
                )
                Spacer(Modifier.height(KronoTokens.Spacing.xs))
                Text(
                    text = "Versão ${BuildConfig.VERSION_NAME}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = KronoTokens.Typography.statusLabel
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(KronoTokens.Spacing.sm))
                Text(
                    text = "Widget flutuante de cronômetro. Gratuito, sem anúncios e código aberto.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }
        }

        Spacer(Modifier.height(KronoTokens.Spacing.xl))

        SettingsGroup(title = "Ações") {
            AboutActionRow(
                icon = KronoIcons.Status.Favorite,
                iconColor = Color(0xFFEF4444),
                title = "Apoiar o Projeto",
                subtitle = "Ko-fi · Ajude a manter gratuito",
                trailing = TrailingType.Chevron,
                onClick = onSupportClick
            )

            RowDivider()

            AboutActionRow(
                icon = KronoIcons.Status.Bug,
                iconColor = Color(0xFFF59E0B),
                title = "Relatar Bug",
                subtitle = "Encontrou algo errado?",
                trailing = TrailingType.Chevron,
                onClick = { showBugReport = true }
            )

            RowDivider()

            AboutActionRow(
                icon = KronoIcons.Status.Source,
                iconColor = Color(0xFF6B7FD4),
                title = "Código Fonte",
                subtitle = "GitHub · Contribua com o projeto",
                trailing = TrailingType.External,
                onClick = {
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_URL))
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                }
            )
        }

        Spacer(Modifier.height(KronoTokens.Spacing.lg))

        SettingsGroup(title = "Informações") {
            AboutActionRow(
                icon = KronoIcons.Settings.History,
                iconColor = Color(0xFF10B981),
                title = "Novidades da Versão",
                subtitle = "Ver o que mudou nesta versão",
                trailing = TrailingType.Chevron,
                onClick = { onShowChangelog(localUpdateInfo) }
            )
        }

        Spacer(Modifier.height(KronoTokens.Spacing.xxl))
    }

    if (showBugReport) {
        BugReportDialog(onDismiss = { showBugReport = false })
    }
}

private enum class TrailingType { Chevron, External }

@Composable
private fun AboutActionRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    trailing: TrailingType,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                horizontal = KronoTokens.Spacing.lg,
                vertical = KronoTokens.Spacing.md
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = iconColor
            )
        }

        Spacer(Modifier.width(KronoTokens.Spacing.md))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = KronoTokens.Typography.statusLabel
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            imageVector = when (trailing) {
                TrailingType.Chevron  -> KronoIcons.Navigation.ChevronRight
                TrailingType.External -> KronoIcons.Navigation.OpenExternal
            },
            contentDescription = null,
            modifier = Modifier.size(KronoTokens.Icon.small),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun RowDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 68.dp, end = KronoTokens.Spacing.lg),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
        thickness = 0.5.dp
    )
}
