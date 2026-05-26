package com.krono.app.core.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.krono.app.BuildConfig
import com.krono.app.R
import com.krono.app.core.ui.components.SettingsDivider
import com.krono.app.core.ui.components.SettingsRow
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoTokens
import com.krono.app.core.util.ApkInstaller
import com.krono.app.core.util.DownloadStatus
import com.krono.app.core.util.UpdateInfo
import com.krono.app.core.util.UpdateResult
import com.krono.app.core.util.checkForUpdate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun UpdatesPanel(
    updateInfo: UpdateInfo,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = KronoTokens.Settings.panelHorizontalInset)
    ) {
        Spacer(Modifier.height(KronoTokens.Settings.panelTopSpacing))
        UpdatesPanelContent(updateInfo = updateInfo)
        Spacer(Modifier.weight(1f))
    }
}

@Composable
fun UpdatesPanelContent(
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
    val activeVersionLabel = stringResource(R.string.version_prefix, activeVersion)
    val noChangesText = stringResource(R.string.updates_no_changes)
    val hasNewVersion = remoteInfo != null && !upToDate
    val isDownloading = downloadStatus is DownloadStatus.Downloading
    val isDownloaded = downloadStatus is DownloadStatus.Completed

    val changelogItems = remember(activeInfo.changelog, noChangesText) {
        val parsed = parseChangelog(activeInfo.changelog)
        if (parsed.isEmpty()) listOf(ChangelogItem(noChangesText, ItemType.OTHER)) else parsed
    }

    LaunchedEffect(downloadId) {
        if (downloadId != -1L) {
            while (true) {
                delay(500)
                downloadStatus = ApkInstaller.getDownloadStatus(context)
                if (downloadStatus is DownloadStatus.Completed || downloadStatus is DownloadStatus.Failed) break
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Surface(
            shape = KronoTokens.Shape.progressBar,
            color = if (hasNewVersion) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.secondaryContainer
            }
        ) {
            Text(
                text = if (hasNewVersion) {
                    stringResource(R.string.updates_pill_new_version, activeVersionLabel)
                } else {
                    stringResource(R.string.updates_pill_current_version, activeVersionLabel)
                },
                modifier = Modifier.padding(
                    horizontal = KronoTokens.Spacing.md,
                    vertical = KronoTokens.Spacing.xs
                ),
                style = MaterialTheme.typography.labelMedium
            )
        }

        Spacer(Modifier.height(KronoTokens.Settings.panelSectionGap))

        SettingsGroup(title = stringResource(R.string.updates_whats_new_title)) {
            changelogItems.take(6).forEachIndexed { index, item ->
                SettingsRow(
                    title = item.text,
                    leadingIcon = item.type.icon,
                    iconTint = item.type.iconTint,
                    iconContainerColor = item.type.iconTint.copy(alpha = 0.14f)
                )
                if (index < minOf(changelogItems.size, 6) - 1) {
                    SettingsDivider()
                }
            }
        }
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
                .height(KronoTokens.Button.height),
            shape = KronoTokens.Shape.button,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (upToDate) Color(0xFF16A34A) else MaterialTheme.colorScheme.primary
            )
        ) {
            val actionIcon = when {
                checking || hasNewVersion -> KronoIcons.Status.MobileAlert
                isDownloading -> KronoIcons.Status.MobileArrowDown
                isDownloaded || upToDate -> KronoIcons.Status.MobileCheck
                else -> KronoIcons.Status.Update
            }
            val label = when {
                checking -> stringResource(R.string.updates_action_checking)
                isDownloading -> stringResource(R.string.updates_action_downloading)
                isDownloaded -> stringResource(R.string.updates_action_install)
                upToDate -> stringResource(R.string.updates_action_updated)
                hasNewVersion -> stringResource(R.string.updates_action_download)
                else -> stringResource(R.string.updates_action_check)
            }
            Icon(
                imageVector = actionIcon,
                contentDescription = null
            )
            Spacer(Modifier.width(KronoTokens.Button.iconSpacing))
            Text(label)
        }

        Spacer(Modifier.height(KronoTokens.Spacing.md))
    }
}
