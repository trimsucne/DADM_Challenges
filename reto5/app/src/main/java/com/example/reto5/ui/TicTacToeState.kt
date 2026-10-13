package com.example.reto5.ui

import com.example.reto5.logic.TicTacToeGame
import com.example.reto5.online.OnlineGameModel

enum class PlayerRole {
    HOST,  // 'X'
    GUEST  // 'O'
}

data class TicTacToeState(
    val board: List<Char> = List(TicTacToeGame.BOARD_SIZE) { TicTacToeGame.EMPTY_SYMBOL },
    val currentPlayer: Char = TicTacToeGame.HUMAN_SYMBOL,
    val gameStatus: Char = TicTacToeGame.EMPTY_SYMBOL,
    val isComputerThinking: Boolean = false,
    val difficulty: TicTacToeGame.Difficulty = TicTacToeGame.Difficulty.Hard,
    val humanWins: Int = 0,
    val computerWins: Int = 0,
    val ties: Int = 0,
    val showAboutDialog: Boolean = false,
    val showDifficultyDialog: Boolean = false,
    val showQuitDialog: Boolean = false,
    val isSoundEnabled: Boolean = true,

    // Online Multiplayer State
    val gameMode: GameMode = GameMode.LOCAL_VS_AI,
    val playerName: String = "Player1",
    val onlineGame: OnlineGameModel? = null,
    val availableOnlineGames: List<OnlineGameModel> = emptyList(),
    val playerRole: PlayerRole = PlayerRole.HOST,
    val showModeDialog: Boolean = false,
    val showLobbyDialog: Boolean = false,
    val isOnlineConnecting: Boolean = false
)
