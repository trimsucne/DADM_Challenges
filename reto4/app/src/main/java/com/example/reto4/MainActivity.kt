package com.example.reto4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.reto4.ui.TicTacToeScreen
import com.example.reto4.ui.TicTacToeViewModel
import com.example.reto4.ui.theme.Reto4Theme

class MainActivity : ComponentActivity() {
    private val gameViewModel: TicTacToeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Reto4Theme {
                TicTacToeScreen(viewModel = gameViewModel)
            }
        }
    }
}
