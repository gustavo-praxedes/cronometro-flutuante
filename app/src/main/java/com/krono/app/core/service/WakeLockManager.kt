package com.krono.app.core.service

import android.content.Context
import android.os.PowerManager

/**
 * Gerencia o WakeLock do sistema para manter o processador ativo 
 * e a tela ligada conforme configuração.
 */
class WakeLockManager(private val context: Context) {

    private var wakeLock: PowerManager.WakeLock? = null

    fun applyWakeLock(enable: Boolean) {
        if (enable) {
            if (wakeLock?.isHeld != true) {
                val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
                wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Krono::WakeLock").apply {
                    acquire(99 * 3600_000L) // Limite longo de segurança
                }
            }
        } else {
            release()
        }
    }

    fun release() {
        if (wakeLock?.isHeld == true) {
            wakeLock?.release()
        }
        wakeLock = null
    }
}
