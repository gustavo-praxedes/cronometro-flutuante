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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krono.app.BuildConfig
import com.krono.app.R
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoTokens

private const val GITHUB_URL = "https://github.com/gustavo-praxedes/krono"
private const val LATEST_APK_URL = "https://github.com/gustavo-praxedes/krono/releases/latest/download/krono.apk"

@Composable
fun AboutPanel(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val appName = stringResource(R.string.app_name)
    val shareChooser = stringResource(R.string.about_share_chooser)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = KronoTokens.Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(KronoTokens.Spacing.xl))

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
                    text = appName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Normal,
                    fontSize = KronoTokens.Typography.dialogTitle
                )
                Spacer(Modifier.height(KronoTokens.Spacing.xs))
                Text(
                    text = stringResource(R.string.about_version, BuildConfig.VERSION_NAME),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = KronoTokens.Typography.statusLabel),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Normal
                )
                Spacer(Modifier.height(KronoTokens.Spacing.sm))
                Text(
                    text = stringResource(R.string.about_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }
        }

        Spacer(Modifier.height(KronoTokens.Spacing.xl))

        SettingsGroup(title = stringResource(R.string.about_project_group_title)) {
            AboutActionRow(
                icon = KronoIcons.Status.Source,
                iconColor = Color(0xFF6B7FD4),
                title = stringResource(R.string.about_source_title),
                subtitle = stringResource(R.string.about_source_subtitle),
                trailing = KronoIcons.Navigation.OpenExternal,
                onClick = {
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_URL))
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                }
            )

            RowDivider()

            AboutActionRow(
                icon = KronoIcons.Action.Share,
                iconColor = Color(0xFF10B981),
                title = stringResource(R.string.about_share_title),
                subtitle = stringResource(R.string.about_share_subtitle),
                trailing = KronoIcons.Navigation.ChevronRight,
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

        Spacer(Modifier.height(KronoTokens.Spacing.xxl))
    }
}

@Composable
private fun AboutActionRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    trailing: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = KronoTokens.Spacing.lg, vertical = KronoTokens.Spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = iconColor)
        }
        Spacer(Modifier.width(KronoTokens.Spacing.md))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = KronoTokens.Typography.statusLabel),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = trailing,
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


