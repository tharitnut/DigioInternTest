package com.example.xogame.data.entity

import androidx.room.PrimaryKey

data class GameSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val boardSize: Int,
    val winLength: Int,
    val startedAt: Long,
    val endedAt: Long? = null,
    val winner: String? = null,
    val durationMs: Long? = null
)
