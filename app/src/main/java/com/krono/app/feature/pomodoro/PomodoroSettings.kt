package com.krono.app.feature.pomodoro

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.krono.app.core.ui.theme.KronoTokens

@Composable
fun PomodoroSettings() {
    Column(modifier = Modifier.padding(KronoTokens.Spacing.lg)) {
        Text("Pomodoro", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(KronoTokens.Spacing.sm))
        Text("Configurações da ferramenta em evolução.", style = MaterialTheme.typography.bodyMedium)
    }
}

