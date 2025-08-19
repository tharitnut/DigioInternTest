package com.example.xogame.data.entity

import androidx.room.Entity

@Entity(primaryKeys = ["sessionId", "moveNumber"])
data class Move(
    val sessionId: Long,
    val moveNumber: Int,
    val row: Int,
    val col: Int,
    val player: String, // "X" or "O"
    val timestamp: Long
)
