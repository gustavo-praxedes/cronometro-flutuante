package com.krono.app.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.krono.app.core.ui.theme.KronoFontOption
import com.krono.app.core.ui.theme.KronoTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FontSelector(
    selectedFont: String,
    onChange     : (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val current  = KronoFontOption.entries.find { it.name == selectedFont }
        ?: KronoFontOption.SYSTEM_DEFAULT

    Row(
        modifier              = Modifier.fillMaxWidth(),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text     = "Fonte",
            style    = MaterialTheme.typography.bodyMedium,
            color    = MaterialTheme.colorScheme.onSurface
        )

        ExposedDropdownMenuBox(
            expanded         = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier         = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value         = current.label,
                onValueChange = {},
                readOnly      = true,
                label         = { Text("Fonte") },
                trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                shape         = KronoTokens.Shape.input,
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                modifier      = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                textStyle     = MaterialTheme.typography.bodyMedium,
                singleLine    = true
            )

            ExposedDropdownMenu(
                expanded         = expanded,
                onDismissRequest = { expanded = false }
            ) {
                KronoFontOption.entries.forEach { option ->
                    DropdownMenuItem(
                        text    = { Text(option.label) },
                        onClick = {
                            onChange(option.name)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}