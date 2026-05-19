package com.krono.app.core.ui.settings

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.krono.app.core.ui.theme.KronoIcons

enum class ItemType(val icon: ImageVector, val iconTint: Color) {
    FEAT(KronoIcons.Action.Sparkle, Color(0xFF10B981)),
    FIX(KronoIcons.Status.Bug, Color(0xFFEF4444)),
    PERF(KronoIcons.Status.Speed, Color(0xFFF59E0B)),
    DOCS(KronoIcons.Status.Doc, Color(0xFF8B5CF6)),
    CHORE(KronoIcons.Status.Build, Color(0xFF6B7280)),
    OTHER(KronoIcons.Action.Check, Color(0xFF3B82F6))
}

data class ChangelogItem(val text: String, val type: ItemType)

fun parseChangelog(changelog: String): List<ChangelogItem> {
    if (changelog.isBlank()) return emptyList()

    val items = mutableListOf<ChangelogItem>()
    var sectionType = ItemType.OTHER

    changelog.lines().forEach { line ->
        val trimmed = line.trim()
        if (trimmed.isBlank()) return@forEach

        if (trimmed.startsWith("#")) {
            sectionType = when {
                trimmed.contains("Novidades", true) || trimmed.contains("✨") -> ItemType.FEAT
                trimmed.contains("Correções", true) || trimmed.contains("🐛") -> ItemType.FIX
                trimmed.contains("Performance", true) || trimmed.contains("⚡") -> ItemType.PERF
                trimmed.contains("Documentação", true) || trimmed.contains("📝") -> ItemType.DOCS
                trimmed.contains("Manutenção", true) || trimmed.contains("🔧") -> ItemType.CHORE
                else -> sectionType
            }
            return@forEach
        }

        if (trimmed.startsWith("-") || trimmed.startsWith("*") || trimmed.startsWith("•")) {
            val content = trimmed.substring(1).trim()
                .replace(Regex("\\[.*?\\]\\(.*?\\)"), "")
            if (content.isBlank() || content.contains("Comparação completa", true)) return@forEach

            val itemType = when {
                content.startsWith("feat", true) -> ItemType.FEAT
                content.startsWith("fix", true) -> ItemType.FIX
                content.startsWith("perf", true) -> ItemType.PERF
                content.startsWith("docs", true) -> ItemType.DOCS
                content.startsWith("chore", true) -> ItemType.CHORE
                content.startsWith("build", true) -> ItemType.CHORE
                content.startsWith("ci", true) -> ItemType.CHORE
                else -> sectionType
            }

            val finalText = if (content.contains(":")) {
                content.substringAfter(":").trim()
            } else {
                content
            }.replaceFirstChar { it.uppercase() }

            items.add(ChangelogItem(finalText, itemType))
        }
    }
    return items
}
