package com.krono.app.core.ui.settings

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
                    modifier = Modifier.fillMaxWidth()
                )
            }

            SettingsDestination.Behavior -> {
                BehaviorPanel(
                    dataStore = dataStore,
                    isServiceRunning = isServiceRunning,
                    onStartFocusMode = onStartFocusMode,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            SettingsDestination.Overlay -> {
                OverlayPanel(
                    dataStore = dataStore,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            SettingsDestination.About -> {
                AboutPanel(
                    onSupportClick = onSupportClick,
                    onShowChangelog = onShowChangelog,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            SettingsDestination.Support -> {
                SupportPanel(
                    totalLifetimeMs = totalLifetimeMs,
                    onDonate = onSupportClick,
                    modifier = Modifier.fillMaxWidth()
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
                    modifier = Modifier.fillMaxWidth()
                )
            }

            SettingsDestination.Updates -> {
                if (pendingUpdateInfo != null) {
                    UpdatesPanel(
                        updateInfo = pendingUpdateInfo,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
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
                    modifier = Modifier.fillMaxWidth()
                )
            }

            SettingsDestination.Countdown -> {
            ToolSettingsPlaceholder(
                toolName = "Cronômetro Regressivo",
                modifier = Modifier.fillMaxWidth()
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