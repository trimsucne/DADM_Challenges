package com.example.reto9.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reto9.data.AiPersona
import com.example.reto9.data.ChatMessage
import com.example.reto9.data.MessageSender

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(viewModel: ChatViewModel) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val listState = rememberLazyListState()

    LaunchedEffect(state.messages.size, state.messages.lastOrNull()?.text) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("IAG Asistente Inteligente", fontWeight = FontWeight.Bold)
                        Text(
                            text = "${state.activePersona.iconEmoji} ${state.activePersona.title} ${if (state.apiKey.isNotBlank()) "• API Key Configurada" else "• Modo Demostración"}",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    TextButton(onClick = { viewModel.setShowApiKeyDialog(true) }) {
                        Text(if (state.apiKey.isNotBlank()) "API Key ⚙" else "+ Key API", fontWeight = FontWeight.Bold)
                    }
                    TextButton(onClick = { viewModel.clearChat() }) {
                        Text("Limpiar", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Persona Selection Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(AiPersona.values()) { persona ->
                    FilterChip(
                        selected = (state.activePersona == persona),
                        onClick = { viewModel.onPersonaSelected(persona) },
                        label = { Text("${persona.iconEmoji} ${persona.title}") }
                    )
                }
            }

            // Sample Prompt Suggestions
            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(state.activePersona.samplePrompts) { prompt ->
                    SuggestionChip(
                        onClick = { viewModel.sendMessage(prompt) },
                        label = { Text(prompt, fontSize = 11.sp) }
                    )
                }
            }

            Divider()

            // Chat Messages List
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(state.messages, key = { it.id }) { message ->
                    ChatMessageBubble(
                        message = message,
                        onCopyText = { text ->
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("AI Response", text))
                            Toast.makeText(context, "Texto copiado al portapapeles", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            // Input Bar
            Surface(
                tonalElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = state.inputText,
                        onValueChange = { viewModel.onInputTextChanged(it) },
                        placeholder = { Text("Escribe una consulta a la IAG...") },
                        singleLine = false,
                        maxLines = 3,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { viewModel.sendMessage() }),
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = { viewModel.sendMessage() },
                        enabled = state.inputText.isNotBlank() && !state.isGenerating,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(56.dp)
                    ) {
                        Text("Enviar")
                    }
                }
            }
        }
    }

    // API Key Settings Dialog
    if (state.showApiKeyDialog) {
        ApiKeySettingsDialog(
            currentKey = state.apiKey,
            onSaveKey = { newKey -> viewModel.saveApiKey(newKey) },
            onDismiss = { viewModel.setShowApiKeyDialog(false) }
        )
    }
}

@Composable
fun ChatMessageBubble(
    message: ChatMessage,
    onCopyText: (String) -> Unit
) {
    val isUser = message.sender == MessageSender.USER

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, shape = CircleShape)
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(message.persona.iconEmoji, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Card(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (!isUser) {
                    Text(
                        text = message.persona.title,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }

                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium
                )

                if (!isUser && !message.isStreaming && message.text.isNotBlank()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { onCopyText(message.text) },
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                        ) {
                            Text("Copiar", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ApiKeySettingsDialog(
    currentKey: String,
    onSaveKey: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var keyInput by remember { mutableStateOf(currentKey) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configurar Gemini API Key", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Ingresa tu API Key de Google Gemini API para conectarte directamente con los modelos de IAG en la nube (gemini-1.5-flash):",
                    style = MaterialTheme.typography.bodySmall
                )
                OutlinedTextField(
                    value = keyInput,
                    onValueChange = { keyInput = it },
                    label = { Text("Gemini API Key") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Si dejas este campo vacío, la aplicación funcionará en Modo Demostración Inteligente.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSaveKey(keyInput) }) {
                Text("Guardar API Key")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
