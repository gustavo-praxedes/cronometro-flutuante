package com.krono.app.core.util

import android.app.NotificationManager
import android.content.Context
import android.os.Build

fun applyPomodoroDnd(context: Context, enable: Boolean) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
    if (!manager.isNotificationPolicyAccessGranted) return
    val targetFilter = if (enable) {
        NotificationManager.INTERRUPTION_FILTER_PRIORITY
    } else {
        NotificationManager.INTERRUPTION_FILTER_ALL
    }
    runCatching {
        if (manager.currentInterruptionFilter != targetFilter) {
            manager.setInterruptionFilter(targetFilter)
        }
    }
}
