package com.example.reto4.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reto4.logic.TicTacToeGame
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TicTacToeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TicTacToeState())
    val uiState: StateFlow<TicTacToeState> = _uiState.asStateFlow()

    var soundManager: SoundManager? = null
    private var preferencesManager: PreferencesManager? = null

    fun initPreferences(prefsManager: PreferencesManager) {
        if (preferencesManager != null) return
        preferencesManager = prefsManager

        val scores = prefsManager.loadScores()
        val difficulty = prefsManager.loadDifficulty()
        val soundEnabled = prefsManager.loadSoundEnabled()

        _uiState.update { state ->
            state.copy(
                humanWins = scores.humanWins,
                computerWins = scores.computerWins,
                ties = scores.ties,
                difficulty = difficulty,
                isSoundEnabled = soundEnabled
            )
        }
    }

    fun onCellClicked(index: Int) {
        val currentState = _uiState.value

        if (currentState.board[index] == TicTacToeGame.EMPTY_SYMBOL &&
            currentState.currentPlayer == TicTacToeGame.HUMAN_SYMBOL &&
            currentState.gameStatus == TicTacToeGame.EMPTY_SYMBOL &&
            !currentState.isComputerThinking
        ) {
            makeHumanMove(index)
        }
    }

    private fun makeHumanMove(index: Int) {
        _uiState.update { state ->
            if (state.board[index] != TicTacToeGame.EMPTY_SYMBOL || state.gameStatus != TicTacToeGame.EMPTY_SYMBOL) {
                return@update state
            }

            val newBoard = state.board.toMutableList()
            newBoard[index] = TicTacToeGame.HUMAN_SYMBOL
            val newStatus = TicTacToeGame.checkGameStatus(newBoard)

            soundManager?.playHumanMoveSound()

            val newHumanWins = if (newStatus == TicTacToeGame.HUMAN_SYMBOL) state.humanWins + 1 else state.humanWins
            val newComputerWins = if (newStatus == TicTacToeGame.COMPUTER_SYMBOL) state.computerWins + 1 else state.computerWins
            val newTies = if (newStatus == 'T') state.ties + 1 else state.ties

            if (newStatus != TicTacToeGame.EMPTY_SYMBOL) {
                preferencesManager?.saveScores(newHumanWins, newComputerWins, newTies)
            }

            state.copy(
                board = newBoard,
                gameStatus = newStatus,
                currentPlayer = TicTacToeGame.COMPUTER_SYMBOL,
                humanWins = newHumanWins,
                computerWins = newComputerWins,
                ties = newTies
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
            
            delay(1000)

            _uiState.update { state ->
                if (state.currentPlayer == TicTacToeGame.COMPUTER_SYMBOL && state.gameStatus == TicTacToeGame.EMPTY_SYMBOL) {
                    val computerMove = TicTacToeGame.getComputerMove(state.board, state.difficulty)
                    if (computerMove != -1 && state.board[computerMove] == TicTacToeGame.EMPTY_SYMBOL) {
                        val newBoard = state.board.toMutableList()
                        newBoard[computerMove] = TicTacToeGame.COMPUTER_SYMBOL
                        val newStatus = TicTacToeGame.checkGameStatus(newBoard)

                        soundManager?.playComputerMoveSound()

                        val newHumanWins = if (newStatus == TicTacToeGame.HUMAN_SYMBOL) state.humanWins + 1 else state.humanWins
                        val newComputerWins = if (newStatus == TicTacToeGame.COMPUTER_SYMBOL) state.computerWins + 1 else state.computerWins
                        val newTies = if (newStatus == 'T') state.ties + 1 else state.ties

                        if (newStatus != TicTacToeGame.EMPTY_SYMBOL) {
                            preferencesManager?.saveScores(newHumanWins, newComputerWins, newTies)
                        }

                        state.copy(
                            board = newBoard,
                            gameStatus = newStatus,
                            currentPlayer = TicTacToeGame.HUMAN_SYMBOL,
                            isComputerThinking = false,
                            humanWins = newHumanWins,
                            computerWins = newComputerWins,
                            ties = newTies
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

    fun resetScores() {
        preferencesManager?.resetScores()
        _uiState.update { state ->
            state.copy(
                humanWins = 0,
                computerWins = 0,
                ties = 0
            )
        }
    }

    fun toggleSoundEnabled() {
        _uiState.update { state ->
            val newEnabled = !state.isSoundEnabled
            soundManager?.isSoundEnabled = newEnabled
            preferencesManager?.saveSoundEnabled(newEnabled)
            state.copy(isSoundEnabled = newEnabled)
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
        preferencesManager?.saveDifficulty(difficulty)
        _uiState.update { it.copy(difficulty = difficulty, showDifficultyDialog = false) }
    }

    override fun onCleared() {
        super.onCleared()
        soundManager?.release()
    }
}
