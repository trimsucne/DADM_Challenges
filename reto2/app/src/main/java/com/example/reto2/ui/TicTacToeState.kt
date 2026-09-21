package com.example.reto2.ui

import com.example.reto2.logic.TicTacToeGame

data class TicTacToeState(
    val board: List<Char> = List(TicTacToeGame.BOARD_SIZE) { TicTacToeGame.EMPTY_SYMBOL },
    val currentPlayer: Char = TicTacToeGame.HUMAN_SYMBOL,
    val gameStatus: Char = TicTacToeGame.EMPTY_SYMBOL,
    val humanWins: Int = 0,
    val computerWins: Int = 0,
    val ties: Int = 0,
    val isComputerThinking: Boolean = false,
    val showAboutDialog: Boolean = false,
    val showDifficultyDialog: Boolean = false,
    val showQuitDialog: Boolean = false,
    val difficulty: TicTacToeGame.Difficulty = TicTacToeGame.Difficulty.Hard
)
