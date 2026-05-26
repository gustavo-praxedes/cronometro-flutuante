package com.krono.app

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.krono.app.core.data.OverlayConfig
import com.krono.app.core.data.OverlayDataStore
import com.krono.app.core.service.MainService
import com.krono.app.core.ui.theme.KronoTheme
import com.krono.app.core.ui.dialogs.PermissionsDialog
import com.krono.app.core.util.PermissionUtils
import com.krono.app.core.util.UpdateInfo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.Serializable

/**
 * Activity Proxy unificada: Gerencia o lançamento do ícone (Launcher) 
 * e a exibição de diálogos disparados em background.
 */
class TransparentProxyActivity : ComponentActivity() {

    private lateinit var dataStore: OverlayDataStore
    private val permissionsRefreshTrigger = mutableIntStateOf(0)

    private val notificationLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        permissionsRefreshTrigger.intValue++
    }
    private val overlayLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        permissionsRefreshTrigger.intValue++
    }
    private val installLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        permissionsRefreshTrigger.intValue++
    }

    private fun openKofi() {
        startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse("https://ko-fi.com/gustavopraxedes")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }

    companion object {
        private const val EXTRA_TYPE = "extra_type"
        private const val EXTRA_DATA = "extra_data"

        const val TYPE_LAUNCHER    = "launcher"
        const val TYPE_PERMISSIONS = "permissions"
        const val TYPE_UPDATE      = "update"
        const val TYPE_DONATION    = "donation"

        fun start(context: android.content.Context, type: String, data: Serializable? = null) {
            val intent = Intent(context, TransparentProxyActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                putExtra(EXTRA_TYPE, type)
                if (data != null) putExtra(EXTRA_DATA, data)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataStore = OverlayDataStore(this)
        
        // Se vier do ícone do app, o action será MAIN
        val isLauncherCall = intent.action == Intent.ACTION_MAIN || intent.getStringExtra(EXTRA_TYPE) == TYPE_LAUNCHER
        
        if (isLauncherCall) {
            handleLauncherLogic()
            return
        }

        setupDialogUI()
    }

    private fun handleLauncherLogic() {
        val config = runBlocking { dataStore.configFlow.first() }
        
        if (config.autoLaunch && PermissionUtils.hasEssentialPermissions(this)) {
            startForegroundService(
                Intent(this, MainService::class.java).apply {
                    action = ACTION_SHOW_OVERLAY
                    putExtra(EXTRA_TOOL_ID, config.directLaunchToolId.ifBlank { config.activeToolId })
                }
            )
        } else {
            startActivity(Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            })
        }
        finish()
    }

    private fun setupDialogUI() {
        val type = intent.getStringExtra(EXTRA_TYPE) ?: ""
        @Suppress("DEPRECATION")
        val rawData = intent.getSerializableExtra(EXTRA_DATA)
        val updateData = rawData as? UpdateInfo

        setContent {
            val config by dataStore.configFlow.collectAsState(initial = OverlayConfig())
            val trigger by remember { permissionsRefreshTrigger }
            val scope = rememberCoroutineScope()
            
            val hasOverlay = remember(trigger) { Settings.canDrawOverlays(this) }
            val hasNotification = remember(trigger) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == 
                        android.content.pm.PackageManager.PERMISSION_GRANTED
                } else true
            }
            val hasInstall = remember(trigger) { packageManager.canRequestPackageInstalls() }

            KronoTheme(selectedTheme = config.selectedTheme, appFontSize = config.appFontSize) {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
                    when (type) {
                        TYPE_PERMISSIONS -> PermissionsDialog(
                            hasNotificationPermission = hasNotification,
                            hasOverlayPermission      = hasOverlay,
                            hasInstallPermission      = hasInstall,
                            onRequestNotification     = { if (Build.VERSION.SDK_INT >= 33) notificationLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS) },
                            onRequestOverlay          = { overlayLauncher.launch(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))) },
                            onRequestInstall          = { installLauncher.launch(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:$packageName"))) },
                            onDismiss                 = { finish() }
                        )
                        TYPE_UPDATE -> finish()
                        TYPE_DONATION -> {
                            AlertDialog(
                                onDismissRequest = {
                                    scope.launch { dataStore.resetDonationCycle() }
                                    finish()
                                },
                                title = { Text(text = getString(R.string.support_title)) },
                                text = {
                                    Text(
                                        text = getString(
                                            R.string.support_message_with_time,
                                            com.krono.app.core.data.formatLifetimeDetailed(config.totalLifetimeMs)
                                        )
                                    )
                                },
                                dismissButton = {
                                    TextButton(
                                        onClick = {
                                            scope.launch { dataStore.resetDonationCycle() }
                                            finish()
                                        }
                                    ) {
                                        Text(text = getString(R.string.action_cancel))
                                    }
                                },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            openKofi()
                                            scope.launch { dataStore.resetDonationCycle() }
                                            finish()
                                        }
                                    ) {
                                        Text(text = getString(R.string.support_button))
                                    }
                                }
                            )
                        }
                        else -> finish()
                    }
                }
            }
        }
    }
}

