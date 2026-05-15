package com.krono.app

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import java.util.Locale
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.krono.app.core.data.OverlayConfig
import com.krono.app.core.data.OverlayDataStore
import com.krono.app.core.service.MainService
import com.krono.app.core.ui.theme.KronoTheme
import com.krono.app.core.util.UpdateInfo
import com.krono.app.feature.stopwatch.StopwatchViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val navigationEvents              = MutableSharedFlow<String>(extraBufferCapacity = 1)
    private val permissionsDialogEvents       = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    private val permissionsRefreshTrigger     = MutableStateFlow(0)

    private lateinit var dataStore: OverlayDataStore
    private val stopwatchViewModel: StopwatchViewModel
        get() = (application as KronoApp).stopwatchViewModel

    private val pendingUpdateInfo = MutableStateFlow<UpdateInfo?>(null)

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permissionsRefreshTrigger.value++ }

    private val overlayPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { permissionsRefreshTrigger.value++ }

    private val installPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { permissionsRefreshTrigger.value++ }

    override fun onResume() {
        super.onResume()
        permissionsRefreshTrigger.value++

        val lacksOverlay      = !Settings.canDrawOverlays(this)
        val lacksNotification = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) !=
                android.content.pm.PackageManager.PERMISSION_GRANTED

        if (lacksOverlay || lacksNotification) {
            lifecycleScope.launch { permissionsDialogEvents.emit(Unit) }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.getBooleanExtra("open_settings", false)) {
            navigationEvents.tryEmit(AppRoutes.SETTINGS)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataStore = OverlayDataStore(this)

        onBackPressedDispatcher.addCallback(this) {
            isEnabled = false
            onBackPressedDispatcher.onBackPressed()
        }

        setContent {
            val config by dataStore.configFlow.collectAsState(initial = OverlayConfig())
            LaunchedEffect(config.appLanguage) {
                val localeTag = config.appLanguage.ifBlank { "pt-BR" }
                val locale = Locale.forLanguageTag(localeTag)
                Locale.setDefault(locale)
                val res = resources
                val conf = res.configuration
                conf.setLocale(locale)
                @Suppress("DEPRECATION")
                res.updateConfiguration(conf, res.displayMetrics)
            }

            KronoTheme(selectedTheme = config.selectedTheme, appFontSize = config.appFontSize) {
                val surfaceColor = MaterialTheme.colorScheme.background
                
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color    = surfaceColor
                ) {
                    AppNavigation(
                        dataStore                 = dataStore,
                        stopwatchViewModel            = stopwatchViewModel,
                        pendingUpdateInfo         = pendingUpdateInfo.collectAsState().value,
                        navigationEvents          = navigationEvents,
                        permissionsDialogEvents   = permissionsDialogEvents,
                        permissionsRefreshTrigger = permissionsRefreshTrigger.collectAsState().value,
                        isTaskRoot                = isTaskRoot,
                        startInSettings           = intent?.getBooleanExtra("open_settings", false) == true,
                        onTryStartService         = { tryStartService() },
                        onRequestNotification     = { requestNotificationPermission() },
                        onRequestOverlay          = { openOverlayPermissionSettings() },
                        onRequestInstall          = { openInstallPermissionSettings() },
                        onStartFocusMode          = { startFocusMode() },
                        onShowOverlay             = { showOverlay() },
                        onReset                   = { sendResetToService() },
                        isServiceRunning          = { isServiceRunning() }
                    )
                }
            }
        }
    }

    private fun tryStartService() {
        if (!Settings.canDrawOverlays(this)) {
            lifecycleScope.launch { permissionsDialogEvents.emit(Unit) }
            return
        }
        startServiceWithoutMinimize()
    }

    private fun startServiceWithoutMinimize() {
        lifecycleScope.launch {
            val config = dataStore.configFlow.first()
            val intent = Intent(this@MainActivity, MainService::class.java).apply {
                action = if (config.focusModeEnabled) ACTION_START_FOCUS else ACTION_SHOW_OVERLAY
            }
            startForegroundService(intent)
        }
    }

    fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    fun openOverlayPermissionSettings() {
        overlayPermissionLauncher.launch(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
    }

    fun openInstallPermissionSettings() {
        installPermissionLauncher.launch(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:$packageName")))
    }

    private fun sendResetToService() {
        startForegroundService(Intent(this, MainService::class.java).apply { action = ACTION_RESET })
    }

    private fun startFocusMode() {
        startForegroundService(Intent(this, MainService::class.java).apply { action = ACTION_START_FOCUS })
    }

    private fun showOverlay() {
        startForegroundService(Intent(this, MainService::class.java).apply { action = ACTION_SHOW_OVERLAY })
    }

    @Suppress("DEPRECATION")
    private fun isServiceRunning(): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as android.app.ActivityManager
        return manager.getRunningServices(Int.MAX_VALUE).any { it.service.className == MainService::class.java.name }
    }
}
