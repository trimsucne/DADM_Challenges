package com.example.reto7.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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

val RADIUS_OPTIONS_KM = listOf(1.0, 3.0, 5.0, 10.0, 20.0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(viewModel: MapViewModel) {
    val state by viewModel.uiState.collectAsState()

    // Location Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            viewModel.fetchCurrentLocation()
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
            Column(modifier = Modifier.fillMaxSize()) {
                // Category Filter Chips
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
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

                // Interactive OpenStreetMap
                OsmMapView(
                    userLat = state.userLatitude,
                    userLon = state.userLongitude,
                    nearbyPois = state.nearbyPois,
                    onPoiSelected = { viewModel.onPoiSelected(it) },
                    modifier = Modifier.weight(1f)
                )
            }

            // POI Details Banner Card at the bottom when a marker is tapped
            state.selectedPoi?.let { pair ->
                val poi = pair.first
                val distance = pair.second

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.BottomCenter),
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
                                label = { Text("%.2f km".format(distance), fontSize = 11.sp) }
                            )
                        }

                        Text("Categoría: ${poi.category}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                        Text("Dirección: ${poi.address}", style = MaterialTheme.typography.bodySmall)
                        Text("Descripción: ${poi.description}", style = MaterialTheme.typography.bodyMedium)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { viewModel.onPoiSelected(null) }) {
                                Text("Cerrar")
                            }
                        }
                    }
                }
            }
        }
    }

    // Radius Settings Dialog
    if (state.showRadiusSettingsDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.setShowRadiusSettingsDialog(false) },
            title = { Text("Configurar Radio de Búsqueda", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Selecciona el radio en kilómetros para filtrar puntos de interés cercanos (se guarda en Preferencias):")
                    RADIUS_OPTIONS_KM.forEach { optionKm ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (state.searchRadiusKm == optionKm),
                                onClick = { viewModel.onRadiusChanged(optionKm) }
                            )
                            Text(
                                text = "%.0f km".format(optionKm),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.setShowRadiusSettingsDialog(false) }) {
                    Text("Cerrar")
                }
            }
        )
    }
}
