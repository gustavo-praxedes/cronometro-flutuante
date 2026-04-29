package com.krono.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.krono.app.data.CountdownConfig
import com.krono.app.viewmodel.CountdownViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountdownScreen(
    viewModel: CountdownViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val countdowns by viewModel.countdowns.collectAsState()

    // Sheet state: null = closed, non-null = editing/creating
    var sheetTarget by remember { mutableStateOf<CountdownConfig?>(null) }
    var sheetOpen by remember { mutableStateOf(false) }
    // true = create mode, false = edit mode
    var isCreateMode by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cronômetros regressivos") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = countdowns.size < CountdownViewModel.MAX_COUNTDOWNS,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it }
            ) {
                FloatingActionButton(
                    onClick = {
                        sheetTarget = null
                        isCreateMode = true
                        sheetOpen = true
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Novo cronômetro")
                }
            }
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (countdowns.isEmpty()) {
                EmptyCountdownState(
                    modifier = Modifier.align(Alignment.Center),
                    onAdd = {
                        sheetTarget = null
                        isCreateMode = true
                        sheetOpen = true
                    }
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 88.dp  // space for FAB
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = countdowns,
                        key = { it.config.id }
                    ) { state ->
                        CountdownCard(
                            state = state,
                            onEdit = {
                                sheetTarget = state.config
                                isCreateMode = false
                                sheetOpen = true
                            },
                            onToggleOverlay = {
                                viewModel.toggleOverlay(context, state.config.id)
                            },
                            onDelete = {
                                viewModel.deleteCountdown(context, state.config.id)
                            }
                        )
                    }
                }
            }
        }
    }

    // Bottom sheet
    if (sheetOpen) {
        CountdownConfigSheet(
            initial = if (isCreateMode) null else sheetTarget,
            onDismiss = { sheetOpen = false },
            onConfirm = { config ->
                if (isCreateMode) viewModel.addCountdown(config)
                else viewModel.updateConfig(config)
                sheetOpen = false
            }
        )
    }
}

@Composable
private fun EmptyCountdownState(
    modifier: Modifier = Modifier,
    onAdd: () -> Unit
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.HourglassEmpty,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Nenhum cronômetro ainda",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Toque em + para criar seu\nprimeiro cronômetro regressivo",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        FloatingActionButton(onClick = onAdd) {
            Icon(Icons.Default.Add, contentDescription = "Criar cronômetro")
        }
    }
}
