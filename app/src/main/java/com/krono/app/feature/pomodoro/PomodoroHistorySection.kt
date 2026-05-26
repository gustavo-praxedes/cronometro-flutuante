package com.krono.app.feature.pomodoro

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.krono.app.R
import com.krono.app.core.ui.components.SettingsDivider
import com.krono.app.core.ui.components.SettingsRow
import com.krono.app.core.ui.settings.SettingsGroup
import com.krono.app.core.ui.theme.KronoIcons
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
internal fun PomodoroHistorySection(
    rawHistory: String,
    presets: List<PomodoroPresetConfig>
) {
    SettingsGroup(title = stringResource(R.string.pomodoro_history_title)) {
        val historyCycleTemplate = stringResource(R.string.pomodoro_history_cycles)
        val fallbackPresetName = stringResource(R.string.nav_pomodoro)
        val historyItems = remember(rawHistory, presets, historyCycleTemplate, fallbackPresetName) {
            parsePomodoroHistory(
                raw = rawHistory,
                presets = presets,
                cyclesTemplate = historyCycleTemplate,
                fallbackPresetName = fallbackPresetName
            )
        }
        if (historyItems.isEmpty()) {
            SettingsRow(
                title = stringResource(R.string.pomodoro_history_empty),
                subtitle = stringResource(R.string.pomodoro_history_empty_subtitle),
                leadingIcon = KronoIcons.Action.ListAlt
            )
        } else {
            historyItems.take(3).forEachIndexed { index, item ->
                if (index > 0) SettingsDivider()
                SettingsRow(
                    title = item.title,
                    subtitle = item.subtitle,
                    leadingIcon = KronoIcons.Action.ListAlt
                )
            }
        }
    }
}

private data class PomodoroHistoryItem(
    val title: String,
    val subtitle: String
)

private fun parsePomodoroHistory(
    raw: String,
    presets: List<PomodoroPresetConfig>,
    cyclesTemplate: String,
    fallbackPresetName: String
): List<PomodoroHistoryItem> {
    if (raw.isBlank()) return emptyList()
    val dateFormat = SimpleDateFormat("dd/MM HH:mm", Locale("pt", "BR"))
    return raw.split(";").mapNotNull { row ->
        val parts = row.split("|")
        val timestamp = parts.getOrNull(0)?.toLongOrNull() ?: return@mapNotNull null
        val presetId = parts.getOrNull(1).orEmpty()
        val cycles = parts.getOrNull(2)?.toIntOrNull()?.coerceAtLeast(1) ?: 1
        val presetName = presets.firstOrNull { it.id == presetId }?.name ?: presetId.ifBlank { fallbackPresetName }
        PomodoroHistoryItem(
            title = presetName,
            subtitle = "${dateFormat.format(Date(timestamp))} - ${String.format(cyclesTemplate, cycles)}"
        )
    }
}
