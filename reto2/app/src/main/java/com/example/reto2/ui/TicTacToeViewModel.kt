package com.example.reto2.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reto2.logic.TicTacToeGame
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TicTacToeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TicTacToeState())
    val uiState: StateFlow<TicTacToeState> = _uiState.asStateFlow()

    fun onCellClicked(index: Int) {
        val currentState = _uiState.value

        // STRICT CHECK: Block click if cell occupied, not human turn, computer thinking, or game ended
        if (currentState.board[index] == TicTacToeGame.EMPTY_SYMBOL &&
            currentState.currentPlayer == TicTacToeGame.HUMAN_SYMBOL &&
            currentState.gameStatus == TicTacToeGame.EMPTY_SYMBOL &&
            !currentState.isComputerThinking
        ) {
            makeMove(index)
        }
    }

    private fun makeMove(index: Int) {
        _uiState.update { state ->
            // Double-check condition during mutation to avoid state overlap race conditions
            if (state.board[index] != TicTacToeGame.EMPTY_SYMBOL || state.gameStatus != TicTacToeGame.EMPTY_SYMBOL) {
                return@update state
            }

            val newBoard = state.board.toMutableList()
            newBoard[index] = TicTacToeGame.HUMAN_SYMBOL
            val newStatus = TicTacToeGame.checkGameStatus(newBoard)

            state.copy(
                board = newBoard,
                gameStatus = newStatus,
                currentPlayer = TicTacToeGame.COMPUTER_SYMBOL,
                humanWins = if (newStatus == TicTacToeGame.HUMAN_SYMBOL) state.humanWins + 1 else state.humanWins,
                computerWins = if (newStatus == TicTacToeGame.COMPUTER_SYMBOL) state.computerWins + 1 else state.computerWins,
                ties = if (newStatus == 'T') state.ties + 1 else state.ties
            ).also { updatedState ->
                if (newStatus == TicTacToeGame.EMPTY_SYMBOL && updatedState.currentPlayer == TicTacToeGame.COMPUTER_SYMBOL) {
                    triggerComputerMove()
                }
            }
        }
    }

    private fun triggerComputerMove() {
        viewModelScope.launch {
            _uiState.update { it.copy(isComputerThinking = true) }
            delay(500) // Aesthetic delay for good UX turn transition
            
            _uiState.update { state ->
                // Ensure it is still strictly computer's turn and game hasn't ended or restarted
                if (state.currentPlayer == TicTacToeGame.COMPUTER_SYMBOL && state.gameStatus == TicTacToeGame.EMPTY_SYMBOL) {
                    val computerMove = TicTacToeGame.getComputerMove(state.board, state.difficulty)
                    if (computerMove != -1 && state.board[computerMove] == TicTacToeGame.EMPTY_SYMBOL) {
                        val newBoard = state.board.toMutableList()
                        newBoard[computerMove] = TicTacToeGame.COMPUTER_SYMBOL
                        val newStatus = TicTacToeGame.checkGameStatus(newBoard)
                        
                        state.copy(
                            board = newBoard,
                            gameStatus = newStatus,
                            currentPlayer = TicTacToeGame.HUMAN_SYMBOL,
                            isComputerThinking = false,
                            humanWins = if (newStatus == TicTacToeGame.HUMAN_SYMBOL) state.humanWins + 1 else state.humanWins,
                            computerWins = if (newStatus == TicTacToeGame.COMPUTER_SYMBOL) state.computerWins + 1 else state.computerWins,
                            ties = if (newStatus == 'T') state.ties + 1 else state.ties
                        )
                    } else {
                        state.copy(isComputerThinking = false)
                    }
                } else {
                    state.copy(isComputerThinking = false)
                }
            }
        }
    }

    fun resetGame() {
        _uiState.update { state ->
            state.copy(
                board = List(TicTacToeGame.BOARD_SIZE) { TicTacToeGame.EMPTY_SYMBOL },
                currentPlayer = TicTacToeGame.HUMAN_SYMBOL,
                gameStatus = TicTacToeGame.EMPTY_SYMBOL,
                isComputerThinking = false
            )
        }
    }

    fun setShowAboutDialog(show: Boolean) {
        _uiState.update { it.copy(showAboutDialog = show) }
    }

    fun setShowDifficultyDialog(show: Boolean) {
        _uiState.update { it.copy(showDifficultyDialog = show) }
    }

    fun setShowQuitDialog(show: Boolean) {
        _uiState.update { it.copy(showQuitDialog = show) }
    }

    fun setDifficulty(difficulty: TicTacToeGame.Difficulty) {
        _uiState.update { it.copy(difficulty = difficulty, showDifficultyDialog = false) }
    }
}
