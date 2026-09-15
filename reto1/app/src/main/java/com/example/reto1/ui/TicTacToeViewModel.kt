package com.example.reto1.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reto1.logic.TicTacToeGame
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
                currentPlayer = TicTacToeGame.COMPUTER_SYMBOL
            ).also { updatedState ->
                if (newStatus != TicTacToeGame.EMPTY_SYMBOL) {
                    updateScores(newStatus)
                } else if (updatedState.currentPlayer == TicTacToeGame.COMPUTER_SYMBOL) {
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
                    val computerMove = TicTacToeGame.getComputerMove(state.board)
                    if (computerMove != -1 && state.board[computerMove] == TicTacToeGame.EMPTY_SYMBOL) {
                        val newBoard = state.board.toMutableList()
                        newBoard[computerMove] = TicTacToeGame.COMPUTER_SYMBOL
                        val newStatus = TicTacToeGame.checkGameStatus(newBoard)
                        
                        state.copy(
                            board = newBoard,
                            gameStatus = newStatus,
                            currentPlayer = TicTacToeGame.HUMAN_SYMBOL,
                            isComputerThinking = false
                        ).also {
                            if (newStatus != TicTacToeGame.EMPTY_SYMBOL) {
                                updateScores(newStatus)
                            }
                        }
                    } else {
                        state.copy(isComputerThinking = false)
                    }
                } else {
                    state.copy(isComputerThinking = false)
                }
            }
        }
    }

    private fun updateScores(status: Char) {
        _uiState.update { state ->
            when (status) {
                TicTacToeGame.HUMAN_SYMBOL -> state.copy(humanWins = state.humanWins + 1)
                TicTacToeGame.COMPUTER_SYMBOL -> state.copy(computerWins = state.computerWins + 1)
                'T' -> state.copy(ties = state.ties + 1)
                else -> state
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
}
