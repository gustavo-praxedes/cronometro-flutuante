package com.krono.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import com.krono.app.ui.theme.KronoIcons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.krono.app.data.CountdownConfig
import com.krono.app.ui.theme.KronoTokens
import com.krono.app.ui.theme.adaptiveDialogWidth

@Composable
fun CountdownConfigDialog(
    initial: CountdownConfig?,
    onDismiss: () -> Unit,
    onConfirm: (CountdownConfig) -> Unit,
    /** Chamado a cada tick do wheel — atualiza card e overlay ao vivo */
    onPreview: ((totalSeconds: Long) -> Unit)? = null
) {
    var description  by remember { mutableStateOf(initial?.description ?: "") }
    var totalSeconds by remember { mutableLongStateOf(initial?.totalSeconds ?: 0L) }
    var bgColor      by remember {
        mutableStateOf(Color(initial?.backgroundColor ?: 0xFFB5EAD7.toInt()))
    }
    var showColorPicker by remember { mutableStateOf(false) }

    val isEditMode = initial != null

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
            color          = MaterialTheme.colorScheme.surface,
            tonalElevation = KronoTokens.Elevation.dialog
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
                        text = if (isEditMode) "Editar cronômetro" else "Novo cronômetro",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                        ),
                        fontWeight = FontWeight.Bold,
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
                            contentDescription = "Fechar",
                            tint               = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(KronoTokens.Spacing.sectionGap))

                // ── Campo de descrição ─────────────────────────────────────
                OutlinedTextField(
                    value         = description,
                    onValueChange = { if (it.length <= 40) description = it },
                    label         = { Text("Descrição") },
                    placeholder   = { Text("Ex: Foco, Pausa, Cozinhar...") },
                    singleLine    = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction      = ImeAction.Done
                    ),
                    shape    = KronoTokens.Shape.input,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(KronoTokens.Spacing.sectionGap))

                // ── Wheel + cor na mesma linha ─────────────────────────────
                Row(
                    modifier          = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.sm)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        TimeWheelPicker(
                            totalSeconds  = totalSeconds,
                            onValueChange = { totalSeconds = it }
                        )
                    }

                    // Swatch de cor — círculo com ícone de paleta sobreposto
                    IconButton(
                        onClick  = { showColorPicker = true },
                        modifier = Modifier.size(KronoTokens.Button.heightSmall)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(bgColor)
                            )
                            Icon(
                                imageVector        = KronoIcons.Action.Palette,
                                contentDescription = "Cor do card",
                                tint               = overlayTextColor(bgColor).copy(alpha = 0.75f),
                                modifier           = Modifier.size(KronoTokens.Icon.listItem)
                            )
                        }
                    }
                }

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
                        text       = if (isEditMode) "Salvar" else "Criar",
                        fontSize   = KronoTokens.Typography.buttonLabel,
                        fontWeight = FontWeight.Bold
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
