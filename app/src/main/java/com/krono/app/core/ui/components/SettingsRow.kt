package com.krono.app.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.krono.app.core.ui.theme.KronoTokens

@Composable
fun SettingsRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leadingIcon: ImageVector? = null,
    leadingTextIcon: String? = null,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    iconContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else KronoTokens.Alpha.disabled)
            .then(
                if (onClick != null && enabled) {
                    Modifier.clickable(enabled = true, onClick = onClick)
                } else {
                    Modifier
                }
            )
            .padding(
                horizontal = KronoTokens.Settings.panelHorizontalInset,
                vertical = KronoTokens.Settings.rowVerticalInset
            )
            .heightIn(min = KronoTokens.Component.rowMin),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.rowInner)
    ) {
        if (leadingIcon != null || !leadingTextIcon.isNullOrBlank()) {
            Surface(
                shape = KronoTokens.Shape.iconBox,
                color = iconContainerColor,
                modifier = Modifier.size(KronoTokens.Size.iconBox)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (leadingIcon != null) {
                        Icon(
                            imageVector = leadingIcon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(KronoTokens.Size.iconInner)
                        )
                    } else {
                        Text(
                            text = leadingTextIcon.orEmpty(),
                            color = iconTint,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontSize = KronoTokens.Typography.statusLabel,
                                fontWeight = FontWeight.Normal
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Normal,
                    lineHeight = KronoTokens.Typography.titleRowLine
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall.copy(
                        lineHeight = KronoTokens.Typography.statusLabelLine,
                        letterSpacing = 0.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.92f)
                )
            }
        }

        trailing?.invoke()
    }
}
