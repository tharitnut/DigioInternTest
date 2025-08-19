package com.example.xogame.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xogame.data.db.GameRepository
import com.example.xogame.data.entity.Move
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

data class ReplayUiState(
    val moves: List<Move> = emptyList(),
    val index: Int = 0,
    val board: List<List<Cell>> = emptyList(),
    val boardSize: Int = 3
)

class ReplayViewModel(
    private val repo: GameRepository,
    private val sessionId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReplayUiState())
    val uiState: StateFlow<ReplayUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repo.session(sessionId).filterNotNull(),
                repo.moves(sessionId)
            ) { session, moves -> session.boardSize to moves }
                .collect { (size, moves) ->
                    val emptyBoard = List(size) { row -> List(size) { col -> Cell(row, col, "") } }
                    _uiState.value = ReplayUiState(
                        moves = moves,
                        index = 0,
                        board = emptyBoard,
                        boardSize = size
                    )
                }
        }
    }

    fun stepTo(target: Int) {
        val state = _uiState.value
        val safeIndex = target.coerceIn(0, state.moves.size)
        val board = List(state.boardSize) { row ->
            MutableList(state.boardSize) { col ->
                Cell(
                    row,
                    col,
                    ""
                )
            }
        }
        for (i in 0 until safeIndex) {
            val move = state.moves[i]
            board[move.row][move.col] = board[move.row][move.col].copy(value = move.player)
        }
        _uiState.value = state.copy(index = safeIndex, board = board.map { it.toList() })
    }
}