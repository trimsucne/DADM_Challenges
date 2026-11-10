package com.example.reto7.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reto7.R

val CATEGORY_OPTIONS = listOf(
    "All",
    "Hospital",
    "Tourism",
    "Park",
    "Education",
    "Restaurant"
)

val PRESET_RADIUS_KM = listOf(1.0, 5.0, 10.0, 25.0, 50.0, 100.0, 250.0, 500.0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(viewModel: MapViewModel) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Location Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            viewModel.startLocationUpdates()
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Puntos de Interés GPS", fontWeight = FontWeight.Bold)
                        Text(
                            text = "Radio: %.1f km • %d lugares cerca".format(
                                state.searchRadiusKm,
                                state.nearbyPois.size
                            ),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = { viewModel.setShowRadiusSettingsDialog(true) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_menu_radius),
                            contentDescription = "Configurar Radio de Búsqueda"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Interactive OpenStreetMap (Fills the entire background box)
            OsmMapView(
                userLat = state.userLatitude,
                userLon = state.userLongitude,
                radiusKm = state.searchRadiusKm,
                nearbyPois = state.nearbyPois,
                onPoiSelected = { viewModel.onPoiSelected(it) },
                modifier = Modifier.fillMaxSize()
            )

            // Category Filter Chips Floating Overlay Bar (Guaranteed on top of MapView via elevated Surface & zIndex)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .align(Alignment.TopCenter)
                    .zIndex(2f),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                tonalElevation = 6.dp,
                shadowElevation = 4.dp
            ) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(CATEGORY_OPTIONS) { category ->
                        FilterChip(
                            selected = (state.selectedCategory == category),
                            onClick = { viewModel.onCategorySelected(category) },
                            label = { Text(category) }
                        )
                    }
                }
            }

            // POI Details Banner Card with GPS Coordinates & Driving Directions Button
            state.selectedPoi?.let { pair ->
                val poi = pair.first
                val distance = pair.second

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.BottomCenter)
                        .zIndex(2f),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = poi.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            AssistChip(
                                onClick = {},
                                label = { Text("%.1f km".format(distance), fontSize = 11.sp) }
                            )
                        }

                        Text("Categoría: ${poi.category}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                        Text("Coordenadas GPS: Lat %.4f, Lon %.4f".format(poi.latitude, poi.longitude), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                        Text("Dirección: ${poi.address}", style = MaterialTheme.typography.bodySmall)
                        Text("Descripción: ${poi.description}", style = MaterialTheme.typography.bodyMedium)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    val geoUri = Uri.parse("geo:${poi.latitude},${poi.longitude}?q=${poi.latitude},${poi.longitude}(${Uri.encode(poi.name)})")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, geoUri)
                                    mapIntent.setPackage("com.google.android.apps.maps")
                                    try {
                                        context.startActivity(mapIntent)
                                    } catch (e: Exception) {
                                        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=${poi.latitude},${poi.longitude}"))
                                        context.startActivity(webIntent)
                                    }
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Cómo Llegar / Navegar", fontSize = 13.sp)
                            }

                            TextButton(onClick = { viewModel.onPoiSelected(null) }) {
                                Text("Cerrar")
                            }
                        }
                    }
                }
            }
        }
    }

    // Custom Radius Settings Dialog
    if (state.showRadiusSettingsDialog) {
        CustomRadiusDialog(
            currentRadiusKm = state.searchRadiusKm,
            onApply = { newRadiusKm ->
                viewModel.onRadiusChanged(newRadiusKm)
            },
            onDismiss = { viewModel.setShowRadiusSettingsDialog(false) }
        )
    }
}

@Composable
fun CustomRadiusDialog(
    currentRadiusKm: Double,
    onApply: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    var radiusInput by remember { mutableStateOf("%.1f".format(currentRadiusKm)) }
    var sliderValue by remember { mutableFloatStateOf(currentRadiusKm.toFloat().coerceIn(0.5f, 500f)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Radio Personalizado (km)", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Ajusta el radio en kilómetros con el deslizador o escribe cualquier valor exacto (ej. 50 km, 200 km, 1000 km):",
                    style = MaterialTheme.typography.bodySmall
                )

                // Slider (0.5 km to 500 km)
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Radio actual:", style = MaterialTheme.typography.bodyMedium)
                        Text("%.1f km".format(sliderValue), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Slider(
                        value = sliderValue,
                        onValueChange = { newValue ->
                            sliderValue = newValue
                            radiusInput = "%.1f".format(newValue)
                        },
                        valueRange = 0.5f..500.0f
                    )
                }

                // Text Input for Exact Custom Number (up to 10000.0 km)
                OutlinedTextField(
                    value = radiusInput,
                    onValueChange = { input ->
                        radiusInput = input
                        input.replace(',', '.').toDoubleOrNull()?.let { parsed ->
                            if (parsed in 0.1..10000.0) {
                                sliderValue = parsed.toFloat().coerceIn(0.5f, 500f)
                            }
                        }
                    },
                    label = { Text("Valor exacto en km (ej: 15, 80, 250, 1000)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // Quick Presets
                Text("Valores rápidos:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(PRESET_RADIUS_KM) { preset ->
                        FilterChip(
                            selected = (sliderValue.toDouble() == preset),
                            onClick = {
                                sliderValue = preset.toFloat()
                                radiusInput = "%.1f".format(preset)
                            },
                            label = { Text("%.0f km".format(preset)) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsed = radiusInput.replace(',', '.').toDoubleOrNull() ?: sliderValue.toDouble()
                    val finalRadius = parsed.coerceIn(0.1, 10000.0)
                    onApply(finalRadius)
                }
            ) {
                Text("Aplicar")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
