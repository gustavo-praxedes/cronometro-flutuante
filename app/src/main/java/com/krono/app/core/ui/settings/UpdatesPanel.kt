package com.krono.app.core.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.krono.app.BuildConfig
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoTokens
import com.krono.app.core.util.ApkInstaller
import com.krono.app.core.util.DownloadStatus
import com.krono.app.core.util.UpdateInfo
import com.krono.app.core.util.UpdateResult
import com.krono.app.core.util.checkForUpdate
import kotlinx.coroutines.launch

@Composable
fun UpdatesPanel(
    updateInfo: UpdateInfo,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()

    var remoteInfo by remember { mutableStateOf<UpdateInfo?>(null) }
    var checking by remember { mutableStateOf(false) }
    var upToDate by remember { mutableStateOf(false) }
    var downloadStatus by remember { mutableStateOf<DownloadStatus>(DownloadStatus.NotDownloaded) }
    var downloadId by remember { mutableLongStateOf(-1L) }

    val activeInfo = remoteInfo ?: updateInfo
    val activeVersion = activeInfo.tagName.removePrefix("v")
    val hasNewVersion = remoteInfo != null && !upToDate
    val isDownloading = downloadStatus is DownloadStatus.Downloading
    val isDownloaded = downloadStatus is DownloadStatus.Completed

    val changelogItems = remember(activeInfo.changelog) {
        val parsed = parseChangelog(activeInfo.changelog)
        if (parsed.isEmpty()) listOf(ChangelogItem("Sem mudanças registradas.", ItemType.OTHER)) else parsed
    }

    LaunchedEffect(downloadId) {
        if (downloadId != -1L) {
            while (true) {
                kotlinx.coroutines.delay(500)
                downloadStatus = ApkInstaller.getDownloadStatus(context)
                if (downloadStatus is DownloadStatus.Completed || downloadStatus is DownloadStatus.Failed) break
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = KronoTokens.Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(KronoTokens.Spacing.lg))

        Surface(
            shape = RoundedCornerShape(50),
            color = if (hasNewVersion) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer
        ) {
            Text(
                text = if (hasNewVersion) "Nova Versão" else "Versão Atual",
                modifier = Modifier.padding(horizontal = KronoTokens.Spacing.md, vertical = KronoTokens.Spacing.xs),
                style = MaterialTheme.typography.labelMedium
            )
        }

        Spacer(Modifier.height(KronoTokens.Spacing.md))

        SettingsGroup(title = "O QUE HÁ DE NOVO") {
            Column(modifier = Modifier.padding(KronoTokens.Spacing.md)) {
                changelogItems.take(6).forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = KronoTokens.Spacing.xs),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(24.dp).clip(RoundedCornerShape(6.dp)).background(item.type.iconTint.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(item.type.icon, null, tint = item.type.iconTint, modifier = Modifier.size(12.dp))
                        }
                        Spacer(Modifier.width(KronoTokens.Spacing.sm))
                        Text(item.text, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                    }
                    if (index < minOf(changelogItems.size, 6) - 1) {
                        HorizontalDivider(modifier = Modifier.padding(start = 32.dp), thickness = 0.5.dp)
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = {
                when {
                    isDownloaded -> ApkInstaller.installApk(context, activeVersion)
                    upToDate -> Unit
                    hasNewVersion && activeInfo.downloadUrl != null -> {
                        downloadId = ApkInstaller.startDownload(context, activeInfo.downloadUrl, activeVersion)
                    }
                    else -> {
                        checking = true
                        scope.launch {
                            when (val result = checkForUpdate(BuildConfig.VERSION_NAME)) {
                                is UpdateResult.UpdateAvailable -> {
                                    remoteInfo = result.info
                                    upToDate = false
                                }
                                is UpdateResult.UpToDate -> {
                                    remoteInfo = null
                                    upToDate = true
                                }
                                is UpdateResult.NetworkError -> Unit
                            }
                            checking = false
                        }
                    }
                }
            },
            enabled = !checking && !isDownloading,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .height(KronoTokens.Button.height),
            shape = KronoTokens.Shape.button,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (upToDate) Color(0xFF16A34A) else MaterialTheme.colorScheme.primary
            )
        ) {
            when {
                checking -> Text("Verificando...")
                isDownloading -> Text("Baixando...")
                isDownloaded -> Text("Instalar")
                upToDate -> Text("App atualizado")
                hasNewVersion -> Text("Baixar")
                else -> Text("Verificar atualizações")
            }
        }

        Spacer(Modifier.height(KronoTokens.Spacing.md))
    }
}
