package com.krono.app.core.ui.settings

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
import com.krono.app.core.ui.components.ToggleRow
import kotlinx.coroutines.launch

@Composable
fun BehaviorPanel(
    dataStore: OverlayDataStore,
    isServiceRunning: () -> Boolean,
    onStartFocusMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val config = dataStore.configFlow.collectAsState(initial = OverlayConfig()).value
    val scope = rememberCoroutineScope()

    SettingsPanelLayout(modifier = modifier) {
        SettingsGroup(title = stringResource(R.string.settings_subgroup_general)) {
            ToggleRow(
                label = stringResource(R.string.behavior_auto_launch_label),
                subtitle = stringResource(R.string.behavior_auto_launch_subtitle),
                checked = config.autoLaunch,
                leadingIcon = KronoIcons.Feature.Timer,
                onChange = {
                    scope.launch { dataStore.updateConfig(config.copy(autoLaunch = it)) }
                }
            )
            SettingsDivider()
            ToggleRow(
                label = stringResource(R.string.label_wake_lock),
                subtitle = stringResource(R.string.behavior_keep_screen_subtitle),
                checked = config.keepScreenOn,
                leadingIcon = KronoIcons.Action.Light,
                onChange = {
                    scope.launch { dataStore.updateConfig(config.copy(keepScreenOn = it)) }
                }
            )

            SettingsDivider()

            ToggleRow(
                label = stringResource(R.string.label_focus_mode),
                subtitle = stringResource(R.string.behavior_focus_mode_subtitle),
                checked = config.focusModeEnabled,
                leadingIcon = KronoIcons.Action.Focus,
                onChange = { isEnabled ->
                    scope.launch { dataStore.updateConfig(config.copy(focusModeEnabled = isEnabled)) }
                    if (isEnabled && isServiceRunning()) onStartFocusMode()
                }
            )
        }
    }
}

