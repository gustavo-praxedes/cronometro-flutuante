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
import com.krono.app.core.util.UpdateInfo

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
    val appName = stringResource(R.string.app_name)
    val shareChooser = stringResource(R.string.about_share_chooser)
    val formattedTime = remember(totalLifetimeMs) { formatLifetimeDetailed(totalLifetimeMs) }

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

        UpdatesSettingsGroup(updateInfo = updateInfo, expandable = true)

        ExpandableSettingsGroup(
            title = stringResource(R.string.support_title),
            collapsedContent = {
                SettingsRow(
                    title = stringResource(R.string.support_card_title),
                    subtitle = if (totalLifetimeMs >= SUPPORT_MIN_USAGE_MS) {
                        stringResource(R.string.support_message_with_time, formattedTime)
                    } else {
                        stringResource(R.string.support_project_subtitle)
                    },
                    leadingIcon = KronoIcons.Settings.Heart
                )
            },
            expandedContent = { markInteraction ->
                SettingsDivider()
                Button(
                    onClick = {
                        markInteraction()
                        openKofi(context)
                        onDonate()
                    },
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
                Spacer(Modifier.height(KronoTokens.Settings.panelSectionGap))
            }
        )

        BugReportPanelContent(expandable = true)
    }
}

private fun openKofi(context: Context) {
    context.startActivity(
        Intent(Intent.ACTION_VIEW, Uri.parse(KOFI_URL)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    )
}
