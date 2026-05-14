package com.krono.app.feature.pomodoro

import android.app.Notification
import androidx.compose.runtime.Composable
import com.krono.app.core.tool.KronoTool
import com.krono.app.core.tool.ToolViewModel

class PomodoroTool(
    private val pomodoroViewModel: PomodoroViewModel
) : KronoTool {
    override val id: String = "pomodoro"
    override val viewModel: ToolViewModel = pomodoroViewModel
    override fun getNotification(): Notification = Notification()
    override fun onAction(action: String) {}
    override fun destroy() {}

    @Composable
    fun SettingsContent() {
        PomodoroSettings()
    }
}

