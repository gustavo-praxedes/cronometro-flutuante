package com.krono.app.core.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.krono.app.BuildConfig
import com.krono.app.R
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
    isAnyToolRunning: () -> Boolean,
    onStartFocusMode: () -> Unit,
    onSupportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (destination) {
        SettingsDestination.Appearance -> AppearancePanel(dataStore = dataStore, modifier = modifier)
        SettingsDestination.Behavior -> BehaviorPanel(
            dataStore = dataStore,
            isServiceRunning = isServiceRunning,
            isAnyToolRunning = isAnyToolRunning,
            onStartFocusMode = onStartFocusMode,
            modifier = modifier
        )
        SettingsDestination.About -> {
            val updateInfo = pendingUpdateInfo ?: UpdateInfo(
                tagName = BuildConfig.VERSION_NAME,
                changelog = stringResource(R.string.updates_current_version_fallback),
                releaseUrl = "",
                downloadUrl = null
            )
            AboutPanel(
                totalLifetimeMs = totalLifetimeMs,
                updateInfo = updateInfo,
                onDonate = onSupportClick,
                modifier = modifier
            )
        }
        SettingsDestination.Stopwatch -> StopwatchSettings(dataStore = dataStore, modifier = modifier)
        SettingsDestination.Countdown -> CountdownSettings(dataStore = dataStore, modifier = modifier)
        SettingsDestination.Pomodoro -> PomodoroSettings(dataStore = dataStore, modifier = modifier)
    }
}
