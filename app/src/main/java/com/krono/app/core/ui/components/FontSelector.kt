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
            style    = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        ExposedDropdownMenuBox(
            expanded         = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier         = Modifier.width(200.dp)
        ) {
            OutlinedTextField(
                value         = current.label,
                onValueChange = {},
                readOnly      = true,
                trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                shape         = KronoTokens.Shape.badge,
                modifier      = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
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