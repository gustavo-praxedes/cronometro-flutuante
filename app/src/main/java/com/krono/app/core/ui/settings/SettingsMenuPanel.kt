package com.krono.app.core.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krono.app.R
import com.krono.app.core.ui.components.SettingsDivider
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoTokens

// ── Icon accent colors per destination ───────────────────────
private fun SettingsDestination.accentColor(): Color = when (this) {
    SettingsDestination.Appearance  -> Color(0xFF6B7FD4)
    SettingsDestination.Behavior    -> Color(0xFFF59E0B)
    SettingsDestination.Stopwatch   -> Color(0xFF10B981)
    SettingsDestination.Countdown   -> Color(0xFF06B6D4)
    SettingsDestination.Pomodoro    -> Color(0xFFE11D48)
    SettingsDestination.About       -> Color(0xFF3B82F6)
    SettingsDestination.Changelog   -> Color(0xFFF97316)
    SettingsDestination.BugReport   -> Color(0xFFE11D48)
}

// ── Section data ──────────────────────────────────────────────
private data class MenuSection(
    val titleRes: Int,
    val destinations: List<SettingsDestination>
)

private val menuSections = listOf(
    MenuSection(
        titleRes = R.string.settings_menu_section_general,
        destinations = listOf(
            SettingsDestination.Appearance,
            SettingsDestination.Behavior
        )
    ),
    MenuSection(
        titleRes = R.string.settings_menu_section_tools,
        destinations = listOf(
            SettingsDestination.Stopwatch,
            SettingsDestination.Countdown,
            SettingsDestination.Pomodoro
        )
    ),
    MenuSection(
        titleRes = R.string.settings_menu_section_project,
        destinations = listOf(
            SettingsDestination.About,
            SettingsDestination.Changelog,
            SettingsDestination.BugReport
        )
    )
)

@Composable
fun SettingsMenuPanel(
    selectedDestination: SettingsDestination?,
    onDestinationSelected: (SettingsDestination) -> Unit,
    hasPendingUpdate: Boolean,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    // Resolve all titles once for search filtering
    val titleMap = menuSections
        .flatMap { it.destinations }
        .associateWith { stringResource(it.titleRes) }

    val allDestinations = menuSections.flatMap { it.destinations }
    val filteredDestinations = remember(searchQuery, titleMap) {
        if (searchQuery.isBlank()) null
        else allDestinations.filter { dest ->
            val title = titleMap[dest] ?: ""
            title.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(KronoTokens.Settings.panelTopSpacing))

        // ── Search Bar ────────────────────────────────────────
        SettingsSearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = KronoTokens.Settings.panelHorizontalInset)
        )

        Spacer(Modifier.height(KronoTokens.Settings.panelSectionGap))

        if (filteredDestinations != null) {
            // ── Search Results ────────────────────────────────
            if (filteredDestinations.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = KronoTokens.Spacing.xxxl),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = KronoIcons.Action.SearchOff,
                            contentDescription = null,
                            modifier = Modifier.size(KronoTokens.StateIcon.emptyMedium),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                        Spacer(Modifier.height(KronoTokens.Spacing.sm))
                        Text(
                            text = stringResource(R.string.settings_search_no_results),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = KronoTokens.Typography.listItem
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                SectionCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = KronoTokens.Settings.panelHorizontalInset)
                ) {
                    filteredDestinations.forEachIndexed { index, dest ->
                        SettingsItem(
                            title = titleMap[dest] ?: "",
                            subtitle = null,
                            icon = dest.icon,
                            accentColor = dest.accentColor(),
                            selected = selectedDestination == dest,
                            showBadge = dest is SettingsDestination.Changelog && hasPendingUpdate,
                            onClick = { onDestinationSelected(dest) }
                        )
                        if (index < filteredDestinations.lastIndex) {
                            ItemDivider()
                        }
                    }
                }
            }
        } else {
            // ── All Sections ──────────────────────────────────
            menuSections.forEach { section ->
                SectionLabel(title = stringResource(section.titleRes))

                SectionCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = KronoTokens.Settings.panelHorizontalInset)
                ) {
                    section.destinations.forEachIndexed { index, dest ->
                        SettingsItem(
                            title = titleMap[dest] ?: "",
                            subtitle = null,
                            icon = dest.icon,
                            accentColor = dest.accentColor(),
                            selected = selectedDestination == dest,
                            showBadge = dest is SettingsDestination.Changelog && hasPendingUpdate,
                            onClick = { onDestinationSelected(dest) }
                        )
                        if (index < section.destinations.lastIndex) {
                            ItemDivider()
                        }
                    }
                }

                Spacer(Modifier.height(KronoTokens.Settings.panelSectionGap))
            }
        }

        Spacer(Modifier.height(KronoTokens.Settings.panelBottomSpacing))
    }
}

// ── Search Bar ────────────────────────────────────────────────
@Composable
private fun SettingsSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    val textColor = MaterialTheme.colorScheme.onSurface
    val hintColor = MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier
            .height(KronoTokens.Settings.searchHeight)
            .clip(RoundedCornerShape(KronoTokens.Settings.searchCorner))
            .background(containerColor),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = KronoTokens.Settings.searchInnerHorizontal),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.sm)
        ) {
            Icon(
                imageVector = KronoIcons.Action.Search,
                contentDescription = null,
                modifier = Modifier.size(KronoTokens.Settings.searchIcon),
                tint = hintColor
            )

            Box(Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        text = stringResource(R.string.settings_search_placeholder),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = KronoTokens.Typography.listItem
                        ),
                        color = hintColor
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = KronoTokens.Typography.listItem,
                                color = textColor
                            ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            AnimatedVisibility(
                visible = query.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.size(KronoTokens.Settings.searchClearButton)
                ) {
                    Icon(
                        imageVector = KronoIcons.Navigation.Close,
                        contentDescription = stringResource(R.string.action_clear),
                        modifier = Modifier.size(KronoTokens.Settings.searchClearIcon),
                        tint = hintColor
                    )
                }
            }
        }
    }
}

// ── Section Label ─────────────────────────────────────────────
@Composable
private fun SectionLabel(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelMedium.copy(
            fontSize = KronoTokens.Typography.statusLabel,
            letterSpacing = 1.2.sp
        ),
        fontWeight = FontWeight.Normal,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(
            start = KronoTokens.Settings.panelHorizontalInset,
            top = KronoTokens.Spacing.md,
            bottom = KronoTokens.Spacing.sm
        )
    )
}

// ── Section Card Container ────────────────────────────────────
@Composable
private fun SectionCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .clip(KronoTokens.Shape.card)
            .border(
                width = KronoTokens.Settings.dividerThickness,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = KronoTokens.Settings.dividerAlpha),
                shape = KronoTokens.Shape.card
            ),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        shape = KronoTokens.Shape.card
    ) {
        Column(content = content)
    }
}

// ── Divider ───────────────────────────────────────────────────
@Composable
private fun ItemDivider() {
    SettingsDivider(withIconInset = true)
}

// ── Settings Item ─────────────────────────────────────────────
@Composable
private fun SettingsItem(
    title: String,
    subtitle: String?,
    icon: ImageVector,
    accentColor: Color,
    selected: Boolean,
    showBadge: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (selected)
        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = KronoTokens.Settings.menuSelectedRowAlpha)
    else
        Color.Transparent
    val textColor = if (selected)
        MaterialTheme.colorScheme.onSecondaryContainer
    else
        MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(KronoTokens.Shape.card)
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(
                horizontal = KronoTokens.Spacing.lg,
                vertical = KronoTokens.Spacing.md
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Colored icon container
        Box(
            modifier = Modifier
                .size(KronoTokens.Size.iconBox)
                .clip(KronoTokens.Shape.iconBox)
                .background(
                    if (selected) accentColor.copy(alpha = 0.2f)
                    else accentColor.copy(alpha = 0.1f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(KronoTokens.Size.iconInner),
                tint = accentColor.copy(if (selected) 0.9f else 0.75f)
            )
        }

        Spacer(Modifier.width(KronoTokens.Spacing.md))

        // Title + subtitle
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = KronoTokens.Typography.bodyText,
                    lineHeight = KronoTokens.Typography.titleRowLine
                ),
                fontWeight = FontWeight.Normal,
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = KronoTokens.Typography.statusLabel,
                        lineHeight = KronoTokens.Typography.statusLabelLine
                    ),
                    color = if (selected)
                        MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(Modifier.width(KronoTokens.Spacing.sm))

        // Badge
        if (showBadge) {
            Badge(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
                modifier = Modifier.padding(end = KronoTokens.Spacing.xs)
            ) {
                Text(stringResource(R.string.settings_badge_alert), style = MaterialTheme.typography.labelSmall.copy(fontSize = KronoTokens.Typography.statusLabel))
            }
        }

        // Chevron (sempre visível no menu lateral)
        Icon(
            imageVector = KronoIcons.Navigation.ChevronRight,
            contentDescription = null,
            modifier = Modifier.size(KronoTokens.Icon.small),
            tint = if (selected)
                MaterialTheme.colorScheme.onSecondaryContainer
            else
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    }
}


