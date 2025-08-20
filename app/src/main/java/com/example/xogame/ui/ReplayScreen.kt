package com.example.xogame.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.xogame.data.repo.LocalRepository
import com.example.xogame.viewmodel.ReplayViewModel

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReplayScreen(
    sessionId: Long,
    onBack: () -> Unit
) {
    // ViewModel
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
                val boardSide: Dp = min(maxWidth, maxHeight)
                val cellSize: Dp = ((boardSide - (gap * 2 * n)) / n).coerceAtLeast(20.dp)
                val fontSize = (cellSize.value * 0.55f).sp

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.size(boardSide)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            for (r in 0 until n) {
                                Row {
                                    for (c in 0 until n) {
                                        val value =
                                            state.board.getOrNull(r)?.getOrNull(c)?.value.orEmpty()
                                        OutlinedCard(
                                            modifier = Modifier
                                                .size(cellSize)
                                                .padding(gap),
                                            // constant corner radius (same as GameScreen)
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Box(
                                                Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(value, fontSize = fontSize)
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

            // Playback controls
            Text(
                "Move ${state.index}/${state.moves.size}",
                style = MaterialTheme.typography.bodyLarge
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = { vm.stepTo(0) }) { Text("⏮ Start") }
                Spacer(Modifier.width(8.dp))
                Button(onClick = { vm.stepTo((state.index - 1).coerceAtLeast(0)) }) {
                    Text("◀ Prev")
                }
                Spacer(Modifier.width(8.dp))
                Button(onClick = { vm.stepTo((state.index + 1).coerceAtMost(state.moves.size)) }) {
                    Text("Next ▶")
                }
            }
        }
    }
}
