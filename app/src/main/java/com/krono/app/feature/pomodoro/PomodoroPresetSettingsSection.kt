package com.krono.app.feature.pomodoro

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krono.app.R
import com.krono.app.core.ui.components.SettingsRow
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoTokens

@Composable
internal fun PomodoroPresetSettingsSection(
    presets: List<PomodoroPresetConfig>,
    selectedPreset: PomodoroPresetConfig?,
    selectedPresetId: String,
    onPresetSelected: (String) -> Unit,
    onCreatePreset: () -> Unit,
    onEditPreset: (PomodoroPresetConfig) -> Unit,
    onDeletePreset: (PomodoroPresetConfig) -> Unit
) {
    PresetCard {
        PomodoroPresetDropdown(
            presets = presets,
            selectedPreset = selectedPreset,
            selectedPresetId = selectedPresetId,
            onPresetSelected = onPresetSelected,
            onCreatePreset = onCreatePreset,
            onEditPreset = onEditPreset,
            onDeletePreset = onDeletePreset
        )
    }
}

@Composable
private fun PresetCard(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = KronoTokens.Shape.card,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .border(
                    width = KronoTokens.Settings.dividerThickness,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = KronoTokens.Settings.dividerAlpha),
                    shape = KronoTokens.Shape.card
                )
                .padding(KronoTokens.Spacing.none)
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PomodoroPresetDropdown(
    presets: List<PomodoroPresetConfig>,
    selectedPreset: PomodoroPresetConfig?,
    selectedPresetId: String,
    onPresetSelected: (String) -> Unit,
    onCreatePreset: () -> Unit,
    onEditPreset: (PomodoroPresetConfig) -> Unit,
    onDeletePreset: (PomodoroPresetConfig) -> Unit
) {
    var open by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val title = stringResource(R.string.settings_subgroup_presets)
    val currentText = selectedPreset?.dropdownLabel() ?: selectedPresetId

    SettingsRow(
        title = title,
        leadingIcon = KronoIcons.Action.ListAlt,
        onClick = { open = true },
        trailing = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.xs)
            ) {
                Text(
                    text = currentText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    imageVector = if (open) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )

    if (open) {
        ModalBottomSheet(
            onDismissRequest = { open = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = KronoTokens.Spacing.lg, vertical = KronoTokens.Spacing.md)
            ) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = KronoTokens.Typography.statusLabel,
                        letterSpacing = 1.2.sp
                    ),
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = KronoTokens.Spacing.sm)
                )
                HorizontalDivider()

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp)
                ) {
                    items(presets, key = { preset -> preset.id }) { preset ->
                        PresetSheetRow(
                            preset = preset,
                            selected = preset.id == selectedPresetId,
                            onSelect = {
                                open = false
                                onPresetSelected(preset.id)
                            },
                            onEdit = {
                                open = false
                                onEditPreset(preset)
                            },
                            onDelete = {
                                open = false
                                onDeletePreset(preset)
                            }
                        )
                        HorizontalDivider()
                    }

                    item {
                        SettingsRow(
                            title = stringResource(R.string.pomodoro_create_preset_label),
                            leadingIcon = KronoIcons.Action.ListAltAdd,
                            onClick = {
                                open = false
                                onCreatePreset()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PresetSheetRow(
    preset: PomodoroPresetConfig,
    selected: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = KronoTokens.Component.rowMin)
            .padding(vertical = KronoTokens.Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.sm)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onSelect)
                .padding(vertical = KronoTokens.Spacing.sm)
        ) {
            Text(
                text = preset.dropdownLabel(),
                style = MaterialTheme.typography.bodyLarge,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        IconButton(onClick = onEdit) {
            Icon(
                imageVector = KronoIcons.Action.Settings,
                contentDescription = stringResource(R.string.action_edit),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(
            onClick = onDelete,
            enabled = !preset.isBuiltIn
        ) {
            Icon(
                imageVector = KronoIcons.Action.Delete,
                contentDescription = stringResource(R.string.action_delete),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                    alpha = if (preset.isBuiltIn) KronoTokens.Alpha.disabled else 1f
                )
            )
        }
    }
}

private fun PomodoroPresetConfig.dropdownLabel(): String = when (id) {
    PomodoroPresetCatalog.DEFAULT_ID -> "Padrão"
    PomodoroPresetCatalog.LONG_ID -> "Longo"
    PomodoroPresetCatalog.SHORT_ID -> "Curto"
    else -> name
}
