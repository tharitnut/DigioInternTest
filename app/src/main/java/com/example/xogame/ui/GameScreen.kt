package com.example.xogame.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.xogame.data.repo.LocalRepository
import com.example.xogame.viewmodel.GameViewModel

@Composable
fun GameScreen(size: Int, onFinished: () -> Unit) {
    val repo = LocalRepository.current
    val vm: GameViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(repo, size) as T
        }
    })
    val state by vm.uiState.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Player turn: ${state.currentPlayer}", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))

        val cellSize = when {
            state.boardSize <= 3 -> 96.dp
            state.boardSize <= 5 -> 72.dp
            state.boardSize <= 8 -> 56.dp
            else -> 44.dp
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            for (r in 0 until state.boardSize) {
                Row {
                    for (c in 0 until state.boardSize) {
                        val cell = state.board[r][c]
                        OutlinedCard(
                            modifier = Modifier
                                .size(cellSize)
                                .padding(2.dp)
                                .clickable { vm.onCellClick(r, c) },
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    cell.value,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        if (state.isFinished) {
            val msg = if (state.winner == "Draw") "It's a draw!" else "Winner: ${state.winner}"
            Text(msg, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Button(onClick = onFinished) { Text("Back to Home") }
        }
    }
}
