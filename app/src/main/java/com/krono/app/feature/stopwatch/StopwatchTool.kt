package com.krono.app.feature.stopwatch

import android.app.Notification
import androidx.compose.runtime.Composable
import com.krono.app.core.data.OverlayDataStore
import com.krono.app.core.tool.KronoTool
import com.krono.app.core.tool.ToolViewModel

class StopwatchTool(
    private val dataStore: OverlayDataStore,
    private val stopwatchViewModel: StopwatchViewModel
) : KronoTool {

    override val id: String = "stopwatch"
    override val viewModel: ToolViewModel = stopwatchViewModel

    override fun getNotification(): Notification {
        // Implementação temporária até refatorar NotificationHelper
        return Notification() 
    }

    override fun onAction(action: String) {
        when (action) {
            "START" -> stopwatchViewModel.start()
            "PAUSE" -> stopwatchViewModel.pause()
            "RESET" -> stopwatchViewModel.reset()
        }
    }

    override fun destroy() {
        // Limpeza se necessário
    }

    @Composable
    fun OverlayContent(
        onDrag: (Float, Float) -> Unit,
        onDragEnd: () -> Unit,
        onClose: () -> Unit,
        onSettings: () -> Unit,
        onMenuVisibilityChange: (Boolean) -> Unit
    ) {
        // O OverlayManager atual já chama StopwatchOverlay diretamente.
        // Na Fase 2.4 abstrairemos isso via KronoTool.
    }
}
