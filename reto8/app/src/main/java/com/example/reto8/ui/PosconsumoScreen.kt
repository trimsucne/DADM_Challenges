package com.example.reto8.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reto8.data.PosconsumoPoint

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosconsumoScreen(viewModel: PosconsumoViewModel) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var depExpanded by remember { mutableStateOf(false) }
    var catExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Datos Abiertos Colombia", fontWeight = FontWeight.Bold)
                        Text("Puntos Posconsumo y Medio Ambiente", style = MaterialTheme.typography.labelSmall)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    TextButton(onClick = { viewModel.fetchPuntos() }) {
                        Text("Actualizar", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Search Input
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                label = { Text("Buscar punto, dirección o municipio...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Filter Dropdowns Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Department Filter Dropdown
                ExposedDropdownMenuBox(
                    expanded = depExpanded,
                    onExpandedChange = { depExpanded = !depExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = state.selectedDepartamento,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Departamento") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = depExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = depExpanded,
                        onDismissRequest = { depExpanded = false }
                    ) {
                        viewModel.departamentosList.forEach { dep ->
                            DropdownMenuItem(
                                text = { Text(dep) },
                                onClick = {
                                    viewModel.onDepartamentoSelected(dep)
                                    depExpanded = false
                                }
                            )
                        }
                    }
                }

                // Category Filter Dropdown
                ExposedDropdownMenuBox(
                    expanded = catExpanded,
                    onExpandedChange = { catExpanded = !catExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = state.selectedCategoria,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Residuo") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = catExpanded,
                        onDismissRequest = { catExpanded = false }
                    ) {
                        viewModel.categoriasList.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    viewModel.onCategoriaSelected(cat)
                                    catExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Navigation Tabs (Lista | Mapa | Estadísticas)
            TabRow(selectedTabIndex = state.activeTab.ordinal) {
                Tab(
                    selected = state.activeTab == MainTab.LISTA,
                    onClick = { viewModel.onTabSelected(MainTab.LISTA) },
                    text = { Text("Lista (${state.puntos.size})") }
                )
                Tab(
                    selected = state.activeTab == MainTab.MAPA,
                    onClick = { viewModel.onTabSelected(MainTab.MAPA) },
                    text = { Text("Mapa") }
                )
                Tab(
                    selected = state.activeTab == MainTab.ESTADISTICAS,
                    onClick = { viewModel.onTabSelected(MainTab.ESTADISTICAS) },
                    text = { Text("Estadísticas") }
                )
            }

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                when (state.activeTab) {
                    MainTab.LISTA -> {
                        if (state.puntos.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No se encontraron puntos de posconsumo con los filtros aplicados.")
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(state.puntos, key = { it.id }) { point ->
                                    PosconsumoCard(
                                        point = point,
                                        onClick = { viewModel.onPointSelected(point) }
                                    )
                                }
                            }
                        }
                    }
                    MainTab.MAPA -> {
                        OsmMapView(
                            puntos = state.puntos,
                            onPointSelected = { viewModel.onPointSelected(it) },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    MainTab.ESTADISTICAS -> {
                        EstadisticasView(puntos = state.puntos)
                    }
                }
            }
        }
    }

    // Point Detail Dialog
    state.selectedPoint?.let { point ->
        AlertDialog(
            onDismissRequest = { viewModel.onPointSelected(null) },
            title = { Text(point.nombrePunto, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Ubicación: ${point.municipio}, ${point.departamento}", fontWeight = FontWeight.SemiBold)
                    Text("Dirección: ${point.direccion}")
                    Text("Tipo de Residuo: ${point.tipoResiduo}", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                    Text("Horarios: ${point.horario}")
                    Text("Contacto: ${point.contacto}")
                    if (point.latitud != 0.0 && point.longitud != 0.0) {
                        Text("Coordenadas GPS: Lat %.4f, Lon %.4f".format(point.latitud, point.longitud), style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                if (point.latitud != 0.0 && point.longitud != 0.0) {
                    Button(
                        onClick = {
                            val geoUri = Uri.parse("geo:${point.latitud},${point.longitud}?q=${point.latitud},${point.longitud}(${Uri.encode(point.nombrePunto)})")
                            val intent = Intent(Intent.ACTION_VIEW, geoUri)
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=${point.latitud},${point.longitud}"))
                                context.startActivity(webIntent)
                            }
                        }
                    ) {
                        Text("Cómo Llegar / Navegar")
                    }
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.onPointSelected(null) }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

@Composable
fun PosconsumoCard(
    point: PosconsumoPoint,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = point.nombrePunto,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                AssistChip(
                    onClick = {},
                    label = { Text("${point.municipio}, ${point.departamento}", fontSize = 11.sp) }
                )
            }

            Text("Dirección: ${point.direccion}", style = MaterialTheme.typography.bodySmall)
            Text("Residuo: ${point.tipoResiduo}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onClick) {
                    Text("Ver Detalles")
                }
            }
        }
    }
}

@Composable
fun EstadisticasView(puntos: List<PosconsumoPoint>) {
    val catCounts = puntos.groupBy { it.tipoResiduo }.mapValues { it.value.size }
    val depCounts = puntos.groupBy { it.departamento }.mapValues { it.value.size }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize().padding(4.dp)
    ) {
        item {
            Text("Resumen Total de Puntos Registrados: ${puntos.size}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Distribución por Tipo de Residuo:", fontWeight = FontWeight.Bold)
                    catCounts.forEach { (cat, count) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(cat, style = MaterialTheme.typography.bodySmall)
                            Text("$count puntos", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                        }
                        LinearProgressIndicator(
                            progress = { if (puntos.isNotEmpty()) count.toFloat() / puntos.size else 0f },
                            modifier = Modifier.fillMaxWidth().height(6.dp)
                        )
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Distribución por Departamento:", fontWeight = FontWeight.Bold)
                    depCounts.forEach { (dep, count) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(dep, style = MaterialTheme.typography.bodySmall)
                            Text("$count puntos", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
