package com.krono.app.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.outlined.PictureInPicture
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krono.app.data.CountdownState
import com.krono.app.data.TimeUtils

@Composable
fun CountdownCard(
    state: CountdownState,
    onEdit: () -> Unit,
    onToggleOverlay: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val bgColor = Color(state.config.backgroundColor)
    val textColor = readableTextColor(bgColor)

    val containerColor by animateColorAsState(
        targetValue = if (state.isCompleted) Color(0xFFB00020).copy(alpha = 0.15f) else bgColor,
        animationSpec = spring(stiffness = 200f),
        label = "card_bg"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onEdit),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: description + time
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = state.config.description.ifBlank { "Sem título" },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = textColor,
                        maxLines = 1
                    )
                    if (state.isCompleted) {
                        CompletedBadge()
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = TimeUtils.formatSeconds(state.config.totalSeconds),
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor.copy(alpha = 0.7f),
                    fontSize = 13.sp
                )
            }

            // Overlay toggle button
            IconButton(
                onClick = onToggleOverlay,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = if (state.isOverlayVisible)
                        Icons.Filled.PictureInPicture
                    else
                        Icons.Outlined.PictureInPicture,
                    contentDescription = if (state.isOverlayVisible) "Ocultar overlay" else "Mostrar overlay",
                    tint = textColor.copy(alpha = if (state.isOverlayVisible) 1f else 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }

            // ⋮ Menu
            Box {
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Opções",
                        tint = textColor.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Excluir",
                                color = MaterialTheme.colorScheme.error
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CompletedBadge() {
    Surface(
        color = Color(0xFFB00020),
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(10.dp)
            )
            Text(
                text = "Concluído",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun readableTextColor(bg: Color): Color {
    val luminance = 0.299f * bg.red + 0.587f * bg.green + 0.114f * bg.blue
    return if (luminance > 0.5f) Color(0xFF1C1B1F) else Color(0xFFE6E1E5)
}
