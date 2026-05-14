package com.krono.app.core.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.krono.app.core.data.OverlayConfig
import com.krono.app.core.data.OverlayDataStore
import com.krono.app.core.ui.theme.KronoTokens
import com.krono.app.core.util.UpdateInfo
import com.krono.app.feature.pomodoro.PomodoroSettings
import kotlinx.coroutines.CoroutineScope

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
        SettingsDestination.Appearance -> AppearancePanel(dataStore = dataStore, modifier = modifier)
        SettingsDestination.Behavior -> BehaviorPanel(
            dataStore = dataStore,
            isServiceRunning = isServiceRunning,
            onStartFocusMode = onStartFocusMode,
            modifier = modifier
        )
        SettingsDestination.Overlay -> OverlayPanel(dataStore = dataStore, modifier = modifier)
        SettingsDestination.About -> AboutPanel(modifier = modifier)
        SettingsDestination.Support -> SupportPanel(
            totalLifetimeMs = totalLifetimeMs,
            onDonate = onSupportClick,
            modifier = modifier
        )
        SettingsDestination.Changelog -> {
            val updateInfo = pendingUpdateInfo ?: UpdateInfo(
                tagName = "vAtual",
                changelog = "VersÃ£o atual instalada. Sem atualizaÃ§Ã£o pendente.",
                releaseUrl = "",
                downloadUrl = null
            )
            UpdatesPanel(updateInfo = updateInfo, modifier = modifier)
        }
        SettingsDestination.BugReport -> BugReportPanel(modifier = modifier)
        SettingsDestination.Stopwatch -> ToolSettingsPlaceholder("CronÃ´metro", modifier)
        SettingsDestination.Countdown -> ToolSettingsPlaceholder("Contagem Regressiva", modifier)
        SettingsDestination.Pomodoro -> PomodoroSettings()
    }
}

@Composable
private fun ToolSettingsPlaceholder(toolName: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(text = toolName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Em desenvolvimento",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = KronoTokens.Typography.listItem),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


