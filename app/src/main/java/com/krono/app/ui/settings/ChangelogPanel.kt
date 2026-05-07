package com.krono.app.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.krono.app.BuildConfig
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoTokens
import com.krono.app.core.ui.components.SkeletonLoader
import com.krono.app.util.UpdateInfo
import com.krono.app.util.UpdateResult
import com.krono.app.util.checkForUpdate
import kotlinx.coroutines.launch

enum class ItemType(val icon: ImageVector, val iconTint: Color) {
    FEAT (KronoIcons.Action.Sparkle,          Color(0xFF10B981)),
    FIX  (KronoIcons.Status.Bug,              Color(0xFFEF4444)),
    PERF (KronoIcons.Status.Speed,            Color(0xFFF59E0B)),
    DOCS (KronoIcons.Status.Doc,              Color(0xFF8B5CF6)),
    CHORE(KronoIcons.Status.Build,            Color(0xFF6B7280)),
    OTHER(KronoIcons.Action.Check,            Color(0xFF3B82F6))
}

data class ChangelogItem(val text: String, val type: ItemType)

fun parseChangelog(changelog: String): List<ChangelogItem> {
    if (changelog.isBlank()) {
        return listOf(ChangelogItem(
            text = "Esta é a versão inicial do Krono! Explore os recursos e comece a medir seu tempo com precisão.",
            type = ItemType.FEAT
        ))
    }
    val items = mutableListOf<ChangelogItem>()
    var sectionType = ItemType.OTHER

    changelog.lines().forEach { line ->
        val trimmed = line.trim()
        if (trimmed.isBlank()) return@forEach

        if (trimmed.startsWith("#")) {
            sectionType = when {
                trimmed.contains("Novidades", true)    || trimmed.contains("✨") -> ItemType.FEAT
                trimmed.contains("Correções", true)    || trimmed.contains("🐛") -> ItemType.FIX
                trimmed.contains("Performance", true)  || trimmed.contains("⚡") -> ItemType.PERF
                trimmed.contains("Documentação", true) || trimmed.contains("📝") -> ItemType.DOCS
                trimmed.contains("Manutenção", true)   || trimmed.contains("🔧") -> ItemType.CHORE
                else -> sectionType
            }
            return@forEach
        }

        if (trimmed.startsWith("-") || trimmed.startsWith("*") || trimmed.startsWith("•")) {
            val content = trimmed.substring(1).trim()
                .replace(Regex("\\[.*?\\]\\(.*?\\)"), "")

            if (content.isBlank() || content.contains("Comparação completa", true)) return@forEach

            val itemType = when {
                content.startsWith("feat",  true) -> ItemType.FEAT
                content.startsWith("fix",   true) -> ItemType.FIX
                content.startsWith("perf",  true) -> ItemType.PERF
                content.startsWith("docs",  true) -> ItemType.DOCS
                content.startsWith("chore", true) -> ItemType.CHORE
                content.startsWith("build", true) -> ItemType.CHORE
                content.startsWith("ci",    true) -> ItemType.CHORE
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

@Composable
fun ChangelogPanel(
    updateInfo: UpdateInfo,
    onUpdateAvailable: (UpdateInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope          = rememberCoroutineScope()
    val changelogItems = remember(updateInfo.changelog) { parseChangelog(updateInfo.changelog) }

    var checking   by remember { mutableStateOf(false) }
    var lastResult by remember { mutableStateOf<UpdateResult?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = KronoTokens.Spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(KronoTokens.Spacing.xl))

        Text(
            text       = "Novidades da Versão ${updateInfo.tagName}",
            style      = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            fontSize   = KronoTokens.Typography.dialogTitle,
            textAlign  = TextAlign.Center,
            modifier   = Modifier.padding(horizontal = KronoTokens.Spacing.dialogPadding)
        )

        Spacer(Modifier.height(KronoTokens.Spacing.sectionGap))

        LazyColumn(
            modifier            = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.listItemGap),
            horizontalAlignment = Alignment.Start
        ) {
            items(changelogItems) { item ->
                Row(
                    modifier          = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector        = item.type.icon,
                        contentDescription = null,
                        tint               = item.type.iconTint,
                        modifier           = Modifier
                            .size(KronoTokens.Icon.listItem)
                            .padding(top = KronoTokens.Spacing.xs)
                    )

                    Spacer(Modifier.width(KronoTokens.Spacing.listIconGap))

                    Text(
                        text     = item.text,
                        style    = MaterialTheme.typography.bodyMedium.copy(
                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(Modifier.height(KronoTokens.Spacing.sectionGap))

        AnimatedVisibility(
            visible = !checking,
            enter   = fadeIn(
                animationSpec = tween(
                    durationMillis = KronoTokens.Animation.fadeDurationMs,
                    easing = KronoTokens.Motion.easingNormal
                )
            ),
            exit    = fadeOut(
                animationSpec = tween(
                    durationMillis = KronoTokens.Animation.fadeDurationMs,
                    easing = KronoTokens.Motion.easingNormal
                )
            )
        ) {
            val result     = lastResult
            val isUpToDate = result is UpdateResult.UpToDate ||
                    (result is UpdateResult.UpdateAvailable &&
                            result.info.tagName == BuildConfig.VERSION_NAME)

            if (isUpToDate) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector        = KronoIcons.Action.Check,
                        contentDescription = null,
                        tint               = MaterialTheme.colorScheme.primary,
                        modifier           = Modifier.size(KronoTokens.Icon.status)
                    )
                    Spacer(Modifier.width(KronoTokens.Spacing.sm))
                    Text(
                        text  = "Atualizado",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                Button(
                    onClick = {
                        scope.launch {
                            checking = true
                            val response = checkForUpdate(BuildConfig.VERSION_NAME)
                            lastResult = response
                            checking = false
                            if (response is UpdateResult.UpdateAvailable &&
                                response.info.tagName != BuildConfig.VERSION_NAME
                            ) {
                                onUpdateAvailable(response.info)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(KronoTokens.Button.height),
                    shape  = KronoTokens.Shape.button,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor   = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        imageVector        = KronoIcons.Action.Reset,
                        contentDescription = null,
                        modifier           = Modifier.size(KronoTokens.Icon.button)
                    )
                    Spacer(Modifier.width(KronoTokens.Button.iconSpacing))
                    Text(
                        text       = "Verificar Atualizações",
                        fontSize   = KronoTokens.Typography.buttonLabel,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (checking) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                SkeletonLoader.SkeletonText(
                    modifier = Modifier
                        .width(60.dp)
                        .height(KronoTokens.Component.inlineSpinner)
                )
                Spacer(Modifier.width(KronoTokens.Spacing.md))
                Text(
                    text  = "Verificando...",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(Modifier.height(KronoTokens.Spacing.xxl))
    }
}