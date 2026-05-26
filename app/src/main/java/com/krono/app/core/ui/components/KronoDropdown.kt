package com.krono.app.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import com.krono.app.core.ui.theme.KronoTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> KronoDropdown(
    value: T,
    onValueChange: (T) -> Unit,
    options: List<T>,
    label: String? = null,
    leadingIcon: ImageVector? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    optionLeadingIcon: ImageVector? = null,
    optionLeadingContentDescription: String? = null,
    optionLeadingIconVisible: (T) -> Boolean = { true },
    onOptionLeadingClick: ((T) -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
    textMapping: (T) -> String = { it.toString() }
) {
    var open by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val currentText = textMapping(value)
    fun close() {
        open = false
        onDismiss?.invoke()
    }

    SettingsRow(
        title = label.orEmpty(),
        leadingIcon = leadingIcon,
        subtitle = null,
        modifier = modifier.alpha(if (enabled) 1f else KronoTokens.Alpha.disabled),
        enabled = enabled,
        onClick = if (enabled) ({ open = true }) else null,
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
            onDismissRequest = { close() },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = KronoTokens.Spacing.lg, vertical = KronoTokens.Spacing.md)
            ) {
                if (!label.isNullOrBlank()) {
                    Text(
                        text = label.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = KronoTokens.Typography.statusLabel,
                            letterSpacing = 1.2.sp
                        ),
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = KronoTokens.Spacing.sm)
                    )
                    HorizontalDivider()
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp)
                ) {
                    itemsIndexed(options) { index, option ->
                        val isSelected = option == value
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = KronoTokens.Component.rowMin)
                                .clickable {
                                    onValueChange(option)
                                    close()
                                }
                                .padding(vertical = KronoTokens.Spacing.sm),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.sm)
                        ) {
                            if (optionLeadingIcon != null && onOptionLeadingClick != null) {
                                Box(modifier = Modifier.size(KronoTokens.Size.iconBox)) {
                                    if (optionLeadingIconVisible(option)) {
                                        IconButton(
                                            onClick = { onOptionLeadingClick(option) },
                                            modifier = Modifier.matchParentSize()
                                        ) {
                                            Icon(
                                                imageVector = optionLeadingIcon,
                                                contentDescription = optionLeadingContentDescription,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                            Text(
                                text = textMapping(option),
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (index < options.lastIndex) HorizontalDivider()
                    }
                }
            }
        }
    }
}
