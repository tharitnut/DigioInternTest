package com.example.xogame.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xogame.data.db.GameRepository
import com.example.xogame.logic.GameEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class Cell(val row: Int, val col: Int, val value: String = "")

data class GameUiState(
    val board: List<List<Cell>>,
    val currentPlayer: String = "X",
    val moveNumber: Int = 0,
    val isFinished: Boolean = false,
    val winner: String? = null,
    val sessionId: Long = -1,
    val boardSize: Int = 3,
    val winLength: Int = 3
)

class GameViewModel(
    private val repo: GameRepository,
    boardSize: Int
) : ViewModel() {

    private val winLength = if (boardSize <= 3) 3 else 4

    private val _uiState = MutableStateFlow(
        GameUiState(
            board = List(boardSize) { row -> List(boardSize) { col -> Cell(row, col, "") } },
            boardSize = boardSize,
            winLength = winLength
        )
    )
    val uiState: StateFlow<GameUiState> = _uiState

    init {
        viewModelScope.launch {
            val id = repo.createSession(boardSize, winLength)
            _uiState.value = _uiState.value.copy(sessionId = id)
        }
    }

    fun onCellClick(row: Int, col: Int) {
        val state = _uiState.value
        if (state.isFinished) return
        if (state.board[row][col].value.isNotEmpty()) return

        val player = state.currentPlayer
        val newBoard = state.board.map { it.toMutableList() }.toMutableList()
        newBoard[row][col] = newBoard[row][col].copy(value = player)
        val nextMove = state.moveNumber + 1

        viewModelScope.launch {
            if (state.sessionId != -1L) {
                repo.addMove(state.sessionId, nextMove, row, col, player)
            }
        }

        val flat = newBoard.map { row -> row.map { it.value } }
        val won = GameEngine.checkWinner(flat, state.winLength)
        val isDraw = !won.let { it != null } && flat.all { row -> row.all { it.isNotEmpty() } }

        if (won != null || isDraw) {
            val winner = won ?: "Draw"
            viewModelScope.launch {
                if (state.sessionId != -1L) {
                    repo.findSession(state.sessionId, if (won != null) won else null)
                }
            }
            _uiState.value = state.copy(
                board = newBoard,
                currentPlayer = player,
                moveNumber = nextMove,
                isFinished = true,
                winner = winner
            )
        } else {
            val nextPlayer = if (player == "X") "O" else "X"
            _uiState.value = state.copy(
                board = newBoard,
                currentPlayer = nextPlayer,
                moveNumber = nextMove,
            )
        }
    }


}