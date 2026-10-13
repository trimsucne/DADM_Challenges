package com.example.reto5.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.reto5.online.OnlineGameModel

@Composable
fun OnlineLobbyDialog(
    playerName: String,
    onPlayerNameChange: (String) -> Unit,
    availableGames: List<OnlineGameModel>,
    onCreateGame: () -> Unit,
    onJoinGame: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Triqui Online Lobby", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = playerName,
                    onValueChange = onPlayerNameChange,
                    label = { Text("Your Player Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = onCreateGame,
                    enabled = playerName.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Create New Game Session")
                }

                HorizontalDivider()

                Text("Available Games", fontWeight = FontWeight.SemiBold)

                if (availableGames.isEmpty()) {
                    Text(
                        "No available online games right now. Click 'Create New Game Session' to host one!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(availableGames) { game ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Host: ${game.hostName}", fontWeight = FontWeight.Bold)
                                        Text("Game ID: ${game.gameId}", style = MaterialTheme.typography.bodySmall)
                                    }
                                    Button(
                                        onClick = { onJoinGame(game.gameId) },
                                        enabled = playerName.isNotBlank()
                                    ) {
                                        Text("Join")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
