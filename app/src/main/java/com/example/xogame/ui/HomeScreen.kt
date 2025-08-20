package com.example.xogame.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.xogame.data.repo.LocalRepository
import com.example.xogame.data.entity.GameSession
import com.example.xogame.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onStartGame: (Int) -> Unit,
    onOpenReplay: (Long) -> Unit
) {
    val repo = LocalRepository.current
    val vm: HomeViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repo) as T
        }
    })
    val state by vm.uiState.collectAsState()

    var sizeInput by remember { mutableStateOf("3") }

    Column(
        Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text("XO - 2 Players (Local)", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("New Game", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = sizeInput,
                    onValueChange = { sizeInput = it.filter { ch -> ch.isDigit() }.take(2) },
                    label = { Text("Board size (3-12)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                Button(onClick = {
                    val size = sizeInput.toIntOrNull()?.coerceIn(3, 12) ?: 3
                    onStartGame(size)
                }) { Text("Start") }
            }
        }

        Spacer(Modifier.height(24.dp))
        Text("Replays", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        LazyColumn(Modifier.fillMaxSize()) {
            items(state.session) { s ->
                SessionRow(session = s, onOpen = { onOpenReplay(s.id) })
            }
        }
    }
}

@Composable
private fun SessionRow(session: GameSession, onOpen: () -> Unit) {
    val subtitle = buildString {
        append("${session.boardSize}x${session.boardSize}")
        append(" • Win ${session.winLength}")
        if (session.winner != null) append(" • Winner: ${session.winner}")
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onOpen() }
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Session #${session.id}", style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium)
            }
            Text(
                "Open",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
