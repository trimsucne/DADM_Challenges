package com.example.reto1.logic

import kotlin.random.Random

object TicTacToeGame {
    const val BOARD_SIZE = 9
    const val EMPTY_SYMBOL = ' '
    const val HUMAN_SYMBOL = 'X'
    const val COMPUTER_SYMBOL = 'O'

    val WIN_COMBINATIONS = listOf(
        listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // Rows
        listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // Columns
        listOf(0, 4, 8), listOf(2, 4, 6)                  // Diagonals
    )

    /**
     * Check if there's a winner or a tie.
     * Returns:
     * 'X' if human won,
     * 'O' if computer won,
     * 'T' if it's a tie,
     * ' ' if the game is still active.
     */
    fun checkGameStatus(board: List<Char>): Char {
        for (combination in WIN_COMBINATIONS) {
            if (board[combination[0]] != EMPTY_SYMBOL &&
                board[combination[0]] == board[combination[1]] &&
                board[combination[1]] == board[combination[2]]
            ) {
                return board[combination[0]]
            }
        }
        if (board.none { it == EMPTY_SYMBOL }) {
            return 'T' // Tie
        }
        return EMPTY_SYMBOL // Active
    }

    /**
     * Computes the computer's next move based on the tutorial strategy.
     * Returns the 0-indexed position (0-8) or -1 if no move is available.
     */
    fun getComputerMove(board: List<Char>): Int {
        val emptyIndices = board.indices.filter { board[it] == EMPTY_SYMBOL }
        if (emptyIndices.isEmpty()) return -1

        // 1. Can computer win in this move?
        for (index in emptyIndices) {
            val nextBoard = board.toMutableList()
            nextBoard[index] = COMPUTER_SYMBOL
            if (checkGameStatus(nextBoard) == COMPUTER_SYMBOL) {
                return index
            }
        }

        // 2. Can human win in their next move? Block them.
        for (index in emptyIndices) {
            val nextBoard = board.toMutableList()
            nextBoard[index] = HUMAN_SYMBOL
            if (checkGameStatus(nextBoard) == HUMAN_SYMBOL) {
                return index
            }
        }

        // 3. Otherwise, pick strategically: center, corners, or random
        if (board[4] == EMPTY_SYMBOL) return 4
        
        val corners = listOf(0, 2, 6, 8).filter { board[it] == EMPTY_SYMBOL }
        if (corners.isNotEmpty()) {
            return corners[Random.nextInt(corners.size)]
        }

        return emptyIndices[Random.nextInt(emptyIndices.size)]
    }
}
