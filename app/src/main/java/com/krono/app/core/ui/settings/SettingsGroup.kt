package com.krono.app.core.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.border
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krono.app.core.ui.theme.KronoTokens
import kotlinx.coroutines.delay

@Composable
fun SettingsGroup(
    title: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = KronoTokens.Typography.statusLabel,
                letterSpacing = 1.2.sp
            ),
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(
                top = KronoTokens.Settings.groupTitleTop,
                bottom = KronoTokens.Settings.groupTitleBottom
            )
        )

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
}

@Composable
fun ExpandableSettingsGroup(
    title: String,
    keepExpanded: Boolean = false,
    collapsedContent: @Composable (expand: () -> Unit) -> Unit,
    expandedContent: @Composable (markInteraction: () -> Unit) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var interactionToken by remember { mutableLongStateOf(0L) }
    val markInteraction = {
        interactionToken += 1L
        expanded = true
    }

    LaunchedEffect(expanded, interactionToken, keepExpanded) {
        if (expanded && !keepExpanded) {
            delay(5_000L)
            if (!keepExpanded) expanded = false
        }
    }

    SettingsGroup(title = title) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
        ) {
            Column(modifier = Modifier.clickable(onClick = markInteraction)) {
                collapsedContent(markInteraction)
            }
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    expandedContent(markInteraction)
                }
            }
        }
    }
}

