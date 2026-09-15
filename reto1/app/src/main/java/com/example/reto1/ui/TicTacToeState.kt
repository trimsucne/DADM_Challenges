package com.example.reto1.ui

import com.example.reto1.logic.TicTacToeGame

data class TicTacToeState(
    val board: List<Char> = List(TicTacToeGame.BOARD_SIZE) { TicTacToeGame.EMPTY_SYMBOL },
    val currentPlayer: Char = TicTacToeGame.HUMAN_SYMBOL,
    val gameStatus: Char = TicTacToeGame.EMPTY_SYMBOL,
    val humanWins: Int = 0,
    val computerWins: Int = 0,
    val ties: Int = 0,
    val isComputerThinking: Boolean = false
)
