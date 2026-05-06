package com.krono.app.core.tool

import android.app.Notification

/**
 * Contrato que toda ferramenta (Stopwatch, Countdown) deve implementar
 * para ser registrada no orquestrador central.
 */
interface KronoTool {
    val id: String
    val viewModel: ToolViewModel
    
    /**
     * Retorna a notificação atualizada para esta ferramenta.
     */
    fun getNotification(): Notification
    
    /**
     * Chamado quando o serviço recebe uma ação destinada a esta ferramenta.
     */
    fun onAction(action: String)
    
    /**
     * Remove o overlay e limpa recursos.
     */
    fun destroy()
}
