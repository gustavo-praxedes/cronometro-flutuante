package com.krono.app.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krono.app.data.CountdownConfig
import com.krono.app.data.CountdownDataStore
import com.krono.app.data.CountdownState
import com.krono.app.service.MainService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CountdownViewModel(
    private val dataStore: CountdownDataStore
) : ViewModel() {

    companion object {
        const val MAX_COUNTDOWNS = 5

        // Actions broadcast to MainService
        const val ACTION_COUNTDOWN_PLAY    = "com.krono.app.COUNTDOWN_PLAY"
        const val ACTION_COUNTDOWN_PAUSE   = "com.krono.app.COUNTDOWN_PAUSE"
        const val ACTION_COUNTDOWN_RESET   = "com.krono.app.COUNTDOWN_RESET"
        const val ACTION_COUNTDOWN_OVERLAY_SHOW = "com.krono.app.COUNTDOWN_OVERLAY_SHOW"
        const val ACTION_COUNTDOWN_OVERLAY_HIDE = "com.krono.app.COUNTDOWN_OVERLAY_HIDE"
        const val ACTION_COUNTDOWN_DESTROY = "com.krono.app.COUNTDOWN_DESTROY"
        const val EXTRA_COUNTDOWN_ID = "countdown_id"
    }

    private val _countdowns = MutableStateFlow<List<CountdownState>>(emptyList())
    val countdowns: StateFlow<List<CountdownState>> = _countdowns.asStateFlow()

    init {
        viewModelScope.launch {
            dataStore.configs.collect { configs ->
                val current = _countdowns.value.associateBy { it.config.id }
                _countdowns.value = configs.map { config ->
                    // Preserve runtime state if already loaded; otherwise fresh state
                    current[config.id]?.copy(config = config)
                        ?: CountdownState(config = config)
                }
            }
        }
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────

    fun addCountdown(config: CountdownConfig) {
        if (_countdowns.value.size >= MAX_COUNTDOWNS) return
        viewModelScope.launch {
            val updated = _countdowns.value.map { it.config } + config
            dataStore.save(updated)
            // StateFlow updated reactively via DataStore collector above
        }
    }

    fun updateConfig(config: CountdownConfig) {
        viewModelScope.launch {
            val updated = _countdowns.value.map {
                if (it.config.id == config.id) it.config else it.config
            }.map { if (it.id == config.id) config else it }
            dataStore.save(updated)
        }
    }

    fun deleteCountdown(context: Context, id: String) {
        // Stop service-side timer and overlay if active
        sendAction(context, ACTION_COUNTDOWN_DESTROY, id)
        viewModelScope.launch {
            val updated = _countdowns.value
                .filter { it.config.id != id }
                .map { it.config }
            dataStore.save(updated)
        }
    }

    // ── RUNTIME STATE (local + forward to service) ────────────────────────────

    fun play(context: Context, id: String) {
        updateRuntime(id) { it.copy(isRunning = true, isCompleted = false) }
        sendAction(context, ACTION_COUNTDOWN_PLAY, id)
    }

    fun pause(context: Context, id: String) {
        updateRuntime(id) { it.copy(isRunning = false) }
        sendAction(context, ACTION_COUNTDOWN_PAUSE, id)
    }

    fun reset(context: Context, id: String) {
        updateRuntime(id) { it.copy(isRunning = false, isCompleted = false, remainingSeconds = it.config.totalSeconds) }
        sendAction(context, ACTION_COUNTDOWN_RESET, id)
    }

    fun toggleOverlay(context: Context, id: String) {
        val state = _countdowns.value.find { it.config.id == id } ?: return
        if (state.isOverlayVisible) {
            updateRuntime(id) { it.copy(isOverlayVisible = false) }
            sendAction(context, ACTION_COUNTDOWN_OVERLAY_HIDE, id)
        } else {
            updateRuntime(id) { it.copy(isOverlayVisible = true) }
            sendAction(context, ACTION_COUNTDOWN_OVERLAY_SHOW, id)
        }
    }

    /** Called by MainService when countdown reaches zero */
    fun onCompleted(id: String) {
        updateRuntime(id) { it.copy(isRunning = false, isCompleted = true, remainingSeconds = 0L) }
    }

    /** Called by MainService each tick to sync remaining time */
    fun onTick(id: String, remainingSeconds: Long) {
        updateRuntime(id) { it.copy(remainingSeconds = remainingSeconds) }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun updateRuntime(id: String, transform: (CountdownState) -> CountdownState) {
        _countdowns.update { list -> list.map { if (it.config.id == id) transform(it) else it } }
    }

    private fun sendAction(context: Context, action: String, id: String) {
        val intent = Intent(context, MainService::class.java).apply {
            this.action = action
            putExtra(EXTRA_COUNTDOWN_ID, id)
        }
        context.startService(intent)
    }
}
