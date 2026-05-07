package com.krono.app.core.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.krono.app.TransparentProxyActivity

object PermissionUtils {

    fun hasEssentialPermissions(context: Context): Boolean {
        val lacksOverlay = !Settings.canDrawOverlays(context)
        val lacksNotification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context, 
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        } else false

        return !lacksOverlay && !lacksNotification
    }

    fun requestEssentialPermissions(context: Context) {
        TransparentProxyActivity.start(context, TransparentProxyActivity.TYPE_PERMISSIONS)
    }
}
