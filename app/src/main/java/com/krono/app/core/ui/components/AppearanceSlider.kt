package com.krono.app.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.krono.app.core.ui.theme.KronoTokens

@Composable
internal fun AppearanceSlider(
    label   : String,
    value   : Float,
    minLabel: String,
    maxLabel: String,
    range   : ClosedFloatingPointRange<Float>,
    display : String,
    onChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyLarge)
            Text(
                text       = display,
                style      = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Normal,
                color      = MaterialTheme.colorScheme.primary
            )
        }
        Row(
            modifier          = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text     = minLabel,
                style    = MaterialTheme.typography.bodySmall,
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = KronoTokens.Spacing.xs)
            )
            Slider(
                value         = value,
                onValueChange = onChange,
                valueRange    = range,
                modifier      = Modifier.weight(1f)
            )
            Text(
                text     = maxLabel,
                style    = MaterialTheme.typography.bodySmall,
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = KronoTokens.Spacing.xs)
            )
        }
    }
}
