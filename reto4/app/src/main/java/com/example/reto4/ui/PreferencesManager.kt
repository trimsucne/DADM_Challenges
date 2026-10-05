package com.example.reto4.ui

import android.content.Context
import android.content.SharedPreferences
import com.example.reto4.logic.TicTacToeGame

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "ttt_prefs"
        private const val KEY_HUMAN_WINS = "mHumanWins"
        private const val KEY_COMPUTER_WINS = "mComputerWins"
        private const val KEY_TIES = "mTies"
        private const val KEY_DIFFICULTY = "difficulty"
        private const val KEY_SOUND_ENABLED = "isSoundEnabled"
    }

    fun loadScores(): Scores {
        val humanWins = prefs.getInt(KEY_HUMAN_WINS, 0)
        val computerWins = prefs.getInt(KEY_COMPUTER_WINS, 0)
        val ties = prefs.getInt(KEY_TIES, 0)
        return Scores(humanWins, computerWins, ties)
    }

    fun saveScores(humanWins: Int, computerWins: Int, ties: Int) {
        prefs.edit()
            .putInt(KEY_HUMAN_WINS, humanWins)
            .putInt(KEY_COMPUTER_WINS, computerWins)
            .putInt(KEY_TIES, ties)
            .apply()
    }

    fun resetScores() {
        prefs.edit()
            .putInt(KEY_HUMAN_WINS, 0)
            .putInt(KEY_COMPUTER_WINS, 0)
            .putInt(KEY_TIES, 0)
            .apply()
    }

    fun loadDifficulty(): TicTacToeGame.Difficulty {
        val name = prefs.getString(KEY_DIFFICULTY, TicTacToeGame.Difficulty.Hard.name)
        return try {
            TicTacToeGame.Difficulty.valueOf(name ?: TicTacToeGame.Difficulty.Hard.name)
        } catch (e: Exception) {
            TicTacToeGame.Difficulty.Hard
        }
    }

    fun saveDifficulty(difficulty: TicTacToeGame.Difficulty) {
        prefs.edit()
            .putString(KEY_DIFFICULTY, difficulty.name)
            .apply()
    }

    fun loadSoundEnabled(): Boolean {
        return prefs.getBoolean(KEY_SOUND_ENABLED, true)
    }

    fun saveSoundEnabled(enabled: Boolean) {
        prefs.edit()
            .putBoolean(KEY_SOUND_ENABLED, enabled)
            .apply()
    }

    data class Scores(
        val humanWins: Int,
        val computerWins: Int,
        val ties: Int
    )
}
