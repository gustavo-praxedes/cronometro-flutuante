package com.krono.app.core.ui.settings

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.*
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import com.krono.app.R
import com.krono.app.core.data.OverlayConfig
import com.krono.app.core.data.OverlayDataStore
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoTokens
import com.krono.app.core.util.UpdateInfo

private val SettingsHeaderControlSize = 48.dp
private val SettingsHeaderIconSize = 24.dp
private val SettingsHeaderGap = 8.dp
private const val SettingsPanelSlideDurationMs = 100
private const val SettingsPanelFadeDurationMs = 250

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun SettingsScreen(
    dataStore         : OverlayDataStore,
    pendingUpdateInfo : UpdateInfo?,
    isServiceRunning  : () -> Boolean,
    isAnyToolRunning  : () -> Boolean,
    onStartFocusMode  : () -> Unit,
    onShowOverlay     : () -> Unit,
    onBack            : () -> Unit,
    modifier: Modifier = Modifier
) {
    val config = dataStore.configFlow.collectAsState<OverlayConfig, OverlayConfig?>(initial = null).value ?: return
    val scope       = rememberCoroutineScope()

    var selectedDestination by remember { mutableStateOf<SettingsDestination?>(null) }
    var updateInfo by remember { mutableStateOf<UpdateInfo?>(pendingUpdateInfo) }

    val configuration = LocalConfiguration.current
    val activity = LocalContext.current.findActivity()
    val widthClass = activity?.let { calculateWindowSizeClass(it).widthSizeClass }
    val isWideScreen = when (widthClass) {
        WindowWidthSizeClass.Expanded,
        WindowWidthSizeClass.Medium -> true
        WindowWidthSizeClass.Compact -> false
        else -> configuration.screenWidthDp >= 600
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
            isAnyToolRunning = isAnyToolRunning,
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
            isAnyToolRunning = isAnyToolRunning,
            onStartFocusMode = onStartFocusMode,
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
private fun WideScreenLayout(
    config: OverlayConfig,
    dataStore: OverlayDataStore,
    scope: kotlinx.coroutines.CoroutineScope,
    selectedDestination: SettingsDestination?,
    onDestinationSelected: (SettingsDestination?) -> Unit,
    updateInfo: UpdateInfo?,
    isServiceRunning: () -> Boolean,
    isAnyToolRunning: () -> Boolean,
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
                    .weight(0.40f)
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
                                start = KronoTokens.Settings.panelHorizontalInset - ((SettingsHeaderControlSize - SettingsHeaderIconSize) / 2),
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
                                    contentDescription = stringResource(R.string.action_back),
                                    modifier = Modifier.size(SettingsHeaderIconSize)
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
                        compactItems = true,
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
                    .weight(0.60f)
                    .fillMaxHeight()
            ) {
                AnimatedContent(
                    targetState = selectedDestination,
                    transitionSpec = {
                        settingsPanelContentTransition(forward = targetState != null)
                    },
                    label = "panel-transition",
                    modifier = Modifier
                        .fillMaxSize()
                        .clipToBounds()
                ) { destination ->
                    if (destination == null) {
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
                        Column(Modifier.fillMaxSize()) {
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
                                    text = stringResource(destination.titleRes),
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontSize = KronoTokens.Typography.dialogTitle
                                    ),
                                    fontWeight = FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            SettingsPanelHost(
                                destination = destination,
                                config = config,
                                dataStore = dataStore,
                                scope = scope,
                                totalLifetimeMs = config.totalLifetimeMs,
                                pendingUpdateInfo = updateInfo,
                                isServiceRunning = isServiceRunning,
                                isAnyToolRunning = isAnyToolRunning,
                                onStartFocusMode = onStartFocusMode,
                                onSupportClick = {},
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
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
    isAnyToolRunning: () -> Boolean,
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
                        },
                        modifier = Modifier.padding(
                            start = KronoTokens.Settings.panelHorizontalInset - ((SettingsHeaderControlSize - SettingsHeaderIconSize) / 2)
                        )
                    ) {
                        Icon(
                            imageVector = if (selectedDestination != null)
                                KronoIcons.Navigation.Back
                            else
                                KronoIcons.Navigation.Back,
                            contentDescription = stringResource(R.string.action_back),
                            modifier = Modifier.size(SettingsHeaderIconSize)
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
                settingsPanelContentTransition(forward = targetState != null)
            },
            label = "menu-panel-transition",
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .clipToBounds()
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
                    isAnyToolRunning = isAnyToolRunning,
                    onStartFocusMode = onStartFocusMode,
                    onSupportClick = {},
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

private fun settingsPanelContentTransition(forward: Boolean): ContentTransform {
    val slideSpec = tween<IntOffset>(SettingsPanelSlideDurationMs)
    val fadeInSpec = tween<Float>(SettingsPanelFadeDurationMs)
    val fadeOutSpec = tween<Float>(SettingsPanelFadeDurationMs)
    val enter = if (forward) {
        slideInHorizontally(
            animationSpec = slideSpec,
            initialOffsetX = { -it }
        )
    } else {
        slideInHorizontally(
            animationSpec = slideSpec,
            initialOffsetX = { it }
        )
    }
    val exit = if (forward) {
        slideOutHorizontally(
            animationSpec = slideSpec,
            targetOffsetX = { it }
        )
    } else {
        slideOutHorizontally(
            animationSpec = slideSpec,
            targetOffsetX = { -it }
        )
    }
    return ContentTransform(
        targetContentEnter = enter + fadeIn(animationSpec = fadeInSpec),
        initialContentExit = exit + fadeOut(animationSpec = fadeOutSpec),
        targetContentZIndex = 1f,
        sizeTransform = null
    )
}



private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

