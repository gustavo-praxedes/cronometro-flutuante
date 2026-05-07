package com.krono.app.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.krono.app.core.ui.theme.KronoThemeOption
import com.krono.app.core.ui.theme.KronoTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSelector(
    selectedTheme: String,
    onChange     : (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val current  = KronoThemeOption.entries.find { it.name == selectedTheme }
        ?: KronoThemeOption.AUTO

    Row(
        modifier              = Modifier.fillMaxWidth(),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text     = "Tema",
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
                KronoThemeOption.entries.forEach { option ->
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