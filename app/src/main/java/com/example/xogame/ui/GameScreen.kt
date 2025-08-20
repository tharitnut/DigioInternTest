package com.example.xogame.ui

import android.annotation.SuppressLint
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.xogame.data.repo.LocalRepository
import com.example.xogame.viewmodel.GameViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    size: Int,
    onFinished: () -> Unit
) {
    // ViewModel
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
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Centered, responsive square board ---
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 0.dp)
            ) {
                val n = state.boardSize
                val gap = 2.dp
                // choose smaller side to make a perfect square
                val boardSide: Dp = min(maxWidth, maxHeight)
                // each cell fits including outer paddings
                val cellSize: Dp = ((boardSide - (gap * 2 * n)) / n).coerceAtLeast(20.dp)
                // scale text from cell size (keeps X/O equal across sizes)
                val fontSize = (cellSize.value * 0.55f).sp

                // place the square in the center horizontally
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.size(boardSide)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            for (r in 0 until n) {
                                Row {
                                    for (c in 0 until n) {
                                        val cell = state.board[r][c]
                                        val canClick = !state.isFinished && cell.value.isEmpty()

                                        OutlinedCard(
                                            modifier = Modifier
                                                .size(cellSize)
                                                .padding(gap)
                                                .clickable(enabled = canClick) {
                                                    vm.onCellClick(r, c)
                                                },
                                            border = BorderStroke(
                                                1.dp,
                                                MaterialTheme.colorScheme.outlineVariant
                                            ),
                                            // constant corner radius so 12x12 looks like 3x3
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Box(
                                                Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    cell.value,
                                                    fontSize = fontSize,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            if (state.isFinished) {
                val msg =
                    if (state.winner == "Draw") "It's a draw!" else "Winner: ${state.winner}"
                Text(msg, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Button(onClick = onFinished) { Text("Back to Home") }
            }
        }
    }

    // Confirm cancel dialog when pressing back
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
                            vm.cancelCurrentSession()  // delete unfinished session + moves
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
