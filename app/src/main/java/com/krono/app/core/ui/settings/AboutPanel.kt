package com.krono.app.core.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.krono.app.R
import com.krono.app.core.data.formatLifetimeDetailed
import com.krono.app.core.ui.components.SettingsDivider
import com.krono.app.core.ui.components.SettingsRow
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoTokens
import com.krono.app.core.util.ApkInstaller
import com.krono.app.core.util.DownloadStatus
import com.krono.app.core.util.UpdateInfo
import com.krono.app.core.util.UpdateResult
import com.krono.app.core.util.checkForUpdate
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.krono.app.BuildConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val SUPPORT_MIN_USAGE_MS = 5 * 60 * 1000L

private const val GITHUB_URL = "https://github.com/gustavo-praxedes/krono"
private const val LATEST_APK_URL = "https://github.com/gustavo-praxedes/krono/releases/latest/download/krono.apk"
private const val KOFI_URL = "https://ko-fi.com/gustavopraxedes"

@Composable
fun AboutPanel(
    totalLifetimeMs: Long,
    updateInfo: UpdateInfo,
    onDonate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val appName = stringResource(R.string.app_name)
    val shareChooser = stringResource(R.string.about_share_chooser)
    val formattedTime = remember(totalLifetimeMs) { formatLifetimeDetailed(totalLifetimeMs) }
    val noChangesText = stringResource(R.string.updates_no_changes)
    var remoteInfo by remember { mutableStateOf<UpdateInfo?>(null) }
    var checking by remember { mutableStateOf(false) }
    var upToDate by remember { mutableStateOf(false) }
    var downloadStatus by remember { mutableStateOf<DownloadStatus>(DownloadStatus.NotDownloaded) }
    var downloadId by remember { mutableLongStateOf(-1L) }
    val activeInfo = remoteInfo ?: updateInfo
    val hasNewVersion = remoteInfo != null && !upToDate
    val isDownloading = downloadStatus is DownloadStatus.Downloading
    val isDownloaded = downloadStatus is DownloadStatus.Completed
    val changelogItems = remember(activeInfo.changelog, noChangesText) {
        parseChangelog(activeInfo.changelog).ifEmpty {
            listOf(ChangelogItem(noChangesText, ItemType.OTHER))
        }
    }

    androidx.compose.runtime.LaunchedEffect(downloadId) {
        if (downloadId != -1L) {
            while (true) {
                delay(500)
                downloadStatus = ApkInstaller.getDownloadStatus(context)
                if (downloadStatus is DownloadStatus.Completed || downloadStatus is DownloadStatus.Failed) break
            }
        }
    }

    SettingsPanelLayout(modifier = modifier) {
        SettingsGroup(title = stringResource(R.string.about_creator_title)) {
            SettingsRow(
                title = stringResource(R.string.about_creator_title),
                subtitle = stringResource(R.string.about_creator_subtitle),
                leadingIcon = KronoIcons.Status.Person
            )
        }

        SettingsGroup(title = stringResource(R.string.about_project_group_title)) {
            SettingsRow(
                title = stringResource(R.string.about_description_title),
                subtitle = stringResource(R.string.about_description),
                leadingIcon = KronoIcons.Status.Doc
            )
            SettingsDivider()
            SettingsRow(
                title = stringResource(R.string.about_source_title),
                subtitle = stringResource(R.string.about_source_subtitle),
                leadingIcon = KronoIcons.Status.Source,
                trailing = {
                    androidx.compose.material3.Icon(
                        imageVector = KronoIcons.Navigation.OpenExternal,
                        contentDescription = null
                    )
                },
                onClick = {
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_URL))
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                }
            )
            SettingsDivider()
            SettingsRow(
                title = stringResource(R.string.about_share_title),
                subtitle = stringResource(R.string.about_share_subtitle),
                leadingIcon = KronoIcons.Action.Share,
                trailing = {
                    androidx.compose.material3.Icon(
                        imageVector = KronoIcons.Navigation.ChevronRight,
                        contentDescription = null
                    )
                },
                onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, appName)
                        putExtra(Intent.EXTRA_TEXT, LATEST_APK_URL)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, shareChooser))
                }
            )
        }

        SettingsGroup(title = stringResource(R.string.updates_whats_new_title)) {
            Surface(
                shape = KronoTokens.Shape.progressBar,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(
                        R.string.updates_pill_current_version,
                        stringResource(R.string.version_prefix, activeInfo.tagName.removePrefix("v"))
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = KronoTokens.Spacing.md,
                            vertical = KronoTokens.Spacing.xs
                        ),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
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
            SettingsDivider()
            SettingsRow(
                title = when {
                    checking -> stringResource(R.string.updates_action_checking)
                    isDownloading -> stringResource(R.string.updates_action_downloading)
                    isDownloaded -> stringResource(R.string.updates_action_install)
                    upToDate -> stringResource(R.string.updates_action_updated)
                    hasNewVersion -> stringResource(R.string.updates_action_download)
                    else -> stringResource(R.string.updates_action_check)
                },
                subtitle = stringResource(R.string.updates_current_version_fallback),
                leadingIcon = KronoIcons.Status.Update,
                trailing = {
                    Icon(
                        imageVector = KronoIcons.Navigation.ChevronRight,
                        contentDescription = null
                    )
                },
                onClick = {
                    when {
                        isDownloaded -> ApkInstaller.installApk(context, activeInfo.tagName.removePrefix("v"))
                        upToDate || checking || isDownloading -> Unit
                        hasNewVersion && activeInfo.downloadUrl != null -> {
                            downloadId = ApkInstaller.startDownload(context, activeInfo.downloadUrl, activeInfo.tagName.removePrefix("v"))
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
                }
            )
        }

        SettingsGroup(title = stringResource(R.string.support_title)) {
            SettingsRow(
                title = stringResource(R.string.support_card_title),
                subtitle = if (totalLifetimeMs >= SUPPORT_MIN_USAGE_MS) {
                    stringResource(R.string.support_message_with_time, formattedTime)
                } else {
                    stringResource(R.string.support_project_subtitle)
                },
                leadingIcon = KronoIcons.Settings.Heart
            )
            Spacer(Modifier.height(KronoTokens.Settings.panelSectionGap))
            Button(
                onClick = { openKofi(context); onDonate() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = KronoTokens.Settings.panelHorizontalInset)
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
                    text = stringResource(R.string.support_button),
                    fontSize = KronoTokens.Typography.buttonLabel
                )
            }
        }

        BugReportPanelContent()
    }
}

private fun openKofi(context: Context) {
    context.startActivity(
        Intent(Intent.ACTION_VIEW, Uri.parse(KOFI_URL)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    )
}
