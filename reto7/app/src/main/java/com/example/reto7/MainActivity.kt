package com.example.reto7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.reto7.ui.MapScreen
import com.example.reto7.ui.MapViewModel
import com.example.reto7.ui.theme.Reto7Theme

class MainActivity : ComponentActivity() {
    private val mapViewModel: MapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Reto7Theme {
                MapScreen(viewModel = mapViewModel)
            }
        }
    }
}
