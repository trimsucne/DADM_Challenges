package com.example.reto6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.reto6.ui.CompanyScreen
import com.example.reto6.ui.CompanyViewModel
import com.example.reto6.ui.theme.Reto6Theme

class MainActivity : ComponentActivity() {
    private val companyViewModel: CompanyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Reto6Theme {
                CompanyScreen(viewModel = companyViewModel)
            }
        }
    }
}
