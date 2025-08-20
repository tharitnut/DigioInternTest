package com.example.xogame.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.xogame.data.repo.LocalRepository
import com.example.xogame.viewmodel.GameViewModel
import kotlinx.coroutines.launch

private fun fontSizeFor(boardSize: Int) = when {
    boardSize <= 3 -> 36.sp
    boardSize <= 5 -> 28.sp
    boardSize <= 8 -> 22.sp
    else -> 18.sp
}

private fun cellSizeFor(boardSize: Int) = when {
    boardSize <= 3 -> 96.dp
    boardSize <= 5 -> 72.dp
    boardSize <= 8 -> 56.dp
    else -> 44.dp
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    size: Int,
    onFinished: () -> Unit
) {
    val repo = LocalRepository.current
    val vm: GameViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(repo, size) as T
        }
    })
    val state by vm.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    var showConfirmCancel by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Player turn: ${state.currentPlayer}") },
                navigationIcon = {
                    IconButton(onClick = { showConfirmCancel = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val cellSize = cellSizeFor(state.boardSize)
            val symbolSize = fontSizeFor(state.boardSize)

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                for (r in 0 until state.boardSize) {
                    Row {
                        for (c in 0 until state.boardSize) {
                            val cell = state.board[r][c]
                            val canClick = !state.isFinished && cell.value.isEmpty()

                            OutlinedCard(
                                modifier = Modifier
                                    .size(cellSize)
                                    .padding(2.dp)
                                    .clickable(enabled = canClick) { vm.onCellClick(r, c) },
                                border = BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.outlineVariant
                                ),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(
                                        cell.value,
                                        fontSize = symbolSize,
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

    if (showConfirmCancel) {
        AlertDialog(
            onDismissRequest = { showConfirmCancel = false },
            title = { Text("Leave game?") },
            text = { Text("Do you want to go back to Home and cancel this session?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmCancel = false
                        scope.launch {
                            vm.cancelCurrentSession()
                            onFinished()
                        }
                    }
                ) { Text("Yes, cancel") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmCancel = false }) { Text("No") }
            }
        )
    }
}
