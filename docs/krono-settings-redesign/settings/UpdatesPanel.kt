package com.krono.app.core.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoTokens
import com.krono.app.core.util.ApkInstaller
import com.krono.app.core.util.DownloadStatus
import com.krono.app.core.util.UpdateInfo

@Composable
fun UpdatesPanel(
    updateInfo: UpdateInfo,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val version = remember(updateInfo.tagName) { updateInfo.tagName.removePrefix("v") }

    val changelogItems = remember(updateInfo.changelog) {
        val items = parseChangelog(updateInfo.changelog)
        if (items.isEmpty()) {
            listOf(ChangelogItem("Esta atualização traz melhorias de estabilidade e correções internas.", ItemType.OTHER))
        } else {
            items
        }
    }

    var downloadStatus by remember {
        val initialStatus = if (ApkInstaller.getDownloadedFile(context, version)?.exists() == true) {
            DownloadStatus.Completed
        } else {
            ApkInstaller.getDownloadStatus(context)
        }
        mutableStateOf(initialStatus)
    }

    var downloadId             by remember { mutableLongStateOf(-1L) }
    var showDownloadStartedMsg by remember { mutableStateOf(false) }

    val isDownloaded  = downloadStatus is DownloadStatus.Completed
    val isDownloading = downloadStatus is DownloadStatus.Downloading
    val progress      = (downloadStatus as? DownloadStatus.Downloading)?.percent?.toFloat()?.div(100f) ?: 0f

    LaunchedEffect(downloadId) {
        if (downloadId != -1L) {
            while (true) {
                kotlinx.coroutines.delay(500)
                downloadStatus = ApkInstaller.getDownloadStatus(context)
                if (downloadStatus is DownloadStatus.Completed || downloadStatus is DownloadStatus.Failed) break
            }
        }
    }

    LaunchedEffect(showDownloadStartedMsg) {
        if (showDownloadStartedMsg) {
            kotlinx.coroutines.delay(KronoTokens.Animation.toastDurationMs.toLong())
            showDownloadStartedMsg = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = KronoTokens.Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(KronoTokens.Spacing.lg))

        // Update available badge + title
        Surface(
            shape = RoundedCornerShape(50),
            color = MaterialTheme.colorScheme.errorContainer,
            modifier = Modifier.wrapContentSize()
        ) {
            Text(
                text = "Nova Versão",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(
                    horizontal = KronoTokens.Spacing.md,
                    vertical = KronoTokens.Spacing.xs
                )
            )
        }

        Spacer(Modifier.height(KronoTokens.Spacing.sm))

        Text(
            text = "v$version disponível",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(KronoTokens.Spacing.lg))

        // Download started toast
        AnimatedVisibility(
            visible = showDownloadStartedMsg,
            enter   = fadeIn(animationSpec = tween(KronoTokens.Animation.fadeDurationMs)),
            exit    = fadeOut(animationSpec = tween(KronoTokens.Animation.fadeDurationMs))
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = KronoTokens.Spacing.md),
                shape = KronoTokens.Shape.card,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Row(
                    modifier = Modifier.padding(KronoTokens.Spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = KronoIcons.Action.Download,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(KronoTokens.Icon.status)
                    )
                    Spacer(Modifier.width(KronoTokens.Spacing.sm))
                    Text(
                        text = "Download iniciado em segundo plano",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Progress bar
        if (isDownloading) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = KronoTokens.Spacing.lg)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = KronoTokens.Spacing.xs),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Baixando atualização...",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                LinearProgressIndicator(
                    progress  = { progress },
                    modifier  = Modifier
                        .fillMaxWidth()
                        .height(KronoTokens.Stroke.progressBar)
                        .clip(RoundedCornerShape(50)),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap  = StrokeCap.Round
                )
            }
        }

        if (downloadStatus is DownloadStatus.Failed) {
            Text(
                text  = "Falha no download. Tente novamente.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(bottom = KronoTokens.Spacing.sm)
            )
        }

        // Changelog preview
        SettingsGroup(title = "O que há de novo") {
            Column(modifier = Modifier.padding(KronoTokens.Spacing.md)) {
                changelogItems.take(5).forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = KronoTokens.Spacing.xs),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(item.type.iconTint.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.type.icon,
                                contentDescription = null,
                                tint = item.type.iconTint,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                        Spacer(Modifier.width(KronoTokens.Spacing.sm))
                        Text(
                            text = item.text,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (index < minOf(changelogItems.size, 5) - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 32.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))

        // Action button
        Button(
            onClick = {
                if (isDownloaded) {
                    ApkInstaller.installApk(context, version)
                } else if (!isDownloading) {
                    val url = updateInfo.downloadUrl ?: return@Button
                    downloadId = ApkInstaller.startDownload(context, url, version)
                    showDownloadStartedMsg = true
                }
            },
            enabled  = !isDownloading || isDownloaded,
            modifier = Modifier
                .fillMaxWidth()
                .height(KronoTokens.Button.height),
            shape = KronoTokens.Shape.button
        ) {
            when {
                isDownloaded -> {
                    Icon(
                        imageVector = KronoIcons.Action.Check,
                        contentDescription = null,
                        modifier = Modifier.size(KronoTokens.Icon.button)
                    )
                    Spacer(Modifier.width(KronoTokens.Button.iconSpacing))
                    Text(
                        text = "Instalar agora",
                        fontWeight = FontWeight.Bold,
                        fontSize = KronoTokens.Typography.buttonLabel
                    )
                }
                isDownloading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(KronoTokens.Component.buttonSpinner),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(KronoTokens.Button.iconSpacing))
                    Text(
                        text = "Baixando...",
                        fontSize = KronoTokens.Typography.buttonLabel
                    )
                }
                else -> {
                    Icon(
                        imageVector = KronoIcons.Action.Download,
                        contentDescription = null,
                        modifier = Modifier.size(KronoTokens.Icon.button)
                    )
                    Spacer(Modifier.width(KronoTokens.Button.iconSpacing))
                    Text(
                        text = "Baixar e instalar",
                        fontWeight = FontWeight.Bold,
                        fontSize = KronoTokens.Typography.buttonLabel
                    )
                }
            }
        }

        Spacer(Modifier.height(KronoTokens.Spacing.xxl))
    }
}
