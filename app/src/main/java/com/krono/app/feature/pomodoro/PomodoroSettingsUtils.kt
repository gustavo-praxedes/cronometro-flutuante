package com.krono.app.feature.pomodoro

internal fun nextUserPresetIndex(presets: List<PomodoroPresetConfig>): Int {
    var index = 1
    while (presets.any { it.id == "USR_$index" }) index++
    return index
}

internal fun upsertPreset(
    presets: List<PomodoroPresetConfig>,
    preset: PomodoroPresetConfig
): List<PomodoroPresetConfig> {
    val mapped = presets.map {
        if (it.id == preset.id) preset else it
    }.toMutableList()
    if (mapped.none { it.id == preset.id }) mapped.add(preset)
    return mapped.sortedBy {
        when (it.id) {
            PomodoroPresetCatalog.DEFAULT_ID -> 0
            PomodoroPresetCatalog.LONG_ID -> 1
            PomodoroPresetCatalog.SHORT_ID -> 2
            else -> 100
        }
    }
}

internal fun deletePreset(
    presets: List<PomodoroPresetConfig>,
    presetId: String
): List<PomodoroPresetConfig> =
    presets.filterNot { it.id == presetId && !it.isBuiltIn }
        .ifEmpty { PomodoroPresetCatalog.defaults() }

internal fun selectedPresetIdAfterDelete(
    presets: List<PomodoroPresetConfig>,
    updatedPresets: List<PomodoroPresetConfig>,
    deletedPresetId: String
): String {
    val deletedIndex = presets.indexOfFirst { it.id == deletedPresetId }
    val previousId = presets.getOrNull(deletedIndex - 1)?.id
    return when {
        previousId != null && updatedPresets.any { it.id == previousId } -> previousId
        updatedPresets.any { it.id == PomodoroPresetCatalog.DEFAULT_ID } -> PomodoroPresetCatalog.DEFAULT_ID
        else -> updatedPresets.firstOrNull()?.id ?: PomodoroPresetCatalog.DEFAULT_ID
    }
}

internal fun formatAsHhMmSs(totalSeconds: Long): String {
    val safe = totalSeconds.coerceAtLeast(0L)
    val hh = (safe / 3600L).toInt()
    val mm = ((safe % 3600L) / 60L).toInt()
    val ss = (safe % 60L).toInt()
    return "%02d:%02d:%02d".format(hh, mm, ss)
}
