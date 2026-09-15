package com.example.reto1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.reto1.ui.TicTacToeScreen
import com.example.reto1.ui.TicTacToeViewModel
import com.example.reto1.ui.theme.Reto1Theme

class MainActivity : ComponentActivity() {
    private val gameViewModel = TicTacToeViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Reto1Theme {
                TicTacToeScreen(viewModel = gameViewModel)
            }
        }
    }
}
