package com.example.reto2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.reto2.ui.TicTacToeScreen
import com.example.reto2.ui.TicTacToeViewModel
import com.example.reto2.ui.theme.Reto2Theme

class MainActivity : ComponentActivity() {
    private val gameViewModel = TicTacToeViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Reto2Theme {
                TicTacToeScreen(viewModel = gameViewModel)
            }
        }
    }
}
