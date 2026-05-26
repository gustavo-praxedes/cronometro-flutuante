package com.krono.app.feature.countdown

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import com.krono.app.core.ui.theme.KronoIcons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.krono.app.feature.countdown.CountdownConfig
import com.krono.app.R
import com.krono.app.core.ui.theme.KronoTokens
import com.krono.app.core.ui.theme.adaptiveDialogWidth

@Composable
fun CountdownConfigDialog(
    initial: CountdownConfig?,
    onDismiss: () -> Unit,
    onConfirm: (CountdownConfig) -> Unit,
    /** Chamado a cada tick do wheel — atualiza card e overlay ao vivo */
    onPreview: ((totalSeconds: Long) -> Unit)? = null
) {
    val defaultBg = MaterialTheme.colorScheme.primaryContainer.toArgb()
    var description   by remember { mutableStateOf(initial?.description ?: "") }
    var totalSeconds  by remember { mutableLongStateOf(initial?.totalSeconds ?: 0L) }
    var bgColor      by remember {
        mutableStateOf(Color(initial?.backgroundColor ?: defaultBg))
    }
    var showColorPicker by remember { mutableStateOf(false) }

    val isEditMode = initial != null
    val dialogColor = MaterialTheme.colorScheme.surfaceColorAtElevation(KronoTokens.Elevation.dialog)

    // Preview ao vivo — dispara a cada mudança de totalSeconds
    LaunchedEffect(totalSeconds) {
        if (isEditMode) onPreview?.invoke(totalSeconds)
    }

    Dialog(
        onDismissRequest = {
            if (isEditMode) onPreview?.invoke(initial!!.totalSeconds) // reverte preview
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier       = Modifier
                .adaptiveDialogWidth()
                .wrapContentHeight(),
            shape          = KronoTokens.Shape.dialog,
            color          = dialogColor,
            tonalElevation = 0.dp,
            shadowElevation = KronoTokens.Elevation.dialog
        ) {
            Column(
                modifier = Modifier
                    .padding(KronoTokens.Spacing.dialogPadding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // ── Header: título + X ─────────────────────────────────────
                Box(
                    modifier         = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isEditMode) stringResource(R.string.countdown_edit_timer_title) else stringResource(R.string.countdown_new_timer_title),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                        ),
                        fontWeight = FontWeight.Normal,
                        fontSize   = KronoTokens.Typography.dialogTitle
                    )
                    IconButton(
                        onClick  = {
                            if (isEditMode) onPreview?.invoke(initial!!.totalSeconds)
                            onDismiss()
                        },
                        modifier = Modifier
                            .size(KronoTokens.Icon.close)
                            .align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            imageVector        = KronoIcons.Navigation.Close,
                            contentDescription = stringResource(R.string.action_close),
                            tint               = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(KronoTokens.Spacing.sectionGap))

                // ── Campo de descrição ─────────────────────────────────────
                OutlinedTextField(
                    value         = description,
                    onValueChange = { if (it.length <= KronoTokens.Component.descriptionMaxLen) description = it },
                    label         = { Text(stringResource(R.string.countdown_description_label)) },
                    placeholder   = { Text(stringResource(R.string.countdown_description_placeholder_dialog)) },
                    singleLine    = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction      = ImeAction.Done
                    ),
                    shape    = KronoTokens.Shape.input,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(
                            onClick  = { showColorPicker = true },
                            modifier = Modifier.size(KronoTokens.Button.heightSmall)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Box(
                                    modifier = Modifier
                                        .size(KronoTokens.Component.colorSwatch)
                                        .clip(CircleShape)
                                        .background(bgColor)
                                )
                                Icon(
                                    imageVector        = KronoIcons.Action.Palette,
                                    contentDescription = stringResource(R.string.countdown_color_card_label),
                                    tint               = overlayTextColor(bgColor).copy(alpha = KronoTokens.Alpha.medium),
                                    modifier           = Modifier.size(KronoTokens.Icon.listItem)
                                )
                            }
                        }
                    },
                    supportingText = {
                        Text(
                            text      = "${description.length}/${KronoTokens.Component.descriptionMaxLen}",
                            modifier  = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    }
                )

                Spacer(Modifier.height(KronoTokens.Spacing.sectionGap))

                // ── Wheel Picker (Ocupando a largura total) ────────────────
                TimeWheelPicker(
                    totalSeconds  = totalSeconds,
                    fadeColor     = dialogColor,
                    onValueChange = { totalSeconds = it }
                )

                Spacer(Modifier.height(KronoTokens.Spacing.sectionGap))

                // ── Botão confirmar ────────────────────────────────────────
                Button(
                    onClick  = {
                        val config = (initial ?: CountdownConfig(
                            description     = description,
                            totalSeconds    = totalSeconds,
                            backgroundColor = bgColor.toArgb()
                        )).copy(
                            description     = description,
                            totalSeconds    = totalSeconds,
                            backgroundColor = bgColor.toArgb()
                        )
                        onConfirm(config)
                    },
                    enabled  = totalSeconds > 0L,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(KronoTokens.Button.height),
                    shape  = KronoTokens.Shape.button,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor   = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text       = if (isEditMode) stringResource(R.string.action_save) else stringResource(R.string.action_create),
                        fontSize   = KronoTokens.Typography.buttonLabel,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }

    // Seletor de cor pastel
    if (showColorPicker) {
        CountdownColorPickerDialog(
            currentColor    = bgColor,
            onDismiss       = { showColorPicker = false },
            onColorSelected = { bgColor = it; showColorPicker = false }
        )
    }
}


