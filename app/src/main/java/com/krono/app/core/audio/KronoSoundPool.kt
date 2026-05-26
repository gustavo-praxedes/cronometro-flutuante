package com.krono.app.core.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log

object KronoSoundPool {
    private const val TAG = "KronoSoundPool"
    private var soundPool: SoundPool? = null
    private val soundMap = mutableMapOf<Int, Int>() // resId -> soundId
    private val loadedIds = mutableSetOf<Int>()

    fun init(context: Context) {
        if (soundPool != null) return

        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(attrs)
            .build()

        soundPool?.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0) {
                loadedIds.add(sampleId)
            } else {
                Log.e(TAG, "Failed to load sound sampleId=$sampleId status=$status")
            }
        }

        // Preload second-tick sounds
        preload(context.applicationContext, KronoSoundCatalog.environmentResId("krono_env_ticking"))
        preload(context.applicationContext, KronoSoundCatalog.environmentResId("krono_env_fastticking"))
        
        // Preload play/pause beeps
        KronoSoundCatalog.playPause.forEach { preload(context.applicationContext, it.rawResId) }
    }

    private fun preload(context: Context, resId: Int) {
        if (soundMap.containsKey(resId)) return
        val sId = soundPool?.load(context, resId, 1) ?: return
        soundMap[resId] = sId
    }

    fun play(context: Context, resId: Int, volume: Float): Int {
        if (soundPool == null) init(context.applicationContext)
        if (!soundMap.containsKey(resId)) preload(context.applicationContext, resId)
        return play(resId, volume)
    }

    private fun play(resId: Int, volume: Float): Int {
        val sId = soundMap[resId] ?: return 0
        if (!loadedIds.contains(sId)) {
            Log.w(TAG, "Sound not loaded yet: resId=$resId")
            return 0
        }
        return soundPool?.play(sId, volume, volume, 1, 0, 1f) ?: 0
    }

    fun stop(streamId: Int) {
        if (streamId > 0) soundPool?.stop(streamId)
    }

    fun release() {
        soundPool?.release()
        soundPool = null
        soundMap.clear()
        loadedIds.clear()
    }
}
