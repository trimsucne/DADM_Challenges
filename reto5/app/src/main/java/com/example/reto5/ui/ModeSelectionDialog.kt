package com.example.reto5.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

enum class GameMode {
    LOCAL_VS_AI,
    ONLINE_MULTIPLAYER
}

@Composable
fun ModeSelectionDialog(
    currentMode: GameMode,
    onModeSelected: (GameMode) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Game Mode", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { onModeSelected(GameMode.LOCAL_VS_AI) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = if (currentMode == GameMode.LOCAL_VS_AI) {
                        ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    } else {
                        ButtonDefaults.outlinedButtonColors()
                    }
                ) {
                    Text("Local Single-Player vs AI")
                }

                OutlinedButton(
                    onClick = { onModeSelected(GameMode.ONLINE_MULTIPLAYER) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = if (currentMode == GameMode.ONLINE_MULTIPLAYER) {
                        ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    } else {
                        ButtonDefaults.outlinedButtonColors()
                    }
                ) {
                    Text("Online Multiplayer (Triqui Online)")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
