package com.example.xogame.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xogame.data.db.GameRepository
import com.example.xogame.data.entity.GameSession
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val session: List<GameSession> = emptyList(),
    val boardSizeInput: String = "3"

)

class HomeViewModel(private val repo: GameRepository) : ViewModel() {
    val uiState: StateFlow<HomeUiState> =
        repo.allSession().map { list ->
            HomeUiState(session = list, boardSizeInput = "3")
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())
}