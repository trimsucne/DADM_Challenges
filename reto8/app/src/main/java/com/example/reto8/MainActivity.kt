package com.example.reto8

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.reto8.ui.PosconsumoScreen
import com.example.reto8.ui.PosconsumoViewModel
import com.example.reto8.ui.theme.Reto8Theme
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {
    private val viewModel: PosconsumoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configure osmdroid userAgent BEFORE creating MapView
        val appUserAgent = "Reto8DatosGovApp/1.0 ($packageName; Android)"
        Configuration.getInstance().userAgentValue = appUserAgent
        Configuration.getInstance().additionalHttpRequestProperties["User-Agent"] = appUserAgent
        Configuration.getInstance().load(applicationContext, getSharedPreferences("osmdroid", MODE_PRIVATE))

        enableEdgeToEdge()
        setContent {
            Reto8Theme {
                PosconsumoScreen(viewModel = viewModel)
            }
        }
    }
}
