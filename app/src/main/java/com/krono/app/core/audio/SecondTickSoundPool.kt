package com.krono.app.core.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.SystemClock
import android.util.Log

object SecondTickSoundPool {
    private const val TAG = "SecondTickSoundPool"
    private const val LOAD_MISS_LOG_INTERVAL_MS = 2_000L
    private val lock = Any()

    private var soundPool: SoundPool? = null
    private var selectedResId: Int? = null
    private var selectedSoundId: Int = 0
    private var loadedSoundId: Int = 0
    private var lastStreamId: Int = 0
    private var lastLoadMissLogAtMs: Long = 0L

    fun prepare(context: Context, resId: Int) {
        val appContext = context.applicationContext
        synchronized(lock) {
            val pool = ensurePoolLocked()
            if (selectedResId == resId && selectedSoundId != 0) return
            unloadSelectedLocked(pool)
            selectedResId = resId
            selectedSoundId = pool.load(appContext, resId, 1)
            loadedSoundId = 0
        }
    }

    fun playSelected(context: Context, resId: Int, volume: Float): Int {
        val safeVolume = volume.coerceIn(0f, 1f)
        if (safeVolume <= 0f) return 0

        val pool: SoundPool
        val soundId: Int
        synchronized(lock) {
            pool = ensurePoolLocked()
            if (selectedResId != resId || selectedSoundId == 0) {
                unloadSelectedLocked(pool)
                selectedResId = resId
                selectedSoundId = pool.load(context.applicationContext, resId, 1)
                loadedSoundId = 0
            }
            soundId = selectedSoundId
            if (loadedSoundId != soundId) {
                logLoadMissLocked(resId)
                return 0
            }
        }

        val streamId = pool.play(soundId, safeVolume, safeVolume, 1, 0, 1f)
        if (streamId > 0) {
            synchronized(lock) {
                lastStreamId = streamId
            }
        }
        return streamId
    }

    fun stopActiveStream() {
        synchronized(lock) {
            val streamId = lastStreamId
            lastStreamId = 0
            if (streamId > 0) soundPool?.stop(streamId)
        }
    }

    fun clearSelected() {
        synchronized(lock) {
            unloadSelectedLocked(soundPool)
        }
    }

    fun release() {
        synchronized(lock) {
            soundPool?.release()
            soundPool = null
            selectedResId = null
            selectedSoundId = 0
            loadedSoundId = 0
            lastStreamId = 0
        }
    }

    private fun ensurePoolLocked(): SoundPool {
        soundPool?.let { return it }

        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        return SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(attrs)
            .build()
            .also { pool ->
                soundPool = pool
                pool.setOnLoadCompleteListener { _, sampleId, status ->
                    synchronized(lock) {
                        if (sampleId != selectedSoundId) return@setOnLoadCompleteListener
                        if (status == 0) {
                            loadedSoundId = sampleId
                        } else {
                            selectedResId = null
                            selectedSoundId = 0
                            loadedSoundId = 0
                            Log.e(TAG, "Failed to load second tick sampleId=$sampleId status=$status")
                        }
                    }
                }
            }
    }

    private fun unloadSelectedLocked(pool: SoundPool?) {
        if (lastStreamId > 0) pool?.stop(lastStreamId)
        if (selectedSoundId != 0) pool?.unload(selectedSoundId)
        selectedResId = null
        selectedSoundId = 0
        loadedSoundId = 0
        lastStreamId = 0
    }

    private fun logLoadMissLocked(resId: Int) {
        val now = SystemClock.elapsedRealtime()
        if (now - lastLoadMissLogAtMs < LOAD_MISS_LOG_INTERVAL_MS) return
        lastLoadMissLogAtMs = now
        Log.w(TAG, "Second tick not loaded yet: resId=$resId")
    }
}
