package com.example.xogame.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.xogame.data.repo.LocalRepository
import com.example.xogame.data.entity.GameSession
import com.example.xogame.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
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
    val context = LocalContext.current

    var sizeInput by remember { mutableStateOf("3") }
    var showConfirmDelete by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("XO Game") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
        ) {
            // New Game card
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("New Game", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = sizeInput,
                        onValueChange = { value ->
                            sizeInput = value.filter { ch -> ch.isDigit() }.take(2)
                        },
                        label = { Text("Board size (3-12)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = {
                        val size = sizeInput.toIntOrNull()
                        if (size == null) {
                            Toast.makeText(context, "Please enter a number", Toast.LENGTH_SHORT)
                                .show()
                            return@Button
                        }
                        if (size < 3) {
                            Toast.makeText(context, "Minimum board size is 3", Toast.LENGTH_SHORT)
                                .show()
                            return@Button
                        }
                        if (size > 12) {
                            Toast.makeText(context, "Maximum board size is 12", Toast.LENGTH_SHORT)
                                .show()
                            return@Button
                        }
                        // valid size → start game
                        onStartGame(size)
                    }) {
                        Text("Start")
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Replays header + Delete History button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Replays", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.weight(1f))
                OutlinedButton(onClick = { showConfirmDelete = true }) {
                    Text("Clear History")
                }
            }
            Spacer(Modifier.height(8.dp))

            // Game History
            if (state.session.isEmpty()) {
                Text(
                    "No history yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(Modifier.fillMaxSize()) {
                    items(state.session) { session ->
                        SessionRow(session = session, onOpen = { onOpenReplay(session.id) })
                    }
                }
            }
        }
    }

    // Confirm dialog for clear history
    if (showConfirmDelete) {
        AlertDialog(
            onDismissRequest = { showConfirmDelete = false },
            title = { Text("Clear all history?") },
            text = { Text("This will remove all saved game sessions and moves.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.onDeleteHistory()
                    showConfirmDelete = false
                    Toast.makeText(context, "History cleared", Toast.LENGTH_SHORT).show()
                }) { Text("Clear") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDelete = false }) { Text("Cancel") }
            }
        )
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
