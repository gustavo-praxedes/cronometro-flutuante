package com.krono.app.data

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class CountdownConfig(
    val id: String = UUID.randomUUID().toString(),
    val description: String,
    val totalSeconds: Long,
    val backgroundColor: Int,
    val createdAt: Long = System.currentTimeMillis()
)
