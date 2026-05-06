package com.krono.app.feature.countdown

import android.app.Notification
import androidx.compose.runtime.Composable
import com.krono.app.core.tool.KronoTool
import com.krono.app.core.tool.ToolViewModel

class CountdownTool(
    private val countdownViewModel: CountdownViewModel
) : KronoTool {

    override val id: String = "countdown"
    override val viewModel: ToolViewModel = countdownViewModel

    override fun getNotification(): Notification {
        return Notification()
    }

    override fun onAction(action: String) {
        // Actions handled via MainService → CountdownManager
    }

    override fun destroy() {
        // Cleanup if needed
    }

    @Composable
    fun SettingsContent() {
        CountdownSettings()
    }
}