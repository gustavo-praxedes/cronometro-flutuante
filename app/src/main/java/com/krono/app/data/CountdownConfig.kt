package com.krono.app.data

import android.graphics.Color
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class CountdownConfig(
    val id             : String = UUID.randomUUID().toString(),
    val description    : String = "",                          // em branco por padrão
    val totalSeconds   : Long   = 0L,                          // zerado por padrão
    val backgroundColor: Int    = Color.WHITE,                 // branco por padrão
    val createdAt      : Long   = System.currentTimeMillis()
)
