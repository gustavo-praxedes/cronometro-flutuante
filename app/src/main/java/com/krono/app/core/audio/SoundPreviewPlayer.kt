package com.krono.app.core.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log

object SoundPreviewPlayer {
    private const val TAG = "SoundPreviewPlayer"
    private val handler = Handler(Looper.getMainLooper())
    private val lock = Any()
    private var player: MediaPlayer? = null
    private var pendingStart: Runnable? = null

    fun play(
        context: Context,
        resId: Int,
        volume: Float,
        usage: Int,
        startDelayMs: Long = 0L,
        startOffsetMs: Long = 0L,
        previewMaxMs: Long = 0L
    ) {
        val safeVolume = volume.coerceIn(0f, 1f)
        if (safeVolume <= 0f) {
            stop("volume=0")
            return
        }
        val appContext = context.applicationContext
        synchronized(lock) {
            stopLocked("restart")
            if (startDelayMs > 0L) {
                val delayedStart = Runnable {
                    synchronized(lock) {
                        startLocked(appContext, resId, safeVolume, usage, startOffsetMs, previewMaxMs)
                        pendingStart = null
                    }
                }
                pendingStart = delayedStart
                handler.postDelayed(delayedStart, startDelayMs)
            } else {
                startLocked(appContext, resId, safeVolume, usage, startOffsetMs, previewMaxMs)
            }
        }
    }

    private fun startLocked(
        context: Context,
        resId: Int,
        volume: Float,
        usage: Int,
        startOffsetMs: Long,
        previewMaxMs: Long
    ) {
        runCatching {
            val asset = context.resources.openRawResourceFd(resId) ?: return
            try {
                val mediaPlayer = MediaPlayer()
                mediaPlayer.setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(usage)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                mediaPlayer.setDataSource(asset.fileDescriptor, asset.startOffset, asset.length)
                mediaPlayer.isLooping = false
                mediaPlayer.setVolume(volume, volume)
                mediaPlayer.setOnCompletionListener { completed ->
                    synchronized(lock) {
                        if (player === completed) player = null
                    }
                    completed.release()
                }
                mediaPlayer.setOnErrorListener { broken, _, _ ->
                    synchronized(lock) {
                        if (player === broken) player = null
                    }
                    broken.release()
                    true
                }
                mediaPlayer.prepare()
                mediaPlayer.seekToCompat(startOffsetMs.coerceAtMost(mediaPlayer.duration.toLong().coerceAtLeast(0L)))
                mediaPlayer.start()
                player = mediaPlayer
                val safePreviewMaxMs = previewMaxMs.takeIf { it > 0L }
                if (safePreviewMaxMs != null) {
                    handler.postDelayed(
                        {
                            synchronized(lock) {
                                if (player === mediaPlayer) {
                                    player = null
                                    runCatching { mediaPlayer.release() }
                                }
                            }
                        },
                        safePreviewMaxMs
                    )
                }
            } finally {
                asset.close()
            }
        }.onFailure { error ->
            Log.e(TAG, "preview failed resId=$resId", error)
            stopLocked("error")
        }
    }

    fun stop(reason: String = "stop") {
        synchronized(lock) {
            stopLocked(reason)
        }
    }

    private fun stopLocked(reason: String) {
        pendingStart?.let { handler.removeCallbacks(it) }
        pendingStart = null
        val mediaPlayer = player
        player = null
        if (mediaPlayer != null) {
            runCatching { if (mediaPlayer.isPlaying) mediaPlayer.stop() }
            runCatching { mediaPlayer.release() }
        }
        Log.d(TAG, "stopped: $reason")
    }

    @Suppress("DEPRECATION")
    private fun MediaPlayer.seekToCompat(positionMs: Long) {
        if (positionMs <= 0L) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            seekTo(positionMs, MediaPlayer.SEEK_CLOSEST)
        } else {
            seekTo(positionMs.toInt())
        }
    }
}
