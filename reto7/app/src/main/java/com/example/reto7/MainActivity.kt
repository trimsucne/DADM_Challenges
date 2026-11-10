package com.example.reto7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.reto7.ui.MapScreen
import com.example.reto7.ui.MapViewModel
import com.example.reto7.ui.theme.Reto7Theme
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {
    private val mapViewModel: MapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configure compliant User-Agent and headers for osmdroid before MapView instantiation
        val appUserAgent = "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Mobile Safari/537.36 Reto7GpsPoiApp/1.0"
        Configuration.getInstance().userAgentValue = appUserAgent
        Configuration.getInstance().additionalHttpRequestProperties["User-Agent"] = appUserAgent
        Configuration.getInstance().additionalHttpRequestProperties["Referer"] = "https://tile.openstreetmap.org/"
        Configuration.getInstance().load(applicationContext, getSharedPreferences("osmdroid", MODE_PRIVATE))

        enableEdgeToEdge()
        setContent {
            Reto7Theme {
                MapScreen(viewModel = mapViewModel)
            }
        }
    }
}
