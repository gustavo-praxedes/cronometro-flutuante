package com.krono.app.core.ui.settings

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.krono.app.R
import com.krono.app.core.ui.theme.KronoIcons

sealed class SettingsDestination(
    @StringRes val titleRes: Int,
    val icon: ImageVector,
    val subtitle: String
) {
    data object Appearance : SettingsDestination(
        titleRes = R.string.settings_appearance,
        icon = KronoIcons.Settings.Appearance,
        subtitle = "Temas, cores e fontes"
    )

    data object Behavior : SettingsDestination(
        titleRes = R.string.settings_behavior,
        icon = KronoIcons.Settings.Behavior,
        subtitle = "Auto-início, botões e limites"
    )

    data object Overlay : SettingsDestination(
        titleRes = R.string.settings_overlay,
        icon = KronoIcons.Settings.Overlay,
        subtitle = "Escala, raio e posição"
    )

    data object Stopwatch : SettingsDestination(
        titleRes = R.string.settings_stopwatch,
        icon = KronoIcons.Feature.Timer,
        subtitle = "Configurações do cronômetro"
    )

    data object Countdown : SettingsDestination(
        titleRes = R.string.settings_countdown,
        icon = KronoIcons.Feature.Countdown,
        subtitle = "Configurações da contagem regressiva"
    )

    data object About : SettingsDestination(
        titleRes = R.string.settings_about,
        icon = KronoIcons.Settings.Info,
        subtitle = "Informações e links do projeto"
    )

    data object Support : SettingsDestination(
        titleRes = R.string.settings_support,
        icon = KronoIcons.Settings.Heart,
        subtitle = "Apoie o desenvolvimento"
    )

    data object Changelog : SettingsDestination(
        titleRes = R.string.settings_changelog,
        icon = KronoIcons.Settings.History,
        subtitle = "Novidades desta versão"
    )

    data object Updates : SettingsDestination(
        titleRes = R.string.settings_updates,
        icon = KronoIcons.Settings.Update,
        subtitle = "Verificar atualizações disponíveis"
    )
}
