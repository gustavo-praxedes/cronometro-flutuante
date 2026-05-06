package com.krono.app.ui

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.krono.app.data.CountdownConfig
import com.krono.app.core.service.MainService
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoTokens
import com.krono.app.viewmodel.CountdownViewModel
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountdownScreen(
    viewModel: CountdownViewModel,
    onOpenSettings: () -> Unit
) {
    val context    = LocalContext.current
    val countdowns by viewModel.countdowns.collectAsState()

    var dialogTarget by remember { mutableStateOf<CountdownConfig?>(null) }
    var dialogOpen   by remember { mutableStateOf(false) }
    var isCreateMode by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                ),
                navigationIcon = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            imageVector = KronoIcons.Navigation.Menu,
                            contentDescription = "Configurações",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = countdowns.isNotEmpty() && countdowns.size < CountdownViewModel.MAX_COUNTDOWNS,
                enter   = fadeIn() + slideInVertically { it },
                exit    = fadeOut() + slideOutVertically { it }
            ) {
                LargeFloatingActionButton(
                    onClick        = { dialogTarget = null; isCreateMode = true; dialogOpen = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor   = MaterialTheme.colorScheme.onPrimary,
                    shape          = KronoTokens.Shape.button,
                    modifier       = Modifier.size(KronoTokens.FAB.size)
                ) {
                    Icon(KronoIcons.Action.Add, "Novo", modifier = Modifier.size(KronoTokens.FAB.iconSize))
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
                    onAdd    = { dialogTarget = null; isCreateMode = true; dialogOpen = true }
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start  = KronoTokens.Spacing.lg,
                        end    = KronoTokens.Spacing.lg,
                        top    = KronoTokens.Spacing.sm,
                        bottom = KronoTokens.Spacing.listBottomPadding
                    ),
                    verticalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.listItemGap)
                ) {
                    items(countdowns, key = { it.config.id }) { state ->
                        CountdownCard(
                            state           = state,
                            onEdit          = { dialogTarget = state.config; isCreateMode = false; dialogOpen = true },
                            onPlay          = { viewModel.play(context, state.config.id) },
                            onPause         = { viewModel.pause(context, state.config.id) },
                            onReset         = { viewModel.reset(context, state.config.id) },
                            onToggleOverlay = { viewModel.toggleOverlay(context, state.config.id) },
                            onDelete        = { viewModel.deleteCountdown(context, state.config.id) }
                        )
                    }
                }
            }
        }
    }

    if (dialogOpen) {
        CountdownConfigDialog(
            initial   = if (isCreateMode) null else dialogTarget,
            onDismiss = {
                if (!isCreateMode) viewModel.revertPreview(dialogTarget?.id)
                dialogOpen = false
            },
            onConfirm = { config ->
                if (isCreateMode) viewModel.addCountdown(config)
                else viewModel.updateConfig(config)
                dialogOpen = false
            },
            onPreview = { seconds ->
                if (!isCreateMode) {
                    viewModel.previewRemaining(dialogTarget?.id, seconds)
                    dialogTarget?.id?.let { id ->
                        context.startService(
                            Intent(context, MainService::class.java).apply {
                                action = CountdownViewModel.ACTION_COUNTDOWN_SYNC
                                putExtra(CountdownViewModel.EXTRA_COUNTDOWN_ID, id)
                            }
                        )
                    }
                }
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
        modifier            = modifier.padding(KronoTokens.Spacing.xxxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(KronoTokens.StateIcon.emptyLarge)) {
            Icon(
                KronoIcons.Status.Empty, null,
                modifier = Modifier.size(KronoTokens.StateIcon.emptyLarge),
                tint     = MaterialTheme.colorScheme.primary.copy(alpha = KronoTokens.Alpha.low)
            )
            Icon(
                KronoIcons.Status.Empty, null,
                modifier = Modifier.size(KronoTokens.StateIcon.emptyMedium),
                tint     = MaterialTheme.colorScheme.primary.copy(alpha = KronoTokens.Alpha.disabled)
            )
        }

        Spacer(Modifier.height(KronoTokens.Spacing.xl))

        Text(
            "Nenhum timer",
            style      = MaterialTheme.typography.titleLarge.copy(
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            ),
            fontWeight = FontWeight.SemiBold,
            textAlign  = TextAlign.Center
        )

        Spacer(Modifier.height(KronoTokens.Spacing.sm))

        Text(
            "Toque em + para criar seu\nprimeiro timer regressivo",
            style     = MaterialTheme.typography.bodyMedium,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(KronoTokens.Spacing.xxxl))

        Button(
            onClick  = onAdd,
            modifier = Modifier.height(KronoTokens.Button.height),
            shape    = KronoTokens.Shape.button,
            colors   = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor   = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Icon(KronoIcons.Action.Add, null, modifier = Modifier.size(KronoTokens.Icon.button))
            Spacer(Modifier.width(KronoTokens.Button.iconSpacing))
            Text(
                "Criar timer",
                fontSize   = KronoTokens.Typography.buttonLabel,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
