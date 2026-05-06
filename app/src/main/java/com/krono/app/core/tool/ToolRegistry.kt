package com.krono.app.core.tool

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Registro central de ferramentas ativas.
 */
object ToolRegistry {
    private val _tools = MutableStateFlow<Map<String, KronoTool>>(emptyMap())
    val tools: StateFlow<Map<String, KronoTool>> = _tools.asStateFlow()

    fun register(tool: KronoTool) {
        _tools.value = _tools.value + (tool.id to tool)
    }

    fun unregister(id: String) {
        _tools.value = _tools.value - id
    }

    fun getTool(id: String): KronoTool? = _tools.value[id]
    
    fun getAllTools(): List<KronoTool> = _tools.value.values.toList()
}
