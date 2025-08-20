package com.example.xogame.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.xogame.data.repo.LocalRepository
import com.example.xogame.viewmodel.ReplayViewModel

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
fun ReplayScreen(sessionId: Long, onBack: () -> Unit) {
    val repo = LocalRepository.current
    val vm: ReplayViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ReplayViewModel(repo, sessionId) as T
        }
    })
    val state by vm.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Replay") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
                            val cell = state.board.getOrNull(r)?.getOrNull(c)
                            OutlinedCard(
                                Modifier
                                    .size(cellSize)
                                    .padding(2.dp)
                            ) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(cell?.value ?: "", fontSize = symbolSize)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = { vm.stepTo(0) }) { Text("⏮ Start") }
                Spacer(Modifier.width(8.dp))
                Button(onClick = { vm.stepTo((state.index - 1).coerceAtLeast(0)) }) { Text("◀ Prev") }
                Spacer(Modifier.width(8.dp))
                Text(
                    "Move ${state.index}/${state.moves.size}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = { vm.stepTo((state.index + 1).coerceAtMost(state.moves.size)) }) {
                    Text(
                        "Next ▶"
                    )
                }
                Spacer(Modifier.width(8.dp))
                Button(onClick = onBack) { Text("Back") }
            }
        }
    }
}
