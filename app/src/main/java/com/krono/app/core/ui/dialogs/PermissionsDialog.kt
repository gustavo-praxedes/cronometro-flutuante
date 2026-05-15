package com.krono.app.core.ui.dialogs

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.krono.app.core.ui.theme.KronoIcons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.krono.app.core.ui.theme.KronoTokens
import com.krono.app.R
import com.krono.app.core.ui.theme.adaptiveDialogWidth
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionsDialog(
    hasNotificationPermission : Boolean,
    hasOverlayPermission      : Boolean,
    hasInstallPermission      : Boolean,
    onRequestNotification     : () -> Unit,
    onRequestOverlay          : () -> Unit,
    onRequestInstall          : () -> Unit,
    onDismiss                 : () -> Unit
) {
    // Overlay só libera com notificação + overlay. Install é opcional.
    val coreGranted = hasOverlayPermission &&
            (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || hasNotificationPermission)

    // Removido o LaunchedEffect de fechamento automático para o usuário ver o check verde.

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier         = Modifier.adaptiveDialogWidth()
    ) {
        Surface(
            modifier       = Modifier.fillMaxWidth(),
            shape          = KronoTokens.Shape.dialog,
            color          = MaterialTheme.colorScheme.surface,
            tonalElevation = KronoTokens.Elevation.dialog
        ) {
            Column(
                modifier            = Modifier
                    .fillMaxWidth()
                    .padding(KronoTokens.Spacing.dialogPadding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ── Cabeçalho ────────────────────────────────
                Box(
                    modifier         = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                     Text(
                         text       = stringResource(R.string.permissions_title),
                         style      = MaterialTheme.typography.headlineSmall.copy(
                             platformStyle = PlatformTextStyle(includeFontPadding = false)
                         ),
                         fontWeight = FontWeight.Normal,
                         fontSize   = KronoTokens.Typography.dialogTitle,
                         textAlign  = TextAlign.Center,
                         modifier   = Modifier.padding(horizontal = KronoTokens.Spacing.dialogPadding)
                     )

                    IconButton(
                        onClick  = onDismiss,
                        modifier = Modifier
                            .size(KronoTokens.Icon.close)
                            .align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            imageVector        = KronoIcons.Navigation.Close,
                            contentDescription = stringResource(R.string.action_close),
                            tint               = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(KronoTokens.Spacing.sectionGap))

                Text(
                    text      = stringResource(R.string.permissions_subtitle),
                    style     = MaterialTheme.typography.bodyMedium.copy(
                        platformStyle = PlatformTextStyle(includeFontPadding = false)
                    ),
                    fontSize  = KronoTokens.Typography.bodyText,
                    textAlign = TextAlign.Center,
                    color     = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(KronoTokens.Spacing.sectionGap))

                // ── Permissão: Notificações (Android 13+) ────
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    PermissionItem(
                        icon        = KronoIcons.Action.Notification,
                        title       = stringResource(R.string.permissions_notifications_title),
                        description = stringResource(R.string.permissions_notifications_desc),
                        granted     = hasNotificationPermission,
                        optional    = false,
                        onClick     = onRequestNotification
                    )
                    Spacer(Modifier.height(KronoTokens.Spacing.md))
                }

                // ── Permissão: Overlay ────────────────────────
                PermissionItem(
                    icon        = KronoIcons.Action.Settings,
                    title       = stringResource(R.string.permissions_overlay_title),
                    description = stringResource(R.string.permissions_overlay_desc),
                    granted     = hasOverlayPermission,
                    optional    = false,
                    onClick     = onRequestOverlay
                )

                Spacer(Modifier.height(KronoTokens.Spacing.md))

                // ── Permissão: Instalar APK (opcional) ───────
                PermissionItem(
                    icon        = KronoIcons.Action.Download,
                    title       = stringResource(R.string.permissions_install_title),
                    description = stringResource(R.string.permissions_install_desc),
                    granted     = hasInstallPermission,
                    optional    = true,
                    onClick     = onRequestInstall
                )

                Spacer(Modifier.height(KronoTokens.Spacing.sectionGap))

                 // ── Botão Concluir (aparece quando core OK) ───
                 AnimatedVisibility(
                     visible = coreGranted,
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
                    Button(
                        onClick  = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(KronoTokens.Button.height),
                        shape = KronoTokens.Shape.button
                    ) {
                        Icon(
                            imageVector        = KronoIcons.Action.Check,
                            contentDescription = null,
                            modifier           = Modifier.size(KronoTokens.Icon.button)
                        )
                        Spacer(Modifier.width(KronoTokens.Button.iconSpacing))
                        Text(
                            text       = stringResource(R.string.action_done),
                            fontSize   = KronoTokens.Typography.buttonLabel,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PermissionItem(
    icon       : ImageVector,
    title      : String,
    description: String,
    granted    : Boolean,
    optional   : Boolean,
    onClick    : () -> Unit
) {
    val containerColor = when {
        granted  -> MaterialTheme.colorScheme.primaryContainer
        optional -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        else     -> MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = if (granted)
        MaterialTheme.colorScheme.onPrimaryContainer
    else
        MaterialTheme.colorScheme.onSurfaceVariant

    val checkTint = if (granted) Color(0xFF10B981) else MaterialTheme.colorScheme.outline

    Surface(
        onClick        = { if (!granted) onClick() },
        enabled        = !granted,
        shape          = KronoTokens.Shape.card,
        color          = containerColor,
        tonalElevation = KronoTokens.Elevation.flat,
        modifier       = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(KronoTokens.Spacing.lg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = if (granted) Color(0xFF10B981) else contentColor,
                modifier           = Modifier.size(KronoTokens.Icon.dialogHeader)
            )

            Spacer(Modifier.width(KronoTokens.Spacing.md))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text       = title,
                        style      = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Normal,
                        color      = MaterialTheme.colorScheme.onSurface
                    )
                    if (optional) {
                        Spacer(Modifier.width(KronoTokens.Spacing.xs))
                        Text(
                            text  = stringResource(R.string.permissions_optional),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
                Text(
                    text  = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor
                )
            }

            Spacer(Modifier.width(KronoTokens.Spacing.md))

            Icon(
                imageVector        = if (granted) KronoIcons.Action.Check else KronoIcons.Status.Unchecked,
                contentDescription = null,
                tint               = checkTint,
                modifier           = Modifier.size(KronoTokens.Icon.status)
            )
        }
    }
}


