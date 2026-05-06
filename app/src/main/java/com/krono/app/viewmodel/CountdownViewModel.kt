package com.krono.app.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krono.app.data.CountdownConfig
import com.krono.app.data.CountdownDataStore
import com.krono.app.data.CountdownState
import com.krono.app.core.service.MainService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CountdownViewModel(
    private val dataStore: CountdownDataStore
) : ViewModel() {

    companion object {
        const val MAX_COUNTDOWNS = 20

        const val ACTION_COUNTDOWN_PLAY         = "com.krono.app.COUNTDOWN_PLAY"
        const val ACTION_COUNTDOWN_PAUSE        = "com.krono.app.COUNTDOWN_PAUSE"
        const val ACTION_COUNTDOWN_RESET        = "com.krono.app.COUNTDOWN_RESET"
        const val ACTION_COUNTDOWN_OVERLAY_SHOW = "com.krono.app.COUNTDOWN_OVERLAY_SHOW"
        const val ACTION_COUNTDOWN_OVERLAY_HIDE = "com.krono.app.COUNTDOWN_OVERLAY_HIDE"
        const val ACTION_COUNTDOWN_DESTROY      = "com.krono.app.COUNTDOWN_DESTROY"
        /** Empurra estado atual do ViewModel para o overlay ativo (preview ao vivo) */
        const val ACTION_COUNTDOWN_SYNC         = "com.krono.app.COUNTDOWN_SYNC"
        const val EXTRA_COUNTDOWN_ID            = "countdown_id"
    }

    private val _countdowns = MutableStateFlow<List<CountdownState>>(emptyList())
    val countdowns: StateFlow<List<CountdownState>> = _countdowns.asStateFlow()

    init {
        viewModelScope.launch {
            dataStore.configs.collect { configs ->
                val current = _countdowns.value.associateBy { it.config.id }
                _countdowns.value = configs.map { config ->
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
            dataStore.save(_countdowns.value.map { it.config } + config)
        }
    }

    fun updateConfig(config: CountdownConfig) {
        viewModelScope.launch {
            dataStore.save(_countdowns.value.map {
                if (it.config.id == config.id) config else it.config
            })
        }
    }

    fun deleteCountdown(context: Context, id: String) {
        sendAction(context, ACTION_COUNTDOWN_DESTROY, id)
        viewModelScope.launch {
            dataStore.save(_countdowns.value.filter { it.config.id != id }.map { it.config })
        }
    }

    // ── Preview ao vivo (wheel no dialog) ────────────────────────────────────

    /**
     * Atualiza [remainingSeconds] no card e overlay sem persistir.
     * Chamado a cada tick do TimeWheelPicker enquanto o dialog está aberto.
     */
    fun previewRemaining(id: String?, seconds: Long) {
        if (id == null) return
        updateRuntime(id) { it.copy(remainingSeconds = seconds) }
        // Nota: o CountdownScreen também dispara ACTION_COUNTDOWN_SYNC via startService
        // para que o CountdownManager.syncOverlay() atualize o overlay ativo.
    }

    /**
     * Reverte o preview ao estado original (chamado ao cancelar o dialog).
     */
    fun revertPreview(id: String?) {
        if (id == null) return
        updateRuntime(id) { it.copy(remainingSeconds = it.config.totalSeconds) }
    }

    // ── Timer ─────────────────────────────────────────────────────────────────

    fun play(context: Context, id: String) {
        updateRuntime(id) { it.copy(isRunning = true, isCompleted = false) }
        sendAction(context, ACTION_COUNTDOWN_PLAY, id)
    }

    fun pause(context: Context, id: String) {
        updateRuntime(id) { it.copy(isRunning = false) }
        sendAction(context, ACTION_COUNTDOWN_PAUSE, id)
    }

    fun reset(context: Context, id: String) {
        updateRuntime(id) {
            it.copy(isRunning = false, isCompleted = false, remainingSeconds = it.config.totalSeconds)
        }
        sendAction(context, ACTION_COUNTDOWN_RESET, id)
    }

    // ── Overlay ───────────────────────────────────────────────────────────────

    fun toggleOverlay(context: Context, id: String) {
        val visible = _countdowns.value.find { it.config.id == id }?.isOverlayVisible ?: return
        updateRuntime(id) { it.copy(isOverlayVisible = !visible) }
        sendAction(
            context,
            if (visible) ACTION_COUNTDOWN_OVERLAY_HIDE else ACTION_COUNTDOWN_OVERLAY_SHOW,
            id
        )
    }

    /** Chamado pelo X do overlay — sem context, manager já tratou o service side */
    fun forceHideOverlay(id: String) {
        updateRuntime(id) { it.copy(isOverlayVisible = false) }
    }

    // ── Chamados pelo CountdownManager ────────────────────────────────────────

    fun onCompleted(id: String) {
        updateRuntime(id) { it.copy(isRunning = false, isCompleted = true, remainingSeconds = 0L) }
    }

    fun onTick(id: String, remainingSeconds: Long) {
        updateRuntime(id) { it.copy(remainingSeconds = remainingSeconds) }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun updateRuntime(id: String, transform: (CountdownState) -> CountdownState) {
        _countdowns.update { list ->
            list.map { if (it.config.id == id) transform(it) else it }
        }
    }

    private fun sendAction(context: Context, action: String, id: String) {
        context.startService(
            Intent(context, MainService::class.java).apply {
                this.action = action
                putExtra(EXTRA_COUNTDOWN_ID, id)
            }
        )
    }
}
