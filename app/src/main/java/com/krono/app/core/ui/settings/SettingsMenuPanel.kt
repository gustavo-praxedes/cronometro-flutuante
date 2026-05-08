package com.krono.app.core.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoTokens

// ── Icon accent colors per destination ───────────────────────
private fun SettingsDestination.accentColor(): Color = when (this) {
    SettingsDestination.Appearance  -> Color(0xFF6B7FD4)
    SettingsDestination.Behavior    -> Color(0xFFF59E0B)
    SettingsDestination.Overlay     -> Color(0xFF8B5CF6)
    SettingsDestination.Stopwatch   -> Color(0xFF10B981)
    SettingsDestination.Countdown   -> Color(0xFF06B6D4)
    SettingsDestination.About       -> Color(0xFF3B82F6)
    SettingsDestination.Support     -> Color(0xFFEF4444)
    SettingsDestination.Changelog   -> Color(0xFFF97316)
    SettingsDestination.Updates     -> Color(0xFF22C55E)
}

// ── Section data ──────────────────────────────────────────────
private data class MenuSection(
    val titleRes: Int,
    val destinations: List<SettingsDestination>
)

private val menuSections = listOf(
    MenuSection(
        titleRes = R.string.settings_section_general,
        destinations = listOf(
            SettingsDestination.Appearance,
            SettingsDestination.Behavior,
            SettingsDestination.Overlay
        )
    ),
    MenuSection(
        titleRes = R.string.settings_section_tools,
        destinations = listOf(
            SettingsDestination.Stopwatch,
            SettingsDestination.Countdown
        )
    ),
    MenuSection(
        titleRes = R.string.settings_section_info,
        destinations = listOf(
            SettingsDestination.About,
            SettingsDestination.Support,
            SettingsDestination.Changelog,
            SettingsDestination.Updates
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
            title.contains(searchQuery, ignoreCase = true) ||
                dest.subtitle.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(KronoTokens.Spacing.lg))

        // ── Search Bar ────────────────────────────────────────
        SettingsSearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = KronoTokens.Spacing.lg)
        )

        Spacer(Modifier.height(KronoTokens.Spacing.lg))

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
                            imageVector = KronoIcons.Status.Empty,
                            contentDescription = null,
                            modifier = Modifier.size(KronoTokens.StateIcon.emptyMedium),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                        Spacer(Modifier.height(KronoTokens.Spacing.sm))
                        Text(
                            text = "Nenhum resultado",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                SectionCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = KronoTokens.Spacing.lg)
                ) {
                    filteredDestinations.forEachIndexed { index, dest ->
                        SettingsItem(
                            title = titleMap[dest] ?: "",
                            subtitle = dest.subtitle,
                            icon = dest.icon,
                            accentColor = dest.accentColor(),
                            selected = selectedDestination == dest,
                            showBadge = dest is SettingsDestination.Updates && hasPendingUpdate,
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
                SectionLabel(titleRes = section.titleRes)

                SectionCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = KronoTokens.Spacing.lg)
                ) {
                    section.destinations.forEachIndexed { index, dest ->
                        SettingsItem(
                            title = titleMap[dest] ?: "",
                            subtitle = dest.subtitle,
                            icon = dest.icon,
                            accentColor = dest.accentColor(),
                            selected = selectedDestination == dest,
                            showBadge = dest is SettingsDestination.Updates && hasPendingUpdate,
                            onClick = { onDestinationSelected(dest) }
                        )
                        if (index < section.destinations.lastIndex) {
                            ItemDivider()
                        }
                    }
                }

                Spacer(Modifier.height(KronoTokens.Spacing.lg))
            }
        }

        Spacer(Modifier.height(KronoTokens.Spacing.xxl))
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
            .height(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(containerColor),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = KronoTokens.Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.sm)
        ) {
            Icon(
                imageVector = KronoIcons.Action.Settings, // reuse magnifier-like icon
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = hintColor
            )

            Box(Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        text = "Pesquisar...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = hintColor
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = textColor),
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
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = KronoIcons.Navigation.Close,
                        contentDescription = "Limpar",
                        modifier = Modifier.size(16.dp),
                        tint = hintColor
                    )
                }
            }
        }
    }
}

// ── Section Label ─────────────────────────────────────────────
@Composable
private fun SectionLabel(titleRes: Int) {
    Text(
        text = stringResource(titleRes).uppercase(),
        style = MaterialTheme.typography.labelMedium.copy(
            letterSpacing = 1.2.sp
        ),
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(
            start = KronoTokens.Spacing.lg,
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
        modifier = modifier.clip(KronoTokens.Shape.card),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shadowElevation = 0.dp,
        shape = KronoTokens.Shape.card
    ) {
        Column(content = content)
    }
}

// ── Divider ───────────────────────────────────────────────────
@Composable
private fun ItemDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 68.dp, end = KronoTokens.Spacing.lg),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
        thickness = 0.5.dp
    )
}

// ── Settings Item ─────────────────────────────────────────────
@Composable
private fun SettingsItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    selected: Boolean,
    showBadge: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (selected)
        MaterialTheme.colorScheme.secondaryContainer
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
        // Active indicator (left bar)
        if (selected) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(32.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(2.dp)
                    )
            )
            Spacer(Modifier.width(KronoTokens.Spacing.md))
        }

        // Colored icon container
        Box(
            modifier = Modifier
                .size(40.dp)
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
                modifier = Modifier.size(20.dp),
                tint = accentColor.copy(if (selected) 1f else 0.7f)
            )
        }

        Spacer(Modifier.width(KronoTokens.Spacing.md))

        // Title + subtitle
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = if (selected)
                    MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.width(KronoTokens.Spacing.sm))

        // Badge
        if (showBadge) {
            Badge(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
                modifier = Modifier.padding(end = KronoTokens.Spacing.xs)
            ) {
                Text("!", style = MaterialTheme.typography.labelSmall)
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
