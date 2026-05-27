package com.krono.app.core.ui.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import com.krono.app.R
import com.krono.app.core.data.OverlayConfig
import com.krono.app.core.data.OverlayDataStore
import com.krono.app.core.ui.theme.KronoTokens
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.components.SettingsDivider
import com.krono.app.core.ui.components.KronoDropdown
import com.krono.app.core.ui.components.ToggleRow
import kotlinx.coroutines.launch

@Composable
fun BehaviorPanel(
    dataStore: OverlayDataStore,
    isServiceRunning: () -> Boolean,
    isAnyToolRunning: () -> Boolean,
    onStartFocusMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val config = dataStore.configFlow.collectAsState<OverlayConfig, OverlayConfig?>(initial = null).value ?: return
    val scope = rememberCoroutineScope()

    SettingsPanelLayout(modifier = modifier) {
        SettingsGroup(title = stringResource(R.string.settings_subgroup_general)) {
            val stopwatchLabel = stringResource(R.string.nav_stopwatch)
            val countdownLabel = stringResource(R.string.nav_countdown)
            val pomodoroLabel = stringResource(R.string.nav_pomodoro)
            ToggleRow(
                label = stringResource(R.string.behavior_auto_launch_label),
                subtitle = stringResource(R.string.behavior_auto_launch_subtitle),
                checked = config.autoLaunch,
                leadingIcon = KronoIcons.Feature.Timer,
                onChange = { enabled ->
                    scope.launch {
                        dataStore.updateConfig { current ->
                            current.copy(
                                autoLaunch = enabled,
                                directLaunchToolId = current.directLaunchToolId.ifBlank { current.activeToolId }
                            )
                        }
                    }
                }
            )
            if (config.autoLaunch) {
                SettingsDivider()
                KronoDropdown(
                    value = config.directLaunchToolId,
                    onValueChange = { toolId ->
                        scope.launch { dataStore.updateConfig { it.copy(directLaunchToolId = toolId) } }
                    },
                    options = listOf("stopwatch", "countdown", "pomodoro"),
                    label = stringResource(R.string.behavior_direct_launch_tool_label),
                    leadingIcon = KronoIcons.Feature.Overlay,
                    modifier = Modifier.padding(start = KronoTokens.Spacing.xl),
                    textMapping = { toolId ->
                        when (toolId) {
                            "countdown" -> countdownLabel
                            "pomodoro" -> pomodoroLabel
                            else -> stopwatchLabel
                        }
                    }
                )
            }
            SettingsDivider()
            ToggleRow(
                label = stringResource(R.string.behavior_open_overlay_on_play_label),
                subtitle = stringResource(R.string.behavior_open_overlay_on_play_subtitle),
                checked = config.openOverlayOnPlay,
                leadingIcon = KronoIcons.Feature.Overlay,
                onChange = { enabled ->
                    scope.launch { dataStore.updateConfig { it.copy(openOverlayOnPlay = enabled) } }
                }
            )
            SettingsDivider()
            ToggleRow(
                label = stringResource(R.string.settings_all_sounds_label),
                subtitle = stringResource(R.string.settings_all_sounds_subtitle),
                checked = config.allSoundsEnabled,
                leadingIcon = if (config.allSoundsEnabled) KronoIcons.Action.Notification else KronoIcons.Action.NotificationOff,
                onChange = { enabled ->
                    scope.launch { dataStore.updateConfig { it.copy(allSoundsEnabled = enabled) } }
                }
            )
            SettingsDivider()
            ToggleRow(
                label = stringResource(R.string.label_wake_lock),
                subtitle = stringResource(R.string.behavior_keep_screen_subtitle),
                checked = config.keepScreenOn,
                leadingIcon = KronoIcons.Action.Light,
                onChange = { enabled ->
                    scope.launch { dataStore.updateConfig { it.copy(keepScreenOn = enabled) } }
                }
            )

            SettingsDivider()

            ToggleRow(
                label = stringResource(R.string.label_focus_mode),
                subtitle = stringResource(R.string.behavior_focus_mode_subtitle),
                checked = config.focusModeEnabled,
                leadingIcon = KronoIcons.Action.Focus,
                onChange = { isEnabled ->
                    scope.launch { dataStore.updateConfig { it.copy(focusModeEnabled = isEnabled) } }
                    if (isEnabled && isAnyToolRunning()) onStartFocusMode()
                }
            )
            if (config.focusModeEnabled) {
                SettingsDivider()
                ToggleRow(
                    label = stringResource(R.string.pomodoro_dnd_label),
                    subtitle = stringResource(R.string.pomodoro_dnd_subtitle),
                    checked = config.pomodoroDndDuringFocus,
                    leadingIcon = KronoIcons.Action.Focus,
                    modifier = Modifier.padding(start = KronoTokens.Spacing.xl),
                    onChange = { enabled ->
                        scope.launch { dataStore.updateConfig { it.copy(pomodoroDndDuringFocus = enabled) } }
                    }
                )
            }
        }
    }
}

