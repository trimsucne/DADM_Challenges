package com.example.reto1.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reto1.logic.TicTacToeGame

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicTacToeScreen(
    viewModel: TicTacToeViewModel
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tic-Tac-Toe", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Scoreboard
            ScoreBoard(
                humanWins = state.humanWins,
                computerWins = state.computerWins,
                ties = state.ties
            )

            // Status Message
            StatusMessage(state = state)

            // 3x3 Game Board
            GameBoard(
                board = state.board,
                onCellClicked = { viewModel.onCellClicked(it) }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Play Again / Reset Button
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

@Composable
fun GameBoard(
    board: List<Char>,
    onCellClicked: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.outlineVariant)
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (row in 0 until 3) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (col in 0 until 3) {
                        val index = row * 3 + col
                        GameCell(
                            symbol = board[index],
                            onClick = { onCellClicked(index) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GameCell(
    symbol: Char,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cellColor = MaterialTheme.colorScheme.surface
    val symbolColor = when (symbol) {
        TicTacToeGame.HUMAN_SYMBOL -> MaterialTheme.colorScheme.primary
        TicTacToeGame.COMPUTER_SYMBOL -> MaterialTheme.colorScheme.error
        else -> Color.Transparent
    }

    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = cellColor),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (symbol != TicTacToeGame.EMPTY_SYMBOL) {
                Text(
                    text = symbol.toString(),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Black,
                    color = symbolColor,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TicTacToeScreenPreview() {
    val dummyViewModel = TicTacToeViewModel()
    TicTacToeScreen(viewModel = dummyViewModel)
}
