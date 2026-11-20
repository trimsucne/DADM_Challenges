package com.example.reto9

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.reto9.ui.ChatScreen
import com.example.reto9.ui.ChatViewModel
import com.example.reto9.ui.theme.Reto9Theme

class MainActivity : ComponentActivity() {
    private val viewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Reto9Theme {
                ChatScreen(viewModel = viewModel)
            }
        }
    }
}
