package com.krono.app.core.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.krono.app.R
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoTokens

@Composable
fun SettingsMenuPanel(
    selectedDestination: SettingsDestination?,
    onDestinationSelected: (SettingsDestination) -> Unit,
    hasPendingUpdate: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(KronoTokens.Spacing.xl))

        Text(
            text = stringResource(R.string.settings_section_general),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(
                top = KronoTokens.Spacing.lg,
                start = KronoTokens.Spacing.lg,
                bottom = KronoTokens.Spacing.sm
            )
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = KronoTokens.Spacing.lg)
                .clip(KronoTokens.Shape.card),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            tonalElevation = 1.dp
        ) {
            Column {
                SettingsItem(
                    title = stringResource(R.string.settings_appearance),
                    icon = KronoIcons.Settings.Appearance,
                    selected = selectedDestination == SettingsDestination.Appearance,
                    onClick = { onDestinationSelected(SettingsDestination.Appearance) }
                )

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )

                SettingsItem(
                    title = stringResource(R.string.settings_behavior),
                    icon = KronoIcons.Settings.Behavior,
                    selected = selectedDestination == SettingsDestination.Behavior,
                    onClick = { onDestinationSelected(SettingsDestination.Behavior) }
                )

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )

                SettingsItem(
                    title = stringResource(R.string.settings_overlay),
                    icon = KronoIcons.Settings.Overlay,
                    selected = selectedDestination == SettingsDestination.Overlay,
                    onClick = { onDestinationSelected(SettingsDestination.Overlay) }
                )
            }
        }

        Spacer(modifier = Modifier.height(KronoTokens.Spacing.lg))

        Text(
            text = stringResource(R.string.settings_section_tools),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(
                top = KronoTokens.Spacing.lg,
                start = KronoTokens.Spacing.lg,
                bottom = KronoTokens.Spacing.sm
            )
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = KronoTokens.Spacing.lg)
                .clip(KronoTokens.Shape.card),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            tonalElevation = 1.dp
        ) {
            Column {
                SettingsItem(
                    title = stringResource(R.string.settings_stopwatch),
                    icon = KronoIcons.Feature.Timer,
                    selected = selectedDestination == SettingsDestination.Stopwatch,
                    onClick = { onDestinationSelected(SettingsDestination.Stopwatch) }
                )

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )

                SettingsItem(
                    title = stringResource(R.string.settings_countdown),
                    icon = KronoIcons.Feature.Countdown,
                    selected = selectedDestination == SettingsDestination.Countdown,
                    onClick = { onDestinationSelected(SettingsDestination.Countdown) }
                )
            }
        }

        Spacer(modifier = Modifier.height(KronoTokens.Spacing.lg))

        Text(
            text = stringResource(R.string.settings_section_info),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(
                top = KronoTokens.Spacing.lg,
                start = KronoTokens.Spacing.lg,
                bottom = KronoTokens.Spacing.sm
            )
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = KronoTokens.Spacing.lg)
                .clip(KronoTokens.Shape.card),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            tonalElevation = 1.dp
        ) {
            Column {
                SettingsItem(
                    title = stringResource(R.string.settings_about),
                    icon = KronoIcons.Settings.Info,
                    selected = selectedDestination == SettingsDestination.About,
                    onClick = { onDestinationSelected(SettingsDestination.About) }
                )

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )

                SettingsItem(
                    title = stringResource(R.string.settings_support),
                    icon = KronoIcons.Settings.Heart,
                    selected = selectedDestination == SettingsDestination.Support,
                    onClick = { onDestinationSelected(SettingsDestination.Support) }
                )

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )

                SettingsItem(
                    title = stringResource(R.string.settings_changelog),
                    icon = KronoIcons.Settings.History,
                    selected = selectedDestination == SettingsDestination.Changelog,
                    onClick = { onDestinationSelected(SettingsDestination.Changelog) }
                )

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )

                SettingsItemWithBadge(
                    title = stringResource(R.string.settings_updates),
                    icon = KronoIcons.Settings.Update,
                    selected = selectedDestination == SettingsDestination.Updates,
                    hasBadge = hasPendingUpdate,
                    onClick = { onDestinationSelected(SettingsDestination.Updates) }
                )
            }
        }

        Spacer(modifier = Modifier.height(KronoTokens.Spacing.xxl))
    }
}

@Composable
private fun SettingsItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .then(
                if (selected) {
                    Modifier.border(
                        BorderStroke(3.dp, MaterialTheme.colorScheme.primary),
                        KronoTokens.Shape.card
                    )
                } else Modifier
            ),
        color = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface,
        shape = KronoTokens.Shape.card
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = KronoTokens.Spacing.lg,
                    vertical = KronoTokens.Spacing.md
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(KronoTokens.Icon.listItem)
            )

            Spacer(modifier = Modifier.width(KronoTokens.Spacing.md))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (selected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = if (selected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(KronoTokens.Icon.small)
            )
        }
    }
}

@Composable
private fun SettingsItemWithBadge(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    hasBadge: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .then(
                if (selected) {
                    Modifier.border(
                        BorderStroke(3.dp, MaterialTheme.colorScheme.primary),
                        KronoTokens.Shape.card
                    )
                } else Modifier
            ),
        color = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface,
        shape = KronoTokens.Shape.card
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = KronoTokens.Spacing.lg,
                    vertical = KronoTokens.Spacing.md
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(KronoTokens.Icon.listItem)
            )

            Spacer(modifier = Modifier.width(KronoTokens.Spacing.md))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (selected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.weight(1f))

            if (hasBadge) {
                Badge(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Text(
                        text = "!",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(modifier = Modifier.width(KronoTokens.Spacing.sm))

            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = if (selected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(KronoTokens.Icon.small)
            )
        }
    }
}