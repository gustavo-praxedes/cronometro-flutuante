package com.krono.app.feature.countdown

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krono.app.feature.countdown.CountdownConfig
import com.krono.app.feature.countdown.CountdownDataStore
import com.krono.app.feature.countdown.CountdownState
import com.krono.app.core.data.OverlayDataStore
import com.krono.app.core.service.MainService
import com.krono.app.core.tool.ToolViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import com.krono.app.core.tool.ToolState
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CountdownViewModel(
    private val dataStore: CountdownDataStore,
    private val overlayDataStore: OverlayDataStore
) : ViewModel(), ToolViewModel {

    companion object {
        const val MAX_COUNTDOWNS = 20
        const val SCREEN_OVERLAY_ID = "countdown-screen-overlay"

        const val ACTION_COUNTDOWN_PLAY         = "com.krono.app.COUNTDOWN_PLAY"
        const val ACTION_COUNTDOWN_PAUSE        = "com.krono.app.COUNTDOWN_PAUSE"
        const val ACTION_COUNTDOWN_RESET        = "com.krono.app.COUNTDOWN_RESET"
        const val ACTION_COUNTDOWN_OVERLAY_SHOW = "com.krono.app.COUNTDOWN_OVERLAY_SHOW"
        const val ACTION_COUNTDOWN_OVERLAY_HIDE = "com.krono.app.COUNTDOWN_OVERLAY_HIDE"
        const val ACTION_COUNTDOWN_DESTROY      = "com.krono.app.COUNTDOWN_DESTROY"
        /** Empurra estado atual do ViewModel para o overlay ativo (preview ao vivo) */
        const val ACTION_COUNTDOWN_SYNC         = "com.krono.app.COUNTDOWN_SYNC"
        const val ACTION_COUNTDOWN_PLUS_ONE     = "com.krono.app.COUNTDOWN_PLUS_ONE"
        const val ACTION_COUNTDOWN_SET_SECONDS  = "com.krono.app.COUNTDOWN_SET_SECONDS"
        const val EXTRA_COUNTDOWN_ID            = "countdown_id"
        const val EXTRA_COUNTDOWN_SECONDS       = "countdown_seconds"
        const val EXTRA_COUNTDOWN_ACCUMULATED   = "countdown_accumulated"
    }

    override val toolState: StateFlow<ToolState>
        get() = _countdowns
            .map { list -> list.firstOrNull() ?: CountdownState(CountdownConfig()) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, CountdownState(CountdownConfig()))

    override fun start() {
        _countdowns.value.firstOrNull()?.let { state ->
            updateRuntime(state.config.id) { it.copy(isRunning = true, isCompleted = false) }
        }
    }

    override fun pause() {
        _countdowns.value.firstOrNull()?.let { state ->
            updateRuntime(state.config.id) { it.copy(isRunning = false) }
        }
    }

    override fun reset() {
        _countdowns.value.firstOrNull()?.let { state ->
            accumulateElapsed(state)
            updateRuntime(state.config.id) {
                it.copy(isRunning = false, isCompleted = false, remainingSeconds = it.config.totalSeconds)
            }
        }
    }

    private val _countdowns = MutableStateFlow<List<CountdownState>>(emptyList())
    val countdowns: StateFlow<List<CountdownState>> = _countdowns.asStateFlow()
    private val transientIds = mutableSetOf<String>()

    init {
        viewModelScope.launch {
            dataStore.configs.collect { configs ->
                val current = _countdowns.value.associateBy { it.config.id }
                val persistedStates = configs.map { config ->
                    current[config.id]?.copy(config = config)
                        ?: CountdownState(config = config)
                }
                val transientStates = _countdowns.value.filter { it.config.id in transientIds }
                _countdowns.value = persistedStates + transientStates
            }
        }
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────

    fun addCountdown(config: CountdownConfig) {
        if (persistentConfigs().size >= MAX_COUNTDOWNS) return
        viewModelScope.launch {
            dataStore.save(persistentConfigs() + config)
        }
    }

    fun updateConfig(config: CountdownConfig) {
        viewModelScope.launch {
            dataStore.save(persistentConfigs().map {
                if (it.id == config.id) config else it
            })
        }
    }

    fun deleteCountdown(context: Context, id: String) {
        sendAction(context, ACTION_COUNTDOWN_DESTROY, id)
        if (id in transientIds) {
            transientIds.remove(id)
            _countdowns.update { list -> list.filter { it.config.id != id } }
            return
        }
        viewModelScope.launch {
            dataStore.save(persistentConfigs().filter { it.id != id })
        }
    }

    fun upsertTransientCountdown(
        config: CountdownConfig,
        remainingSeconds: Long,
        isRunning: Boolean
    ) {
        transientIds.add(config.id)
        val safeRemaining = remainingSeconds.coerceAtLeast(0L)
        _countdowns.update { list ->
            val index = list.indexOfFirst { it.config.id == config.id }
            if (index >= 0) {
                list.map { state ->
                    if (state.config.id == config.id) {
                        state.copy(
                            config = config,
                            remainingSeconds = safeRemaining,
                            isRunning = isRunning,
                            isCompleted = safeRemaining <= 0L && !isRunning
                        )
                    } else state
                }
            } else {
                list + CountdownState(
                    config = config,
                    remainingSeconds = safeRemaining,
                    isRunning = isRunning,
                    isCompleted = safeRemaining <= 0L && !isRunning
                )
            }
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
        _countdowns.value.find { it.config.id == id }?.let { accumulateElapsed(it) }
        updateRuntime(id) {
            it.copy(isRunning = false, isCompleted = false, remainingSeconds = it.config.totalSeconds)
        }
        context.startService(
            Intent(context, MainService::class.java).apply {
                action = ACTION_COUNTDOWN_RESET
                putExtra(EXTRA_COUNTDOWN_ID, id)
                putExtra(EXTRA_COUNTDOWN_ACCUMULATED, true)
            }
        )
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

    fun setRemainingSeconds(id: String, seconds: Long, clearCompleted: Boolean = false) {
        updateRuntime(id) { state ->
            state.copy(
                remainingSeconds = seconds.coerceAtLeast(0L),
                isCompleted = if (clearCompleted) false else state.isCompleted
            )
        }
    }

    fun setRemainingAndSync(context: Context, id: String, seconds: Long, clearCompleted: Boolean = false) {
        setRemainingSeconds(id, seconds, clearCompleted)
        context.startService(
            Intent(context, MainService::class.java).apply {
                action = ACTION_COUNTDOWN_SET_SECONDS
                putExtra(EXTRA_COUNTDOWN_ID, id)
                putExtra(EXTRA_COUNTDOWN_SECONDS, seconds.coerceAtLeast(0L))
            }
        )
    }

    fun addOneMinute(context: Context, id: String) {
        val current = _countdowns.value.find { it.config.id == id }?.remainingSeconds ?: 0L
        val next = (current + 60L).coerceAtMost(99L * 3600L + 59L * 60L + 59L)
        setRemainingAndSync(context, id, next, clearCompleted = true)
        sendAction(context, ACTION_COUNTDOWN_PLUS_ONE, id)
    }

    fun accumulateElapsedByTotalAndRemaining(totalSeconds: Long, remainingSeconds: Long) {
        val elapsedMs = ((totalSeconds - remainingSeconds).coerceAtLeast(0L)) * 1000L
        if (elapsedMs <= 0L) return
        viewModelScope.launch {
            overlayDataStore.accumulateTime(elapsedMs)
        }
    }

    fun accumulateElapsedForId(id: String) {
        _countdowns.value.find { it.config.id == id }?.let { accumulateElapsed(it) }
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

    private fun persistentConfigs(): List<CountdownConfig> =
        _countdowns.value
            .filterNot { it.config.id in transientIds }
            .map { it.config }

    private fun accumulateElapsed(state: CountdownState) {
        val elapsedMs = ((state.config.totalSeconds - state.remainingSeconds).coerceAtLeast(0L)) * 1000L
        if (elapsedMs <= 0L) return
        viewModelScope.launch {
            overlayDataStore.accumulateTime(elapsedMs)
        }
    }
}

