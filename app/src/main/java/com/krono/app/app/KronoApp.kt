package com.krono.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.google.firebase.FirebaseApp
import com.krono.app.feature.countdown.CountdownDataStore
import com.krono.app.feature.countdown.CountdownTool
import com.krono.app.feature.countdown.CountdownViewModel
import com.krono.app.feature.stopwatch.StopwatchViewModel
import com.krono.app.feature.stopwatch.StopwatchTool
import com.krono.app.feature.pomodoro.PomodoroTool
import com.krono.app.feature.pomodoro.PomodoroViewModel
import com.krono.app.core.tool.ToolRegistry
import com.krono.app.core.data.OverlayDataStore

const val NOTIFICATION_ID         = 1
const val CHANNEL_ID              = "timer_channel"
const val ACTION_PLAY             = "com.krono.app.ACTION_PLAY"
const val ACTION_PAUSE            = "com.krono.app.ACTION_PAUSE"
const val ACTION_RESET            = "com.krono.app.ACTION_RESET"
const val ACTION_STOP_SERVICE     = "com.krono.app.ACTION_STOP_SERVICE"
const val ACTION_SHOW_OVERLAY     = "com.krono.app.ACTION_SHOW_OVERLAY"
const val ACTION_HIDE_OVERLAY     = "com.krono.app.ACTION_HIDE_OVERLAY"
const val ACTION_START_FOCUS      = "com.krono.app.ACTION_START_FOCUS"
const val EXTRA_SHOW_DONATION     = "extra_show_donation"

class KronoApp : Application() {

    val stopwatchViewModel: StopwatchViewModel by lazy {
        StopwatchViewModel(this)
    }

    val countdownViewModel: CountdownViewModel by lazy {
        CountdownViewModel(CountdownDataStore(this))
    }
    val pomodoroViewModel: PomodoroViewModel by lazy { PomodoroViewModel() }

    override fun onCreate() {
        super.onCreate()
        
        try {
            FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        createNotificationChannel()

        ToolRegistry.register(StopwatchTool(OverlayDataStore(this), stopwatchViewModel))
        ToolRegistry.register(CountdownTool(countdownViewModel))
        ToolRegistry.register(PomodoroTool(pomodoroViewModel))
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name        = "Cronômetro Flutuante"
            val description = "Exibe o tempo e controles na barra de notificações"
            val importance  = NotificationManager.IMPORTANCE_LOW
            
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                this.description = description
                setShowBadge(false)
            }
            
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}

