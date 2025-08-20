package com.example.xogame.data.repo

import androidx.compose.runtime.compositionLocalOf
import com.example.xogame.data.db.GameRepository

val LocalRepository = compositionLocalOf<GameRepository> {
    error("LocalRepository not provided")
}