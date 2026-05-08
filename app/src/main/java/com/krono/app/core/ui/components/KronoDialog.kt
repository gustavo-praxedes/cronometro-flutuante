package com.krono.app.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.krono.app.core.ui.theme.KronoTokens

@Composable
fun KronoDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    dismissOnClickOutside: Boolean = true,
    title: String? = null,
    text: String? = null,
    onConfirm: (() -> Unit)? = null,
    confirmLabel: String = "Confirmar",
    onDismiss: (() -> Unit)? = null,
    dismissLabel: String = "Cancelar",
    isDestructive: Boolean = false,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = true,
            dismissOnClickOutside = dismissOnClickOutside
        )
    ) {
        Surface(
            shape = KronoTokens.Shape.card,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = modifier
                .padding(KronoTokens.Spacing.xxl)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(KronoTokens.Spacing.dialogPadding),
                verticalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.lg)
            ) {
                title?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                text?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                content()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (onDismiss != null || onConfirm != null) {
                        onDismiss?.let { action ->
                            TextButton(
                                onClick = {
                                    action()
                                    onDismissRequest()
                                }
                            ) {
                                Text(dismissLabel)
                            }
                            Spacer(modifier = Modifier.width(KronoTokens.Spacing.sm))
                        }

                        onConfirm?.let { action ->
                            if (isDestructive) {
                                Button(
                                    onClick = {
                                        action()
                                        onDismissRequest()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Text(confirmLabel)
                                }
                            } else {
                                Button(
                                    onClick = {
                                        action()
                                        onDismissRequest()
                                    }
                                ) {
                                    Text(confirmLabel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}