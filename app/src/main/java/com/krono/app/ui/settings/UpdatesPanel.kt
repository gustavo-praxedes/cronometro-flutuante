package com.krono.app.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoTokens
import com.krono.app.core.ui.components.SkeletonLoader
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
            .padding(horizontal = KronoTokens.Spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(KronoTokens.Spacing.xl))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text       = "Nova Versão Disponível",
                style      = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                fontSize   = KronoTokens.Typography.dialogTitle,
                textAlign  = TextAlign.Center,
                modifier   = Modifier.padding(horizontal = KronoTokens.Spacing.dialogPadding)
            )
            Text(
                text     = "Versão v$version",
                style    = MaterialTheme.typography.bodyMedium,
                color    = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(KronoTokens.Spacing.xl))

        AnimatedVisibility(
            visible = showDownloadStartedMsg,
            enter   = fadeIn(
                animationSpec = tween(
                    durationMillis = KronoTokens.Animation.fadeDurationMs,
                    easing = KronoTokens.Motion.easingNormal
                )
            ),
            exit    = fadeOut(
                animationSpec = tween(
                    durationMillis = KronoTokens.Animation.fadeDurationMs,
                    easing = KronoTokens.Motion.easingNormal
                )
            )
        ) {
            Card(
                colors   = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = KronoTokens.Spacing.md)
            ) {
                Row(
                    modifier          = Modifier.padding(KronoTokens.Spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector        = KronoIcons.Action.Download,
                        contentDescription = null,
                        tint               = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier           = Modifier.size(KronoTokens.Icon.status)
                    )
                    Spacer(Modifier.width(KronoTokens.Spacing.sm))
                    Text(
                        text  = "O download continuará em segundo plano!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        if (isDownloading) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = KronoTokens.Spacing.lg)
            ) {
                LinearProgressIndicator(
                    progress  = { progress },
                    modifier  = Modifier
                        .fillMaxWidth()
                        .height(KronoTokens.Stroke.progressBar),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap  = StrokeCap.Round
                )
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(top = KronoTokens.Spacing.xs),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text  = "Baixando: ${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (downloadStatus is DownloadStatus.Failed) {
            Text(
                text     = "Falha no download. Tente novamente.",
                color    = MaterialTheme.colorScheme.error,
                style    = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(bottom = KronoTokens.Spacing.sm)
            )
        }

        Spacer(Modifier.weight(1f))

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
                        imageVector        = KronoIcons.Action.Check,
                        contentDescription = null,
                        modifier           = Modifier.size(KronoTokens.Icon.button)
                    )
                    Spacer(Modifier.width(KronoTokens.Button.iconSpacing))
                    Text(
                        text       = "Instalar agora",
                        fontWeight = FontWeight.Bold,
                        fontSize   = KronoTokens.Typography.buttonLabel
                    )
                }
                 isDownloading -> {
                     SkeletonLoader.SkeletonButton(
                         modifier = Modifier
                             .width(60.dp)
                             .height(KronoTokens.Component.buttonSpinner)
                     )
                     Spacer(Modifier.width(KronoTokens.Button.iconSpacing))
                     Text(
                         text     = "Baixando...",
                         fontSize = KronoTokens.Typography.buttonLabel
                     )
                 }
                else -> {
                    Icon(
                        imageVector        = KronoIcons.Action.Download,
                        contentDescription = null,
                        modifier           = Modifier.size(KronoTokens.Icon.button)
                    )
                    Spacer(Modifier.width(KronoTokens.Button.iconSpacing))
                    Text(
                        text       = "Baixar e instalar",
                        fontWeight = FontWeight.Bold,
                        fontSize   = KronoTokens.Typography.buttonLabel
                    )
                }
            }
        }

        Spacer(Modifier.height(KronoTokens.Spacing.xxl))
    }
}