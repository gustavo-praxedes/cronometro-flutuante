package com.krono.app

import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoTokens
import com.krono.app.core.ui.dialogs.PermissionsDialog
import com.krono.app.feature.stopwatch.StopwatchViewModel
import com.krono.app.feature.stopwatch.StopwatchScreen
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.krono.app.core.data.OverlayConfig
import com.krono.app.core.data.OverlayDataStore
import com.krono.app.core.util.UpdateInfo
import com.krono.app.feature.countdown.CountdownScreen
import com.krono.app.feature.pomodoro.PomodoroScreen
import com.krono.app.core.ui.settings.SettingsScreen
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

object AppRoutes {
    const val TIMER     = "timer"
    const val SETTINGS  = "settings"
    const val COUNTDOWN = "countdown"
    const val POMODORO = "pomodoro"
}

private data class BottomTab(
    val route     : String,
    val label     : String,
    val iconRes   : Int?,
    val iconVector: ImageVector? = null
)

private val BOTTOM_TABS = listOf(
    BottomTab(
        route       = AppRoutes.TIMER,
        label       = "Cronômetro",
        iconRes     = null,
        iconVector  = KronoIcons.Feature.Timer
    ),
    BottomTab(
        route       = AppRoutes.COUNTDOWN,
        label       = "Timer",
        iconRes     = null,
        iconVector  = KronoIcons.Feature.HourglassBottom
    ),
    BottomTab(
        route       = AppRoutes.POMODORO,
        label       = "Pomodoro",
        iconRes     = null,
        iconVector  = KronoIcons.Feature.Pomodoro
    )
)

@Composable
fun AppNavigation(
    dataStore                 : OverlayDataStore,
    stopwatchViewModel            : StopwatchViewModel,
    pendingUpdateInfo         : UpdateInfo?,
    navigationEvents          : SharedFlow<String>,
    permissionsDialogEvents   : SharedFlow<Unit>,
    permissionsRefreshTrigger : Int,
    isTaskRoot                : Boolean,
    startInSettings           : Boolean,
    onTryStartService         : () -> Unit,
    onRequestNotification     : () -> Unit,
    onRequestOverlay          : () -> Unit,
    onRequestInstall          : () -> Unit,
    onStartFocusMode          : () -> Unit,
    onShowOverlay             : () -> Unit,
    onReset                   : () -> Unit,
    isServiceRunning          : () -> Boolean
) {
    val navController = rememberNavController()
    val stopwatchState    by stopwatchViewModel.StopwatchState.collectAsState()
    val context       = LocalContext.current
    val scope         = rememberCoroutineScope()
    val config        by dataStore.configFlow.collectAsState(initial = OverlayConfig())

    var showPermissionsDialog by remember { mutableStateOf(false) }

    val hasOverlayPermission = remember(permissionsRefreshTrigger) { Settings.canDrawOverlays(context) }
    val hasNotificationPermission = remember(permissionsRefreshTrigger) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) ==
                    android.content.pm.PackageManager.PERMISSION_GRANTED
        else true
    }
    val hasInstallPermission = remember(permissionsRefreshTrigger) {
        context.packageManager.canRequestPackageInstalls()
    }

    LaunchedEffect(Unit) {
        launch { navigationEvents.collect { route -> navController.navigate(route) { launchSingleTop = true } } }
        launch { permissionsDialogEvents.collect { showPermissionsDialog = true } }
        val cfg = dataStore.configFlow.first()
        if (cfg.autoLaunch && !isTaskRoot) onTryStartService()
    }

    val currentRoute by navController.currentBackStackEntryAsState()
    val currentDest  = currentRoute?.destination?.route

    val showBottomBar = currentDest == AppRoutes.TIMER || currentDest == AppRoutes.COUNTDOWN || currentDest == AppRoutes.POMODORO

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                KronoBottomBar(
                    tabs         = BOTTOM_TABS,
                    currentRoute = currentDest,
                    onTabSelected = { route ->
                        if (route != currentDest) {
                            navController.navigate(route) {
                                popUpTo(AppRoutes.TIMER) { saveState = true }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->

        val isSettingsRoute = currentDest == AppRoutes.SETTINGS

        NavHost(
            navController    = navController,
            startDestination = if (startInSettings) AppRoutes.SETTINGS else AppRoutes.TIMER,
            modifier         = if (showBottomBar) Modifier.padding(innerPadding) else Modifier
        ) {
            composable(AppRoutes.TIMER) {
                StopwatchScreen(
                    state          = stopwatchState,
                    selectedFont   = config.selectedFont,
                    onStart        = { stopwatchViewModel.start() },
                    onPause        = { stopwatchViewModel.pause() },
                    onReset        = { stopwatchViewModel.reset() },
                    onOpenOverlay  = {
                        scope.launch {
                            dataStore.updateConfig(config.copy(activeToolId = "stopwatch"))
                            onTryStartService()
                        }
                    },
                    onOpenSettings = { navController.navigate(AppRoutes.SETTINGS) }
                )
            }

            composable(AppRoutes.COUNTDOWN) {
                CountdownScreen(
                    viewModel      = (context.applicationContext as KronoApp).countdownViewModel,
                    onOpenSettings = { navController.navigate(AppRoutes.SETTINGS) }
                )
            }

            composable(AppRoutes.SETTINGS) {
                val navigateBack = {
                    if (!navController.popBackStack()) {
                        navController.navigate(AppRoutes.TIMER) {
                            popUpTo(AppRoutes.SETTINGS) { inclusive = true }
                        }
                    }
                }
                BackHandler(onBack = navigateBack)
                SettingsScreen(
                    dataStore         = dataStore,
                    pendingUpdateInfo  = pendingUpdateInfo,
                    isServiceRunning  = isServiceRunning,
                    onStartFocusMode  = onStartFocusMode,
                    onShowOverlay     = onShowOverlay,
                    onBack            = navigateBack
                )
            }
            composable(AppRoutes.POMODORO) {
                PomodoroScreen(
                    viewModel = (context.applicationContext as KronoApp).pomodoroViewModel,
                    onOpenSettings = { navController.navigate(AppRoutes.SETTINGS) },
                    selectedFont = config.selectedFont,
                    onOpenOverlay = {
                        scope.launch {
                            dataStore.updateConfig(config.copy(activeToolId = "pomodoro"))
                            onTryStartService()
                        }
                    }
                )
            }
        }
    }

    if (showPermissionsDialog) {
        PermissionsDialog(
            hasNotificationPermission = hasNotificationPermission,
            hasOverlayPermission      = hasOverlayPermission,
            hasInstallPermission      = hasInstallPermission,
            onRequestNotification     = onRequestNotification,
            onRequestOverlay          = onRequestOverlay,
            onRequestInstall          = onRequestInstall,
            onDismiss                 = { showPermissionsDialog = false }
        )
    }
}

@Composable
private fun KronoBottomBar(
    tabs         : List<BottomTab>,
    currentRoute : String?,
    onTabSelected: (String) -> Unit
) {
    Surface(
        color    = MaterialTheme.colorScheme.primaryContainer.copy(alpha = KronoTokens.BottomBar.alphaContainer),
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .height(KronoTokens.BottomBar.height)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            tabs.forEach { tab ->
                val selected = currentRoute == tab.route

                val tint by animateColorAsState(
                    targetValue = if (selected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    animationSpec = tween(KronoTokens.Animation.fadeDurationMs),
                    label = "tab_tint"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(
                            if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = KronoTokens.BottomBar.alphaSelected)
                            else Color.Transparent
                        )
                        .clickable { onTabSelected(tab.route) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement   = Arrangement.Center
                    ) {
                        val icon = when (tab.route) {
                            AppRoutes.TIMER     -> KronoIcons.Feature.Timer
                            AppRoutes.COUNTDOWN -> KronoIcons.Feature.HourglassBottom
                            AppRoutes.POMODORO  -> KronoIcons.Feature.Pomodoro
                            else                -> tab.iconVector ?: ImageVector.vectorResource(id = tab.iconRes!!)
                        }

                        Icon(
                            imageVector        = icon,
                            contentDescription = tab.label,
                            tint               = tint,
                            modifier           = Modifier.size(KronoTokens.BottomBar.iconSize)
                        )

                        Text(
                            text       = tab.label,
                            fontSize   = KronoTokens.BottomBar.labelSize,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                            color      = tint
                        )
                    }
                }
            }
        }
    }
}
