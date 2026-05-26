package com.krono.app.core.util

import android.content.Context
import android.content.Intent
import com.krono.app.MainActivity
import com.krono.app.TransparentProxyActivity
import com.krono.app.FocusActivity

/**
 * Centraliza toda a navegação disparada pelo serviço ou receivers.
 * Diferente do AppNavigation (que cuida da hierarquia de telas), 
 * o KronoNavigator cuida do "disparo" de intenções externas.
 */
object KronoNavigator {

    fun openSettings(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("open_settings", true)
        }
        context.startActivity(intent)
    }

    fun openTool(context: Context, toolId: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("open_tool", toolId)
        }
        context.startActivity(intent)
    }

    fun showDonation(context: Context) {
        TransparentProxyActivity.start(
            context, 
            TransparentProxyActivity.TYPE_DONATION
        )
    }

    fun startFocusMode(context: Context) {
        val intent = Intent(context, FocusActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        }
        context.startActivity(intent)
    }

    fun requestPermissions(context: Context) {
        TransparentProxyActivity.start(context, TransparentProxyActivity.TYPE_PERMISSIONS)
    }
}

