package com.krono.app.core.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log

object EnvironmentSoundLoop {
    private const val TAG = "EnvironmentSoundLoop"
    private const val STALE_AFTER_MS = 1_500L
    private const val FADE_STEP_MS = 20L

    private val handler = Handler(Looper.getMainLooper())
    private val lock = Any()

    private var player: MediaPlayer? = null
    private var nextPlayer: MediaPlayer? = null
    private var preparingResId: Int? = null
    private var prepareGeneration: Long = 0L
    private var lastResId: Int? = null
    private var lastVolume: Float? = null
    private var lastContext: Context? = null
    private var lastUsage: Int = AudioAttributes.USAGE_MEDIA
    private var lastStartOffsetMs: Long = 0L
    private var lastEndTrimMs: Long = 0L
    private var lastCrossfadeMs: Long = 0L
    private var lastNativeLoop: Boolean = false
    private var lastHeartbeatAtMs: Long = 0L
    private var lastStaleAfterMs: Long = STALE_AFTER_MS
    private var pendingStart: Runnable? = null
    private var pendingRestart: Runnable? = null

    private val staleCheck = Runnable {
        val now = SystemClock.elapsedRealtime()
        synchronized(lock) {
            if (player == null) return@synchronized
            if (now - lastHeartbeatAtMs > lastStaleAfterMs) {
                stopLocked("stale")
            }
        }
    }

    fun heartbeat(
        context: Context,
        resId: Int,
        volume: Float,
        usage: Int = AudioAttributes.USAGE_MEDIA,
        startDelayMs: Long = 0L,
        staleAfterMs: Long = STALE_AFTER_MS,
        startOffsetMs: Long = 0L,
        endTrimMs: Long = 0L,
        crossfadeMs: Long = 0L,
        nativeLoop: Boolean = false
    ) {
        val safeVolume = volume.coerceIn(0f, 1f)
        if (safeVolume <= 0f) {
            stop("volume=0")
            return
        }
        val appContext = context.applicationContext
        val now = SystemClock.elapsedRealtime()
        synchronized(lock) {
            lastHeartbeatAtMs = now
            lastStaleAfterMs = staleAfterMs.coerceAtLeast(0L).takeIf { it > 0L } ?: STALE_AFTER_MS
            lastContext = appContext
            lastUsage = usage
            lastStartOffsetMs = startOffsetMs.coerceAtLeast(0L)
            lastEndTrimMs = endTrimMs.coerceAtLeast(0L)
            lastCrossfadeMs = crossfadeMs.coerceAtLeast(0L)
            lastNativeLoop = nativeLoop

            val needsRestart = lastResId != resId ||
                player?.isLooping != nativeLoop ||
                (player == null && preparingResId != resId)
            if (needsRestart) {
                stopLocked("restart")
                lastResId = resId
                lastVolume = safeVolume
                if (startDelayMs > 0L) {
                    val delayedStart = Runnable {
                        synchronized(lock) {
                            if (player == null && lastResId == resId) {
                                startLocked(appContext, resId, safeVolume, usage, lastStartOffsetMs, nativeLoop)
                            }
                            pendingStart = null
                        }
                    }
                    pendingStart = delayedStart
                    handler.postDelayed(delayedStart, startDelayMs)
                } else {
                    startLocked(appContext, resId, safeVolume, usage, lastStartOffsetMs, nativeLoop)
                }
            } else {
                if (lastVolume != safeVolume) {
                    runCatching { player?.setVolume(safeVolume, safeVolume) }
                    lastVolume = safeVolume
                }
            }

            handler.removeCallbacks(staleCheck)
            handler.postDelayed(staleCheck, lastStaleAfterMs + 100L)
        }
    }

    fun stop(reason: String = "stop") {
        synchronized(lock) {
            stopLocked(reason)
        }
    }

    private fun startLocked(context: Context, resId: Int, volume: Float, usage: Int, startOffsetMs: Long, nativeLoop: Boolean) {
        runCatching {
            val generation = ++prepareGeneration
            preparingResId = resId
            val asset = context.resources.openRawResourceFd(resId) ?: error("raw resource unavailable")
            val mp = MediaPlayer()
            try {
                mp.setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(usage)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                mp.setDataSource(asset.fileDescriptor, asset.startOffset, asset.length)
            } finally {
                asset.close()
            }
            mp.setVolume(volume, volume)
            mp.isLooping = nativeLoop
            mp.setOnPreparedListener { prepared ->
                synchronized(lock) {
                    if (generation != prepareGeneration || lastResId != resId || player != null) {
                        runCatching { prepared.release() }
                        return@synchronized
                    }
                    preparingResId = null
                    player = prepared
                    prepared.seekToCompat(startOffsetMs.coerceAtMost(prepared.duration.toLong().coerceAtLeast(0L)))
                    prepared.start()
                    if (!nativeLoop) scheduleRestartLocked(context, resId, volume)
                }
            }
            mp.setOnErrorListener { broken, _, _ ->
                synchronized(lock) {
                    if (generation == prepareGeneration) preparingResId = null
                    if (player === broken) player = null
                    if (nextPlayer === broken) nextPlayer = null
                }
                runCatching { broken.release() }
                true
            }
            mp.prepareAsync()
        }.onFailure { error ->
            preparingResId = null
            Log.e(TAG, "start failed resId=$resId", error)
            stopLocked("start failed")
        }
    }

    private fun scheduleRestartLocked(context: Context, resId: Int, volume: Float) {
        pendingRestart?.let { handler.removeCallbacks(it) }
        val durationMs = player?.duration?.toLong()?.coerceAtLeast(0L) ?: 0L
        if (durationMs <= 0L) return
        val playableMs = (durationMs - lastStartOffsetMs - lastEndTrimMs).coerceAtLeast(0L)
        val restartDelayMs = (playableMs - lastCrossfadeMs).coerceAtLeast(0L)
        val appContext = context.applicationContext
        val restart = Runnable {
            synchronized(lock) {
                if (player == null || lastResId != resId) {
                    pendingRestart = null
                    return@synchronized
                }
                startNextLocked(appContext, resId, volume)
                pendingRestart = null
            }
        }
        pendingRestart = restart
        handler.postDelayed(restart, restartDelayMs)
    }

    private fun startNextLocked(context: Context, resId: Int, volume: Float) {
        runCatching {
            val oldPlayer = player
            val generation = ++prepareGeneration
            val asset = context.resources.openRawResourceFd(resId) ?: error("raw resource unavailable")
            val incoming = MediaPlayer()
            try {
                incoming.setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(lastUsage)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                incoming.setDataSource(asset.fileDescriptor, asset.startOffset, asset.length)
            } finally {
                asset.close()
            }
            incoming.setVolume(0f, 0f)
            incoming.setOnPreparedListener { prepared ->
                synchronized(lock) {
                    if (generation != prepareGeneration || lastResId != resId || player !== oldPlayer) {
                        runCatching { prepared.release() }
                        return@synchronized
                    }
                    prepared.seekToCompat(lastStartOffsetMs.coerceAtMost(prepared.duration.toLong().coerceAtLeast(0L)))
                    nextPlayer = prepared
                    prepared.start()
                    if (lastCrossfadeMs > 0L && oldPlayer != null) {
                        crossfadeLocked(oldPlayer, prepared, volume, lastCrossfadeMs)
                    } else {
                        runCatching { oldPlayer?.release() }
                        player = prepared
                        nextPlayer = null
                        runCatching { prepared.setVolume(volume, volume) }
                        scheduleRestartLocked(context, resId, volume)
                    }
                }
            }
            incoming.setOnErrorListener { broken, _, _ ->
                synchronized(lock) {
                    if (nextPlayer === broken) nextPlayer = null
                }
                runCatching { broken.release() }
                true
            }
            incoming.prepareAsync()
        }.onFailure { error ->
            Log.e(TAG, "restart failed resId=$resId", error)
            stopLocked("restart failed")
        }
    }

    private fun crossfadeLocked(oldPlayer: MediaPlayer, incoming: MediaPlayer, targetVolume: Float, durationMs: Long) {
        val steps = (durationMs / FADE_STEP_MS).coerceAtLeast(1L).toInt()
        repeat(steps) { index ->
            handler.postDelayed(
                {
                    synchronized(lock) {
                        if (nextPlayer !== incoming) return@synchronized
                        val progress = (index + 1).toFloat() / steps.toFloat()
                        val inVolume = targetVolume * progress
                        val outVolume = targetVolume * (1f - progress)
                        runCatching { incoming.setVolume(inVolume, inVolume) }
                        runCatching { oldPlayer.setVolume(outVolume, outVolume) }
                        if (index == steps - 1) {
                            runCatching { oldPlayer.release() }
                            player = incoming
                            nextPlayer = null
                            lastVolume = targetVolume
                            scheduleRestartLocked(lastContext ?: return@synchronized, lastResId ?: return@synchronized, targetVolume)
                        }
                    }
                },
                FADE_STEP_MS * (index + 1)
            )
        }
    }

    private fun stopLocked(reason: String) {
        handler.removeCallbacks(staleCheck)
        pendingStart?.let { handler.removeCallbacks(it) }
        pendingRestart?.let { handler.removeCallbacks(it) }
        pendingStart = null
        pendingRestart = null
        val mp = player
        val next = nextPlayer
        prepareGeneration++
        player = null
        nextPlayer = null
        preparingResId = null
        lastResId = null
        lastVolume = null
        lastContext = null
        lastUsage = AudioAttributes.USAGE_MEDIA
        lastStartOffsetMs = 0L
        lastEndTrimMs = 0L
        lastCrossfadeMs = 0L
        lastNativeLoop = false
        lastStaleAfterMs = STALE_AFTER_MS
        if (mp != null) {
            runCatching { if (mp.isPlaying) mp.stop() }
            runCatching { mp.release() }
        }
        if (next != null) {
            runCatching { if (next.isPlaying) next.stop() }
            runCatching { next.release() }
        }
        Log.d(TAG, "stopped: $reason")
    }

    @Suppress("DEPRECATION")
    private fun MediaPlayer.seekToCompat(positionMs: Long) {
        if (positionMs <= 0L) return
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            seekTo(positionMs, MediaPlayer.SEEK_CLOSEST)
        } else {
            seekTo(positionMs.toInt())
        }
    }
}
