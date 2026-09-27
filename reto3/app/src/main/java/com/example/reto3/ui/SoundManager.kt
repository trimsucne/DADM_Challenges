package com.example.reto3.ui

import android.content.Context
import android.media.MediaPlayer
import com.example.reto3.R

class SoundManager(private val context: Context) {
    private var humanMediaPlayer: MediaPlayer? = null
    private var computerMediaPlayer: MediaPlayer? = null
    var isSoundEnabled: Boolean = true

    fun init() {
        release()
        try {
            humanMediaPlayer = MediaPlayer.create(context.applicationContext, R.raw.sword)
            computerMediaPlayer = MediaPlayer.create(context.applicationContext, R.raw.swish)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playHumanMoveSound() {
        if (!isSoundEnabled) return
        try {
            humanMediaPlayer?.let { player ->
                if (player.isPlaying) {
                    player.seekTo(0)
                } else {
                    player.start()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playComputerMoveSound() {
        if (!isSoundEnabled) return
        try {
            computerMediaPlayer?.let { player ->
                if (player.isPlaying) {
                    player.seekTo(0)
                } else {
                    player.start()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun release() {
        try {
            humanMediaPlayer?.release()
            humanMediaPlayer = null
            computerMediaPlayer?.release()
            computerMediaPlayer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
