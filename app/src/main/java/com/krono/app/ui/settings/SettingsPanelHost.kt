package com.krono.app.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krono.app.core.data.OverlayConfig
import com.krono.app.core.data.OverlayDataStore
import com.krono.app.core.util.UpdateInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SettingsPanelHost(
    destination: SettingsDestination,
    config: OverlayConfig,
    dataStore: OverlayDataStore,
    scope: CoroutineScope,
    totalLifetimeMs: Long,
    pendingUpdateInfo: UpdateInfo?,
    isServiceRunning: () -> Boolean,
    onStartFocusMode: () -> Unit,
    onSupportClick: () -> Unit,
    onShowChangelog: (UpdateInfo) -> Unit,
    onUpdateAvailable: (UpdateInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    when (destination) {
        SettingsDestination.Appearance -> {
            AppearancePanel(
                dataStore = dataStore,
                modifier = modifier
            )
        }

        SettingsDestination.Behavior -> {
            BehaviorPanel(
                dataStore = dataStore,
                isServiceRunning = isServiceRunning,
                onStartFocusMode = onStartFocusMode,
                modifier = modifier
            )
        }

        SettingsDestination.Overlay -> {
            OverlayPanel(
                dataStore = dataStore,
                modifier = modifier
            )
        }

        SettingsDestination.About -> {
            AboutPanel(
                onSupportClick = onSupportClick,
                onShowChangelog = onShowChangelog,
                modifier = modifier
            )
        }

        SettingsDestination.Support -> {
            SupportPanel(
                totalLifetimeMs = totalLifetimeMs,
                onDonate = onSupportClick,
                modifier = modifier
            )
        }

        SettingsDestination.Changelog -> {
            val updateInfo = pendingUpdateInfo ?: UpdateInfo(
                tagName = "",
                changelog = "",
                releaseUrl = "",
                downloadUrl = null
            )
            ChangelogPanel(
                updateInfo = updateInfo,
                onUpdateAvailable = onUpdateAvailable,
                modifier = modifier
            )
        }

        SettingsDestination.Updates -> {
            if (pendingUpdateInfo != null) {
                UpdatesPanel(
                    updateInfo = pendingUpdateInfo,
                    modifier = modifier
                )
            } else {
                Box(
                    modifier = modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhuma atualização disponível",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        SettingsDestination.Stopwatch -> {
            ToolSettingsPlaceholder(
                toolName = "Cronômetro",
                modifier = modifier
            )
        }

        SettingsDestination.Countdown -> {
            ToolSettingsPlaceholder(
                toolName = "Cronômetro Regressivo",
                modifier = modifier
            )
        }
    }
}

@Composable
private fun ToolSettingsPlaceholder(
    toolName: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Configuração",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "$toolName - Em desenvolvimento",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}