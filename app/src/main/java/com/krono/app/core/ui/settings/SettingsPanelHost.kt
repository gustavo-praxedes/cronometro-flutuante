package com.krono.app.core.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.krono.app.core.data.OverlayConfig
import com.krono.app.core.data.OverlayDataStore
import com.krono.app.core.util.UpdateInfo
import com.krono.app.feature.countdown.CountdownSettings
import com.krono.app.feature.pomodoro.PomodoroSettings
import com.krono.app.feature.stopwatch.StopwatchSettings
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
        SettingsDestination.About -> AboutPanel(modifier = modifier)
        SettingsDestination.Support -> SupportPanel(
            totalLifetimeMs = totalLifetimeMs,
            onDonate = onSupportClick,
            modifier = modifier
        )
        SettingsDestination.Changelog -> {
            val updateInfo = pendingUpdateInfo ?: UpdateInfo(
                tagName = "vAtual",
                changelog = "Versao atual instalada. Sem atualizacao pendente.",
                releaseUrl = "",
                downloadUrl = null
            )
            UpdatesPanel(updateInfo = updateInfo, modifier = modifier)
        }
        SettingsDestination.BugReport -> BugReportPanel(modifier = modifier)
        SettingsDestination.Stopwatch -> StopwatchSettings(dataStore = dataStore, modifier = modifier)
        SettingsDestination.Countdown -> CountdownSettings(dataStore = dataStore, modifier = modifier)
        SettingsDestination.Pomodoro -> PomodoroSettings(dataStore = dataStore, modifier = modifier)
    }
}
