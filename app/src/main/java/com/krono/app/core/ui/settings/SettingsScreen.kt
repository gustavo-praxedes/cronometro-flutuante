package com.krono.app.core.ui.settings

import androidx.compose.animation.AnimatedContent
import androidx.compose.ui.zIndex
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krono.app.R
import com.krono.app.core.data.OverlayConfig
import com.krono.app.core.data.OverlayDataStore
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoTokens
import com.krono.app.core.util.UpdateInfo

private val SettingsHeaderControlSize = 48.dp
private val SettingsHeaderGap = 8.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    dataStore         : OverlayDataStore,
    pendingUpdateInfo : UpdateInfo?,
    isServiceRunning  : () -> Boolean,
    onStartFocusMode  : () -> Unit,
    onShowOverlay     : () -> Unit,
    onBack            : () -> Unit,
    modifier: Modifier = Modifier
) {
    val config      = dataStore.configFlow.collectAsState(initial = OverlayConfig()).value
    val scope       = rememberCoroutineScope()

    var selectedDestination by remember { mutableStateOf<SettingsDestination?>(null) }
    var updateInfo by remember { mutableStateOf<UpdateInfo?>(pendingUpdateInfo) }

    val configuration = LocalConfiguration.current
    val isWideScreen = configuration.screenWidthDp >= 600

    LaunchedEffect(isWideScreen) {
        if (isWideScreen && selectedDestination == null) {
            selectedDestination = SettingsDestination.Appearance
        }
    }

    LaunchedEffect(pendingUpdateInfo) {
        if (pendingUpdateInfo != null) updateInfo = pendingUpdateInfo
    }

    if (isWideScreen) {
        WideScreenLayout(
            config = config,
            dataStore = dataStore,
            scope = scope,
            selectedDestination = selectedDestination,
            onDestinationSelected = { selectedDestination = it },
            updateInfo = updateInfo,
            isServiceRunning = isServiceRunning,
            onStartFocusMode = onStartFocusMode,
            onBack = onBack,
            modifier = modifier
        )
    } else {
        NarrowScreenLayout(
            config = config,
            dataStore = dataStore,
            scope = scope,
            selectedDestination = selectedDestination,
            onDestinationSelected = { selectedDestination = it },
            onBack = onBack,
            updateInfo = updateInfo,
            isServiceRunning = isServiceRunning,
            onStartFocusMode = onStartFocusMode,
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WideScreenLayout(
    config: OverlayConfig,
    dataStore: OverlayDataStore,
    scope: kotlinx.coroutines.CoroutineScope,
    selectedDestination: SettingsDestination?,
    onDestinationSelected: (SettingsDestination?) -> Unit,
    updateInfo: UpdateInfo?,
    isServiceRunning: () -> Boolean,
    onStartFocusMode: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // Menu rail
            Surface(
                modifier = Modifier
                    .weight(0.35f)
                    .fillMaxHeight(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header fixo no topo
                    Spacer(Modifier.height(KronoTokens.Settings.stickyHeaderTop))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(SettingsHeaderControlSize)
                            .padding(
                                start = KronoTokens.Settings.panelHorizontalInset,
                                end = KronoTokens.Settings.panelHorizontalInset
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(SettingsHeaderControlSize),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier.size(SettingsHeaderControlSize)
                            ) {
                                Icon(
                                    imageVector = KronoIcons.Navigation.Back,
                                    contentDescription = stringResource(R.string.action_back)
                                )
                            }
                        }
                        Spacer(Modifier.width(SettingsHeaderGap))
                        Text(
                            text = stringResource(R.string.settings_title),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = KronoTokens.Typography.dialogTitle
                            ),
                            fontWeight = FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Menu scrollável
                    SettingsMenuPanel(
                        selectedDestination = selectedDestination,
                        onDestinationSelected = { onDestinationSelected(it) },
                        hasPendingUpdate = updateInfo != null,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                }
            }

            VerticalDivider(
                modifier = Modifier.fillMaxHeight(),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = KronoTokens.Settings.dividerAlpha),
                thickness = KronoTokens.Settings.dividerThickness
            )

            // Detail panel
            Column(
                modifier = Modifier
                    .weight(0.65f)
                    .fillMaxHeight()
            ) {
                // Sticky header - mesma baseline vertical do painel esquerdo
                if (selectedDestination != null) {
                    Spacer(Modifier.height(KronoTokens.Settings.stickyHeaderTop))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(SettingsHeaderControlSize)
                            .padding(
                                start = KronoTokens.Settings.panelHorizontalInset,
                                end = KronoTokens.Settings.panelHorizontalInset
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(selectedDestination.titleRes),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = KronoTokens.Typography.dialogTitle
                            ),
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (selectedDestination == null) {
                    // Empty state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = KronoIcons.Action.Settings,
                                contentDescription = null,
                                modifier = Modifier.size(KronoTokens.StateIcon.emptyLarge),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = KronoTokens.Settings.emptyStateIconAlpha)
                            )
                            Spacer(modifier = Modifier.height(KronoTokens.Spacing.lg))
                            Text(
                                text = stringResource(R.string.settings_select_category),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = KronoTokens.Typography.bodyText
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = KronoTokens.Settings.emptyStateTextAlpha)
                            )
                        }
                    }
                } else {
                    AnimatedContent(
                        targetState = selectedDestination,
                        transitionSpec = {
                            (slideInHorizontally(
                                animationSpec = tween(KronoTokens.Animation.slideDurationMs),
                                initialOffsetX = { it / 2 }
                            ) + fadeIn(animationSpec = tween(KronoTokens.Animation.fadeDurationMs)))
                                .togetherWith(
                                    slideOutHorizontally(
                                        animationSpec = tween(KronoTokens.Animation.slideDurationMs),
                                        targetOffsetX = { -it / 2 }
                                    ) + fadeOut(animationSpec = tween(KronoTokens.Animation.fadeDurationMs))
                                )
                        },
                        label = "panel-transition",
                        modifier = Modifier.fillMaxSize()
                    ) { destination ->
                        SettingsPanelHost(
                            destination = destination,
                            config = config,
                            dataStore = dataStore,
                            scope = scope,
                            totalLifetimeMs = config.totalLifetimeMs,
                            pendingUpdateInfo = updateInfo,
                            isServiceRunning = isServiceRunning,
                            onStartFocusMode = onStartFocusMode,
                            onSupportClick = {},
                            onShowChangelog = { onDestinationSelected(SettingsDestination.Changelog) },
                            onUpdateAvailable = { onDestinationSelected(SettingsDestination.Changelog) },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NarrowScreenLayout(
    config: OverlayConfig,
    dataStore: OverlayDataStore,
    scope: kotlinx.coroutines.CoroutineScope,
    selectedDestination: SettingsDestination?,
    onDestinationSelected: (SettingsDestination?) -> Unit,
    onBack: () -> Unit,
    updateInfo: UpdateInfo?,
    isServiceRunning: () -> Boolean,
    onStartFocusMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (selectedDestination != null)
                            stringResource(selectedDestination.titleRes)
                        else
                            stringResource(R.string.settings_title),
                        fontWeight = FontWeight.Normal,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (selectedDestination != null) {
                                onDestinationSelected(null)
                            } else {
                                onBack()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (selectedDestination != null)
                                KronoIcons.Navigation.Back
                            else
                                KronoIcons.Navigation.Back,
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        AnimatedContent(
            targetState = selectedDestination,
            transitionSpec = {
                if (targetState != null) {
                    (slideInHorizontally(
                        animationSpec = tween(KronoTokens.Animation.slideDurationMs),
                        initialOffsetX = { it }
                    ) + fadeIn(animationSpec = tween(KronoTokens.Animation.fadeDurationMs)))
                        .togetherWith(
                            slideOutHorizontally(
                                animationSpec = tween(KronoTokens.Animation.slideDurationMs),
                                targetOffsetX = { -it }
                            ) + fadeOut(animationSpec = tween(KronoTokens.Animation.fadeDurationMs))
                        )
                } else {
                    (slideInHorizontally(
                        animationSpec = tween(KronoTokens.Animation.slideDurationMs),
                        initialOffsetX = { -it }
                    ) + fadeIn(animationSpec = tween(KronoTokens.Animation.fadeDurationMs)))
                        .togetherWith(
                            slideOutHorizontally(
                                animationSpec = tween(KronoTokens.Animation.slideDurationMs),
                                targetOffsetX = { it }
                            ) + fadeOut(animationSpec = tween(KronoTokens.Animation.fadeDurationMs))
                        )
                }
            },
            label = "menu-panel-transition",
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { destination ->
            if (destination != null) {
                SettingsPanelHost(
                    destination = destination,
                    config = config,
                    dataStore = dataStore,
                    scope = scope,
                    totalLifetimeMs = config.totalLifetimeMs,
                    pendingUpdateInfo = updateInfo,
                    isServiceRunning = isServiceRunning,
                    onStartFocusMode = onStartFocusMode,
                    onSupportClick = {},
                    onShowChangelog = { onDestinationSelected(SettingsDestination.Changelog) },
                    onUpdateAvailable = { onDestinationSelected(SettingsDestination.Changelog) },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                SettingsMenuPanel(
                    selectedDestination = null,
                    onDestinationSelected = { onDestinationSelected(it) },
                    hasPendingUpdate = updateInfo != null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}


