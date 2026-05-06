package com.krono.app.feature.countdown

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.countdownDataStore by preferencesDataStore(name = "countdown_configs")

class CountdownDataStore(private val context: Context) {

    companion object {
        private val KEY_CONFIGS = stringPreferencesKey("countdown_configs")
        private val json = Json { ignoreUnknownKeys = true }
    }

    val configs: Flow<List<CountdownConfig>> = context.countdownDataStore.data.map { prefs ->
        val raw = prefs[KEY_CONFIGS] ?: return@map emptyList()
        runCatching { json.decodeFromString<List<CountdownConfig>>(raw) }.getOrElse { emptyList() }
    }

    suspend fun save(configs: List<CountdownConfig>) {
        context.countdownDataStore.edit { prefs ->
            prefs[KEY_CONFIGS] = json.encodeToString(configs)
        }
    }
}
