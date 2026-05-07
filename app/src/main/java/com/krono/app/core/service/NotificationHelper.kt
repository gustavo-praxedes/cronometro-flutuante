package com.krono.app.core.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.krono.app.*
import com.krono.app.feature.countdown.CountdownState
import com.krono.app.feature.stopwatch.StopwatchState
import com.krono.app.core.data.toFormattedTime
import com.krono.app.core.receiver.NotificationActionReceiver
import com.krono.app.MainActivity
import com.krono.app.feature.countdown.CountdownViewModel

class NotificationHelper(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val contentPendingIntent: PendingIntent = PendingIntent.getActivity(
        context, 0,
        Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    fun buildNotification(
        StopwatchState: StopwatchState,
        showHours: Boolean,
        showSeconds: Boolean
    ): Notification {

        fun actionIntent(action: String, requestCode: Int): PendingIntent {
            val i = Intent(context, NotificationActionReceiver::class.java).apply {
                this.action = action
            }
            return PendingIntent.getBroadcast(
                context, requestCode, i,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentIntent(contentPendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .addAction(
                if (StopwatchState.isRunning) android.R.drawable.ic_media_pause
                else android.R.drawable.ic_media_play,
                if (StopwatchState.isRunning) context.getString(R.string.action_pause)
                else context.getString(R.string.action_play),
                if (StopwatchState.isRunning) actionIntent(ACTION_PAUSE, 1)
                else actionIntent(ACTION_PLAY, 2)
            )
            .addAction(
                android.R.drawable.ic_menu_revert,
                context.getString(R.string.action_reset),
                actionIntent(ACTION_RESET, 3)
            )
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                context.getString(R.string.action_stop_service),
                actionIntent(ACTION_STOP_SERVICE, 4)
            )

        if (StopwatchState.isRunning && StopwatchState.startTime != -1L) {
            val elapsedSinceStart = System.currentTimeMillis() - StopwatchState.startTime
            val totalElapsed      = StopwatchState.pauseOffset + elapsedSinceStart
            val whenMs            = System.currentTimeMillis() - totalElapsed

            builder
                .setUsesChronometer(true)
                .setChronometerCountDown(false)
                .setWhen(whenMs)
                .setShowWhen(true)
                .setContentText(context.getString(R.string.notification_text_running))

        } else {
            val frozenTime = StopwatchState.elapsedMs.toFormattedTime(
                showHours   = showHours,
                showSeconds = showSeconds
            )
            builder
                .setUsesChronometer(false)
                .setShowWhen(false)
                .setContentText(frozenTime)
        }

        return builder.build()
    }

    fun buildCountdownNotification(state: CountdownState): Notification {
        val id = state.config.id

        fun actionIntent(action: String, requestCode: Int): PendingIntent {
            val i = Intent(context, NotificationActionReceiver::class.java).apply {
                this.action = action
                putExtra(CountdownViewModel.EXTRA_COUNTDOWN_ID, id)
            }
            return PendingIntent.getBroadcast(
                context, requestCode, i,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val formattedTime = (state.remainingSeconds * 1000L).toFormattedTime(showHours = true, showSeconds = true)

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(state.config.description.ifBlank { "Cronômetro regressivo" })
            .setContentText(if (state.isCompleted) "⏰ Concluído!" else formattedTime)
            .setContentIntent(contentPendingIntent)
            .setOngoing(!state.isCompleted)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .addAction(
                if (state.isRunning) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play,
                if (state.isRunning) "Pausar" else "Iniciar",
                if (state.isRunning) actionIntent(CountdownViewModel.ACTION_COUNTDOWN_PAUSE, id.hashCode() + 1)
                else actionIntent(CountdownViewModel.ACTION_COUNTDOWN_PLAY, id.hashCode() + 2)
            )
            .addAction(
                android.R.drawable.ic_menu_revert,
                "Reset",
                actionIntent(CountdownViewModel.ACTION_COUNTDOWN_RESET, id.hashCode() + 3)
            )
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Fechar",
                actionIntent(CountdownViewModel.ACTION_COUNTDOWN_OVERLAY_HIDE, id.hashCode() + 4)
            )
            .build()
    }

    fun postCountdownNotification(state: CountdownState) {
        val notifId = COUNTDOWN_NOTIF_BASE_ID + state.config.id.hashCode()
        notificationManager.notify(notifId, buildCountdownNotification(state))
    }

    fun cancelCountdownNotification(id: String) {
        notificationManager.cancel(COUNTDOWN_NOTIF_BASE_ID + id.hashCode())
    }

    companion object {
        const val COUNTDOWN_NOTIF_BASE_ID = 1000
    }
}
