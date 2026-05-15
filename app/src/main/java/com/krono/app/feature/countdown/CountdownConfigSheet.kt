package com.krono.app.feature.countdown

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import com.krono.app.core.ui.theme.KronoIcons
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.krono.app.feature.stopwatch.TimeLimitField
import com.krono.app.core.ui.dialogs.ColorPickerDialog
import com.krono.app.feature.countdown.CountdownConfig
import com.krono.app.R
import com.krono.app.core.ui.theme.KronoTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountdownConfigSheet(
    initial: CountdownConfig?,           // null = create mode
    onDismiss: () -> Unit,
    onConfirm: (CountdownConfig) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var description by remember { mutableStateOf(initial?.description ?: "") }
    var totalSeconds by remember { mutableLongStateOf(initial?.totalSeconds ?: 300L) }
    var bgColor by remember {
        mutableStateOf(Color(initial?.backgroundColor ?: 0xFF1E1E2E.toInt()))
    }
    var showColorPicker by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.lg)
        ) {
            // Title
            Text(
                text = if (initial == null) stringResource(R.string.countdown_new_timer_title) else stringResource(R.string.countdown_edit_timer_sheet_title),
                style = MaterialTheme.typography.titleLarge
            )

            // Description field
            OutlinedTextField(
                value = description,
                onValueChange = { if (it.length <= 40) description = it },
                label = { Text(stringResource(R.string.countdown_description_label)) },
                placeholder = { Text(stringResource(R.string.countdown_description_placeholder_sheet)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Time input
            Column {
                Text(
                    text = stringResource(R.string.countdown_time_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                TimeLimitField(
                    timeLimitSeconds = totalSeconds,
                    onConfirm = { totalSeconds = it }
                )
            }

            // Color picker row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.countdown_color_card_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                // Color preview circle + palette icon
                IconButton(onClick = { showColorPicker = true }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Spacer(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(bgColor)
                        )
                        Icon(
                            imageVector = KronoIcons.Action.Palette,
                            contentDescription = stringResource(R.string.countdown_choose_color_desc),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.action_cancel))
                }
                Button(
                    onClick = {
                        val config = (initial ?: CountdownConfig(
                            description = description,
                            totalSeconds = totalSeconds,
                            backgroundColor = bgColor.toArgb()
                        )).copy(
                            description = description,
                            totalSeconds = totalSeconds,
                            backgroundColor = bgColor.toArgb()
                        )
                        onConfirm(config)
                    },
                    enabled = totalSeconds > 0L
                ) {
                    Text(if (initial == null) stringResource(R.string.action_create) else stringResource(R.string.action_save))
                }
            }
        }
    }

    // Color picker dialog
    if (showColorPicker) {
        ColorPickerDialog(
            title = stringResource(R.string.countdown_color_card_title),
            initialColor = bgColor,
            initialOpacity = 1f,
            onConfirm = { color, _ -> 
                bgColor = color
                showColorPicker = false 
            },
            onDismiss = { showColorPicker = false }
        )
    }
}

