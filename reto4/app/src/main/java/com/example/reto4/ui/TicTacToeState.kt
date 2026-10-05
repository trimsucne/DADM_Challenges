package com.example.reto4.ui

import com.example.reto4.logic.TicTacToeGame

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
    val isSoundEnabled: Boolean = true
)
