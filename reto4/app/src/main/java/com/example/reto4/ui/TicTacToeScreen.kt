package com.example.reto4.ui

import android.app.Activity
import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reto4.R
import com.example.reto4.logic.TicTacToeGame

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicTacToeScreen(
    viewModel: TicTacToeViewModel
) {
    val state by viewModel.uiState.collectAsState()
    var menuExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Initialize Preferences & SoundManager
    DisposableEffect(context) {
        viewModel.initPreferences(PreferencesManager(context))
        val manager = SoundManager(context).apply {
            isSoundEnabled = state.isSoundEnabled
            init()
        }
        viewModel.soundManager = manager
        onDispose {
            manager.release()
            viewModel.soundManager = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tic-Tac-Toe", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options Menu"
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("New Game") },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_menu_new_game),
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                viewModel.resetGame()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("AI Difficulty") },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_menu_difficulty),
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                viewModel.setShowDifficultyDialog(true)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (state.isSoundEnabled) "Sound: On" else "Sound: Off") },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = if (state.isSoundEnabled) R.drawable.ic_menu_sound else R.drawable.ic_menu_sound_off),
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                viewModel.toggleSoundEnabled()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Reset Scores") },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_menu_reset),
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                viewModel.resetScores()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("About") },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_menu_about),
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                viewModel.setShowAboutDialog(true)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Quit") },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_menu_quit),
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                viewModel.setShowQuitDialog(true)
                            }
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (isLandscape) {
            // Adaptive Landscape Layout
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    BoardView(
                        board = state.board,
                        onCellTouched = { viewModel.onCellClicked(it) }
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ScoreBoard(
                        humanWins = state.humanWins,
                        computerWins = state.computerWins,
                        ties = state.ties
                    )

                    StatusMessage(state = state)

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = { viewModel.resetGame() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Restart Game", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        } else {
            // Portrait Layout
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ScoreBoard(
                    humanWins = state.humanWins,
                    computerWins = state.computerWins,
                    ties = state.ties
                )

                StatusMessage(state = state)

                BoardView(
                    board = state.board,
                    onCellTouched = { viewModel.onCellClicked(it) }
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { viewModel.resetGame() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Restart Game", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }

    // Dialogs
    if (state.showAboutDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.setShowAboutDialog(false) },
            title = { Text("About Tic-Tac-Toe") },
            text = {
                Column {
                    Text("Developer: Trim Suc", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Reto 4: Adaptive Portrait/Landscape layout, persistent scores & difficulty with SharedPreferences, custom Canvas BoardView, and sound effects.")
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.setShowAboutDialog(false) }) {
                    Text("OK")
                }
            }
        )
    }

    if (state.showDifficultyDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.setShowDifficultyDialog(false) },
            title = { Text("Select AI Difficulty") },
            text = {
                Column(Modifier.selectableGroup()) {
                    TicTacToeGame.Difficulty.values().forEach { diff ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .selectable(
                                    selected = (diff == state.difficulty),
                                    onClick = { viewModel.setDifficulty(diff) },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (diff == state.difficulty),
                                onClick = null
                            )
                            Text(
                                text = diff.name,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.setShowDifficultyDialog(false) }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (state.showQuitDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.setShowQuitDialog(false) },
            title = { Text("Quit Game") },
            text = { Text("Are you sure you want to exit the game?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.setShowQuitDialog(false)
                    (context as? Activity)?.finish()
                }) {
                    Text("Quit")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.setShowQuitDialog(false) }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ScoreBoard(humanWins: Int, computerWins: Int, ties: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ScoreItem(label = "Player (X)", score = humanWins, color = MaterialTheme.colorScheme.primary)
            ScoreItem(label = "Ties", score = ties, color = MaterialTheme.colorScheme.onSurfaceVariant)
            ScoreItem(label = "AI (O)", score = computerWins, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun RowScope.ScoreItem(label: String, score: Int, color: Color) {
    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = score.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun StatusMessage(state: TicTacToeState) {
    val message = when {
        state.gameStatus == TicTacToeGame.HUMAN_SYMBOL -> "You Won!"
        state.gameStatus == TicTacToeGame.COMPUTER_SYMBOL -> "Computer Won!"
        state.gameStatus == 'T' -> "It's a Tie!"
        state.isComputerThinking -> "Computer is thinking..."
        else -> "Your Turn (X)"
    }

    val color = when {
        state.gameStatus == TicTacToeGame.HUMAN_SYMBOL -> MaterialTheme.colorScheme.primary
        state.gameStatus == TicTacToeGame.COMPUTER_SYMBOL -> MaterialTheme.colorScheme.error
        state.isComputerThinking -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.onSurface
    }

    Text(
        text = message,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = Modifier.padding(vertical = 4.dp),
        textAlign = TextAlign.Center
    )
}

@Preview(showBackground = true)
@Composable
fun TicTacToeScreenPreview() {
    val dummyViewModel = TicTacToeViewModel()
    TicTacToeScreen(viewModel = dummyViewModel)
}
