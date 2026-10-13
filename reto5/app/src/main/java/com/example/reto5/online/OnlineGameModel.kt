package com.example.reto5.online

import com.google.firebase.firestore.Exclude

data class OnlineGameModel(
    val gameId: String = "",
    val hostName: String = "",
    val guestName: String = "",
    val board: String = "         ", // 9 spaces
    val currentTurn: String = "X", // "X" or "O"
    val status: String = STATUS_WAITING, // "WAITING", "PLAYING", "FINISHED"
    val winner: String = " ", // "X", "O", "T", " "
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val STATUS_WAITING = "WAITING"
        const val STATUS_PLAYING = "PLAYING"
        const val STATUS_FINISHED = "FINISHED"
    }

    @Exclude
    fun getBoardAsList(): List<Char> {
        val chars = board.toCharArray().toList()
        return if (chars.size == 9) chars else List(9) { ' ' }
    }
}
