package com.example.reto1

import com.example.reto1.logic.Difficulty
import com.example.reto1.logic.TicTacToeGame
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TicTacToeGameTest {

    @Test
    fun testCheckGameStatus_emptyBoard_returnsEmpty() {
        val board = List(9) { ' ' }
        assertEquals(' ', TicTacToeGame.checkGameStatus(board))
    }

    @Test
    fun testCheckGameStatus_rowWin_returnsWinner() {
        val board = listOf(
            'X', 'X', 'X',
            ' ', 'O', ' ',
            ' ', ' ', 'O'
        )
        assertEquals('X', TicTacToeGame.checkGameStatus(board))
    }

    @Test
    fun testCheckGameStatus_columnWin_returnsWinner() {
        val board = listOf(
            'O', 'X', ' ',
            'O', 'X', ' ',
            'O', ' ', ' '
        )
        assertEquals('O', TicTacToeGame.checkGameStatus(board))
    }

    @Test
    fun testCheckGameStatus_diagonalWin_returnsWinner() {
        val board = listOf(
            'X', 'O', ' ',
            ' ', 'X', ' ',
            'O', ' ', 'X'
        )
        assertEquals('X', TicTacToeGame.checkGameStatus(board))
    }

    @Test
    fun testCheckGameStatus_tie_returnsTie() {
        val board = listOf(
            'X', 'O', 'X',
            'X', 'O', 'O',
            'O', 'X', 'X'
        )
        assertEquals('T', TicTacToeGame.checkGameStatus(board))
    }

    @Test
    fun testGetComputerMove_easy_returnsAvailableIndex() {
        val board = listOf(
            'X', 'O', 'X',
            ' ', ' ', ' ',
            ' ', ' ', ' '
        )
        val move = TicTacToeGame.getComputerMove(board, Difficulty.EASY)
        assertTrue(move in 3..8)
    }

    @Test
    fun testGetComputerMove_medium_winsIfPossible() {
        // AI needs one more 'O' in top row to win
        val board = listOf(
            'O', 'O', ' ',
            'X', 'X', ' ',
            ' ', ' ', ' '
        )
        val move = TicTacToeGame.getComputerMove(board, Difficulty.MEDIUM)
        assertEquals(2, move)
    }

    @Test
    fun testGetComputerMove_medium_blocksHuman() {
        // Human has two 'X's in second row, AI should block
        val board = listOf(
            'O', ' ', ' ',
            'X', 'X', ' ',
            ' ', ' ', ' '
        )
        val move = TicTacToeGame.getComputerMove(board, Difficulty.MEDIUM)
        assertEquals(5, move)
    }
}
