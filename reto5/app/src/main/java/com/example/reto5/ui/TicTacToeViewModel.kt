package com.example.reto5.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reto5.logic.TicTacToeGame
import com.example.reto5.online.FirebaseGameService
import com.example.reto5.online.OnlineGameModel
import kotlinx.coroutines.Job
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
    private val firebaseGameService = FirebaseGameService()

    private var lobbyJob: Job? = null
    private var gameObserveJob: Job? = null

    fun initPreferences(prefsManager: PreferencesManager) {
        if (preferencesManager != null) return
        preferencesManager = prefsManager

        val scores = prefsManager.loadScores()
        val difficulty = prefsManager.loadDifficulty()
        val soundEnabled = prefsManager.loadSoundEnabled()
        val savedPlayerName = prefsManager.loadPlayerName()

        _uiState.update { state ->
            state.copy(
                humanWins = scores.humanWins,
                computerWins = scores.computerWins,
                ties = scores.ties,
                difficulty = difficulty,
                isSoundEnabled = soundEnabled,
                playerName = savedPlayerName
            )
        }
        startLobbyObservation()
    }

    private fun startLobbyObservation() {
        lobbyJob?.cancel()
        lobbyJob = viewModelScope.launch {
            firebaseGameService.getAvailableGames().collect { games ->
                _uiState.update { it.copy(availableOnlineGames = games) }
            }
        }
    }

    fun setGameMode(mode: GameMode) {
        _uiState.update { it.copy(gameMode = mode, showModeDialog = false) }
        if (mode == GameMode.ONLINE_MULTIPLAYER) {
            _uiState.update { it.copy(showLobbyDialog = true) }
        } else {
            resetGame()
        }
    }

    fun setPlayerName(name: String) {
        preferencesManager?.savePlayerName(name)
        _uiState.update { it.copy(playerName = name) }
    }

    fun createOnlineGame() {
        val name = _uiState.value.playerName
        _uiState.update { it.copy(isOnlineConnecting = true) }

        firebaseGameService.createGame(name) { gameId ->
            if (gameId != null) {
                _uiState.update {
                    it.copy(
                        playerRole = PlayerRole.HOST,
                        showLobbyDialog = false,
                        isOnlineConnecting = false
                    )
                }
                observeOnlineGameSession(gameId)
            } else {
                _uiState.update { it.copy(isOnlineConnecting = false) }
            }
        }
    }

    fun joinOnlineGame(gameId: String) {
        val name = _uiState.value.playerName
        _uiState.update { it.copy(isOnlineConnecting = true) }

        firebaseGameService.joinGame(gameId, name) { success ->
            if (success) {
                _uiState.update {
                    it.copy(
                        playerRole = PlayerRole.GUEST,
                        showLobbyDialog = false,
                        isOnlineConnecting = false
                    )
                }
                observeOnlineGameSession(gameId)
            } else {
                _uiState.update { it.copy(isOnlineConnecting = false) }
            }
        }
    }

    private fun observeOnlineGameSession(gameId: String) {
        gameObserveJob?.cancel()
        gameObserveJob = viewModelScope.launch {
            firebaseGameService.observeGame(gameId).collect { onlineGame ->
                if (onlineGame != null) {
                    val boardList = onlineGame.getBoardAsList()
                    val gameStatusChar = when (onlineGame.winner) {
                        "X" -> TicTacToeGame.HUMAN_SYMBOL
                        "O" -> TicTacToeGame.COMPUTER_SYMBOL
                        "T" -> 'T'
                        else -> TicTacToeGame.EMPTY_SYMBOL
                    }

                    _uiState.update { state ->
                        state.copy(
                            onlineGame = onlineGame,
                            board = boardList,
                            gameStatus = gameStatusChar,
                            currentPlayer = if (onlineGame.currentTurn == "X") TicTacToeGame.HUMAN_SYMBOL else TicTacToeGame.COMPUTER_SYMBOL
                        )
                    }
                }
            }
        }
    }

    fun onCellClicked(index: Int) {
        val currentState = _uiState.value

        if (currentState.gameMode == GameMode.LOCAL_VS_AI) {
            if (currentState.board[index] == TicTacToeGame.EMPTY_SYMBOL &&
                currentState.currentPlayer == TicTacToeGame.HUMAN_SYMBOL &&
                currentState.gameStatus == TicTacToeGame.EMPTY_SYMBOL &&
                !currentState.isComputerThinking
            ) {
                makeHumanMove(index)
            }
        } else {
            // Online Multiplayer
            val onlineGame = currentState.onlineGame ?: return
            val mySymbol = if (currentState.playerRole == PlayerRole.HOST) "X" else "O"

            if (currentState.board[index] == TicTacToeGame.EMPTY_SYMBOL &&
                onlineGame.status == OnlineGameModel.STATUS_PLAYING &&
                onlineGame.currentTurn == mySymbol &&
                currentState.gameStatus == TicTacToeGame.EMPTY_SYMBOL
            ) {
                makeOnlineMove(index, mySymbol)
            }
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

    private fun makeOnlineMove(index: Int, mySymbol: String) {
        val state = _uiState.value
        val gameId = state.onlineGame?.gameId ?: return

        val newBoard = state.board.toMutableList()
        newBoard[index] = if (mySymbol == "X") TicTacToeGame.HUMAN_SYMBOL else TicTacToeGame.COMPUTER_SYMBOL
        val newStatusChar = TicTacToeGame.checkGameStatus(newBoard)

        val nextTurn = if (mySymbol == "X") "O" else "X"
        val winnerStr = when (newStatusChar) {
            TicTacToeGame.HUMAN_SYMBOL -> "X"
            TicTacToeGame.COMPUTER_SYMBOL -> "O"
            'T' -> "T"
            else -> " "
        }

        if (mySymbol == "X") {
            soundManager?.playHumanMoveSound()
        } else {
            soundManager?.playComputerMoveSound()
        }

        firebaseGameService.makeMove(gameId, newBoard, nextTurn, winnerStr)
    }

    fun resetGame() {
        val state = _uiState.value
        if (state.gameMode == GameMode.LOCAL_VS_AI) {
            _uiState.update { s ->
                s.copy(
                    board = List(TicTacToeGame.BOARD_SIZE) { TicTacToeGame.EMPTY_SYMBOL },
                    currentPlayer = TicTacToeGame.HUMAN_SYMBOL,
                    gameStatus = TicTacToeGame.EMPTY_SYMBOL,
                    isComputerThinking = false
                )
            }
        } else {
            val gameId = state.onlineGame?.gameId
            if (gameId != null) {
                firebaseGameService.resetOnlineGame(gameId)
            }
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

    fun setShowModeDialog(show: Boolean) {
        _uiState.update { it.copy(showModeDialog = show) }
    }

    fun setShowLobbyDialog(show: Boolean) {
        _uiState.update { it.copy(showLobbyDialog = show) }
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
        firebaseGameService.detachListener()
    }
}
