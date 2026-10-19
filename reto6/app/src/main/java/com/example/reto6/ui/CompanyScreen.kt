package com.example.reto6.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reto6.data.Company

val CLASSIFICATION_OPTIONS = listOf(
    "All",
    "Consultoría",
    "Desarrollo a la medida",
    "Fábrica de software"
)

val FORM_CLASSIFICATIONS = listOf(
    "Consultoría",
    "Desarrollo a la medida",
    "Fábrica de software"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyScreen(viewModel: CompanyViewModel) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Directorio de Empresas", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onOpenAddDialog() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Text("+", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search Input
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                label = { Text("Buscar por nombre de empresa...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Classification Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(CLASSIFICATION_OPTIONS) { classification ->
                    FilterChip(
                        selected = (state.selectedClassification == classification),
                        onClick = { viewModel.onClassificationFilterChanged(classification) },
                        label = { Text(classification) }
                    )
                }
            }

            Divider()

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.companies.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron empresas con los filtros aplicados.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.companies, key = { it.id }) { company ->
                        CompanyCard(
                            company = company,
                            onDetailsClick = { viewModel.onOpenDetailsDialog(company) },
                            onEditClick = { viewModel.onOpenEditDialog(company) },
                            onDeleteClick = { viewModel.onOpenDeleteConfirmDialog(company) }
                        )
                    }
                }
            }
        }
    }

    // Add / Edit Dialog
    if (state.showAddEditDialog) {
        AddEditCompanyDialog(
            company = state.editingCompany,
            onSave = { name, url, phone, email, services, classification ->
                viewModel.saveCompany(name, url, phone, email, services, classification)
            },
            onDismiss = { viewModel.onDismissDialogs() }
        )
    }

    // Details Dialog
    if (state.showDetailsDialog && state.viewingCompany != null) {
        CompanyDetailsDialog(
            company = state.viewingCompany!!,
            onDismiss = { viewModel.onDismissDialogs() }
        )
    }

    // Delete Confirmation Dialog
    if (state.showDeleteConfirmDialog && state.deletingCompany != null) {
        AlertDialog(
            onDismissRequest = { viewModel.onDismissDialogs() },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Está seguro de que desea eliminar la empresa '${state.deletingCompany?.name}' de la base de datos?") },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmDeleteCompany() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.onDismissDialogs() }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun CompanyCard(
    company: Company,
    onDetailsClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = company.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                AssistChip(
                    onClick = {},
                    label = { Text(company.classification, fontSize = 11.sp) }
                )
            }

            Text("Tel: ${company.phone}", style = MaterialTheme.typography.bodySmall)
            Text("Email: ${company.email}", style = MaterialTheme.typography.bodySmall)
            Text(
                "Servicios: ${company.services}",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onDetailsClick) {
                    Text("Detalles")
                }
                TextButton(onClick = onEditClick) {
                    Text("Editar")
                }
                TextButton(onClick = onDeleteClick) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCompanyDialog(
    company: Company?,
    onSave: (name: String, url: String, phone: String, email: String, services: String, classification: String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(company?.name ?: "") }
    var url by remember { mutableStateOf(company?.url ?: "https://") }
    var phone by remember { mutableStateOf(company?.phone ?: "") }
    var email by remember { mutableStateOf(company?.email ?: "") }
    var services by remember { mutableStateOf(company?.services ?: "") }
    var classification by remember { mutableStateOf(company?.classification ?: FORM_CLASSIFICATIONS.first()) }
    var expanded by remember { mutableStateOf(false) }

    val isFormValid = name.isNotBlank() && phone.isNotBlank() && email.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (company == null) "Agregar Nueva Empresa" else "Editar Empresa") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre de la Empresa *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("URL Sitio Web") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Teléfono de Contacto *") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email de Contacto *") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = services,
                    onValueChange = { services = it },
                    label = { Text("Productos y Servicios") },
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = classification,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Clasificación") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        FORM_CLASSIFICATIONS.forEach { selection ->
                            DropdownMenuItem(
                                text = { Text(selection) },
                                onClick = {
                                    classification = selection
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isFormValid) {
                        onSave(name, url, phone, email, services, classification)
                    }
                },
                enabled = isFormValid
            ) {
                Text("Guardar")
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
fun CompanyDetailsDialog(
    company: Company,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(company.name, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Clasificación:", fontWeight = FontWeight.Bold)
                Text(company.classification)

                Text("Página Web:", fontWeight = FontWeight.Bold)
                Text(
                    text = company.url,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(company.url))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                )

                Text("Teléfono:", fontWeight = FontWeight.Bold)
                Text(company.phone)

                Text("Email:", fontWeight = FontWeight.Bold)
                Text(company.email)

                Text("Productos y Servicios:", fontWeight = FontWeight.Bold)
                Text(company.services)
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}
