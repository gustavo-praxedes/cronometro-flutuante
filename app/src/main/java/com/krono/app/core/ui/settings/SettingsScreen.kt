package com.krono.app.core.ui.settings

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krono.app.R
import com.krono.app.core.data.OverlayConfig
import com.krono.app.core.data.OverlayDataStore
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoTokens
import com.krono.app.core.util.UpdateInfo

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
    val systemIsDark = androidx.compose.foundation.isSystemInDarkTheme()

    var selectedDestination by remember { mutableStateOf<SettingsDestination?>(null) }
    var changelogInfo by remember { mutableStateOf<UpdateInfo?>(null) }
    var updateInfo by remember { mutableStateOf<UpdateInfo?>(pendingUpdateInfo) }

    val configuration = LocalConfiguration.current
    val isWideScreen = configuration.screenWidthDp >= 600

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
    onDestinationSelected: (SettingsDestination) -> Unit,
    updateInfo: UpdateInfo?,
    isServiceRunning: () -> Boolean,
    onStartFocusMode: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = KronoIcons.Navigation.Back,
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SettingsMenuPanel(
                selectedDestination = selectedDestination,
                onDestinationSelected = onDestinationSelected,
                hasPendingUpdate = updateInfo != null,
                modifier = Modifier
                    .weight(0.35f)
                    .fillMaxHeight()
            )

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            val targetDestination = selectedDestination ?: SettingsDestination.Appearance

            AnimatedContent(
                targetState = targetDestination,
                transitionSpec = {
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
                },
                label = "panel-transition",
                modifier = Modifier
                    .weight(0.65f)
                    .fillMaxHeight()
            ) { destination ->
                SettingsPanelHost(
                    destination = destination,
                    config = config,
                    dataStore = dataStore,
                    scope = scope,
                    totalLifetimeMs = 0L,
                    pendingUpdateInfo = updateInfo,
                    isServiceRunning = isServiceRunning,
                    onStartFocusMode = onStartFocusMode,
                    onSupportClick = { onDestinationSelected(SettingsDestination.Support) },
                    onShowChangelog = { info -> onDestinationSelected(SettingsDestination.Changelog) },
                    onUpdateAvailable = { info -> onDestinationSelected(SettingsDestination.Updates) },
                    modifier = Modifier.fillMaxSize()
                )
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
    onDestinationSelected: (SettingsDestination) -> Unit,
    onBack: () -> Unit,
    updateInfo: UpdateInfo?,
    isServiceRunning: () -> Boolean,
    onStartFocusMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (selectedDestination != null)
                            stringResource(selectedDestination.titleRes)
                        else
                            stringResource(R.string.settings_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (selectedDestination != null) {
                                onDestinationSelected(null as SettingsDestination)
                            } else {
                                onBack()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (selectedDestination != null)
                                KronoIcons.Navigation.Close
                            else
                                KronoIcons.Navigation.Back,
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        AnimatedContent(
            targetState = selectedDestination,
            transitionSpec = {
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
                    totalLifetimeMs = 0L,
                    pendingUpdateInfo = updateInfo,
                    isServiceRunning = isServiceRunning,
                    onStartFocusMode = onStartFocusMode,
                    onSupportClick = { onDestinationSelected(SettingsDestination.Support) },
                    onShowChangelog = { info -> onDestinationSelected(SettingsDestination.Changelog) },
                    onUpdateAvailable = { info -> onDestinationSelected(SettingsDestination.Updates) },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                SettingsMenuPanel(
                    selectedDestination = null,
                    onDestinationSelected = onDestinationSelected,
                    hasPendingUpdate = updateInfo != null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}