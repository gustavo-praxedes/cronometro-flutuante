package com.krono.app.core.ui.settings

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.krono.app.R
import com.krono.app.core.ui.theme.KronoIcons

sealed class SettingsDestination(
    @StringRes val titleRes: Int,
    val icon: ImageVector
) {
    data object Appearance : SettingsDestination(
        titleRes = R.string.settings_appearance,
        icon = KronoIcons.Settings.Appearance
    )

    data object Behavior : SettingsDestination(
        titleRes = R.string.settings_behavior,
        icon = KronoIcons.Settings.Behavior
    )

    data object Stopwatch : SettingsDestination(
        titleRes = R.string.settings_stopwatch,
        icon = KronoIcons.Feature.Timer
    )

    data object Countdown : SettingsDestination(
        titleRes = R.string.settings_countdown,
        icon = KronoIcons.Feature.Countdown
    )
    data object Pomodoro : SettingsDestination(
        titleRes = R.string.settings_pomodoro,
        icon = KronoIcons.Feature.Pomodoro
    )

    data object About : SettingsDestination(
        titleRes = R.string.settings_about,
        icon = KronoIcons.Settings.Info
    )

    data object Support : SettingsDestination(
        titleRes = R.string.settings_support,
        icon = KronoIcons.Settings.Heart
    )

    data object Changelog : SettingsDestination(
        titleRes = R.string.settings_changelog,
        icon = KronoIcons.Settings.History
    )

    data object BugReport : SettingsDestination(
        titleRes = R.string.settings_bug_report,
        icon = KronoIcons.Status.Bug
    )
}
