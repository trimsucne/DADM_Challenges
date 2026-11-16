package com.example.reto8.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
    var showJumpToPageDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Datos Abiertos Colombia", fontWeight = FontWeight.Bold)
                        Text("Evaluaciones Agropecuarias y Producción (datos.gov.co)", style = MaterialTheme.typography.labelSmall)
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
                label = { Text("Buscar cultivo, producto o municipio en datos.gov.co...") },
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
                        label = { Text("Grupo Cultivo") },
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

            // Pagination Controls Bar (Siguiente & Anterior with generous comfortable padding)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { viewModel.onPreviousPage() },
                        enabled = state.currentPage > 1 && !state.isLoading,
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Anterior", fontSize = 13.sp, maxLines = 1)
                    }

                    AssistChip(
                        onClick = { showJumpToPageDialog = true },
                        label = {
                            Text(
                                text = "Pág. ${state.currentPage} ▾",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    )

                    Button(
                        onClick = { viewModel.onNextPage() },
                        enabled = state.puntos.size >= state.pageSize && !state.isLoading,
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Siguiente", fontSize = 13.sp, maxLines = 1)
                    }
                }
            }

            // Navigation Tabs (Lista | Mapa | Estadísticas)
            Surface(
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
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
            }

            // Error Message Banner Card
            state.errorMessage?.let { error ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = { viewModel.fetchPuntos() }) {
                            Text("Reintentar")
                        }
                    }
                }
            }

            // Tab Content Box (clipToBounds() and weight(1f) guarantees OsmMapView NEVER overlaps TabRow)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clipToBounds()
            ) {
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
                                    Text("No se encontraron registros para la página ${state.currentPage}.")
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
                            EstadisticasView(
                                puntos = state.puntos,
                                currentPage = state.currentPage
                            )
                        }
                    }
                }
            }
        }
    }

    // Clean Page Jump Dialog
    if (showJumpToPageDialog) {
        JumpToPageDialog(
            currentPage = state.currentPage,
            onGoToPage = { target ->
                viewModel.onGoToPage(target)
                showJumpToPageDialog = false
            },
            onDismiss = { showJumpToPageDialog = false }
        )
    }

    // Point Detail Dialog
    state.selectedPoint?.let { point ->
        AlertDialog(
            onDismissRequest = { viewModel.onPointSelected(null) },
            title = { Text(point.nombrePunto, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Ubicación: ${point.municipio}, ${point.departamento}", fontWeight = FontWeight.SemiBold)
                    Text("Grupo Cultivo: ${point.tipoResiduo}", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                    if (point.direccion.isNotBlank()) {
                        Text("Área Sembrada: ${point.direccion} Hectáreas")
                    }
                    if (point.horario.isNotBlank()) {
                        Text("Producción: ${point.horario} Toneladas")
                    }
                    if (point.contacto.isNotBlank()) {
                        Text("Año de Registro: ${point.contacto}")
                    }
                    if (point.latitud != 0.0 && point.longitud != 0.0) {
                        Text("Coordenadas GPS Exactas: Lat %.4f, Lon %.4f".format(point.latitud, point.longitud), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
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
fun JumpToPageDialog(
    currentPage: Int,
    onGoToPage: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var pageInput by remember { mutableStateOf(currentPage.toString()) }
    val quickPages = listOf(1, 5, 10, 20, 50, 100, 200, 500)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar Página", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Página actual: $currentPage", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)

                OutlinedTextField(
                    value = pageInput,
                    onValueChange = { pageInput = it },
                    label = { Text("Número de página") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Saltos rápidos:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(quickPages) { p ->
                        FilterChip(
                            selected = (pageInput == p.toString()),
                            onClick = { pageInput = p.toString() },
                            label = { Text("Pág $p") }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsed = pageInput.toIntOrNull()
                    if (parsed != null && parsed >= 1) {
                        onGoToPage(parsed)
                    }
                }
            ) {
                Text("Ir a Página")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
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

            Text("Grupo: ${point.tipoResiduo}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
            if (point.direccion.isNotBlank()) {
                Text("Área Sembrada: ${point.direccion} ha • Producción: ${point.horario} t", style = MaterialTheme.typography.bodySmall)
            }

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
fun EstadisticasView(
    puntos: List<PosconsumoPoint>,
    currentPage: Int
) {
    val catCounts = puntos.groupBy { it.tipoResiduo }.mapValues { it.value.size }
    val depCounts = puntos.groupBy { it.departamento }.mapValues { it.value.size }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize().padding(4.dp)
    ) {
        item {
            Text("Estadísticas de la Página $currentPage (${puntos.size} registros de datos.gov.co):", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Distribución por Categoria / Grupo:", fontWeight = FontWeight.Bold)
                    catCounts.forEach { (cat, count) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(cat, style = MaterialTheme.typography.bodySmall)
                            Text("$count registros", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
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
                            Text("$count registros", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
