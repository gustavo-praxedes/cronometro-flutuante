package com.krono.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
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

    var dialogTarget by remember { mutableStateOf<CountdownConfig?>(null) }
    var dialogOpen by remember { mutableStateOf(false) }
    var isCreateMode by remember { mutableStateOf(false) }

    // FAB scale animation (20% bigger = 1.2f base size via size modifier)
    val fabScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(),
        label = "fab_scale"
    )

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
                        dialogTarget = null
                        isCreateMode = true
                        dialogOpen = true
                    },
                    modifier = Modifier
                        .scale(fabScale)
                        .size(72.dp),  // default is 56dp; 72dp ≈ +28% bigger
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Novo cronômetro",
                        modifier = Modifier.size(32.dp)  // icon scales with FAB
                    )
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
                        dialogTarget = null
                        isCreateMode = true
                        dialogOpen = true
                    }
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(countdowns, key = { it.config.id }) { state ->
                        CountdownCard(
                            state = state,
                            onEdit = {
                                dialogTarget = state.config
                                isCreateMode = false
                                dialogOpen = true
                            },
                            onPlay = { viewModel.play(context, state.config.id) },
                            onPause = { viewModel.pause(context, state.config.id) },
                            onReset = { viewModel.reset(context, state.config.id) },
                            onToggleOverlay = { viewModel.toggleOverlay(context, state.config.id) },
                            onDelete = { viewModel.deleteCountdown(context, state.config.id) }
                        )
                    }
                }
            }
        }
    }

    if (dialogOpen) {
        CountdownConfigDialog(
            initial = if (isCreateMode) null else dialogTarget,
            onDismiss = { dialogOpen = false },
            onConfirm = { config ->
                if (isCreateMode) viewModel.addCountdown(config)
                else viewModel.updateConfig(config)
                dialogOpen = false
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
        modifier = modifier.padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Decorative icon with tonal background
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(96.dp)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.HourglassEmpty,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
            )
            Icon(
                imageVector = Icons.Default.HourglassEmpty,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Nenhum cronômetro",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Toque em + para criar seu\nprimeiro cronômetro regressivo",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Inline FAB as CTA
        FloatingActionButton(
            onClick = onAdd,
            modifier = Modifier.size(72.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Criar cronômetro",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
