package com.example.reto9.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.reto9.data.AiPersona
import com.example.reto9.data.AiService
import com.example.reto9.data.ChatMessage
import com.example.reto9.data.MessageSender
import com.example.reto9.data.PreferencesManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val aiService = AiService()
    private val preferencesManager = PreferencesManager(application)

    private val _uiState = MutableStateFlow(ChatState())
    val uiState: StateFlow<ChatState> = _uiState.asStateFlow()

    private var generationJob: Job? = null

    init {
        loadApiKeysAndPersona()
    }

    private fun loadApiKeysAndPersona() {
        val savedUserKey = preferencesManager.getUserApiKey()
        val effectiveKey = preferencesManager.getEffectiveApiKey()
        val savedPersona = preferencesManager.getActivePersona()
        _uiState.update {
            it.copy(
                userApiKey = savedUserKey,
                effectiveApiKey = effectiveKey,
                activePersona = savedPersona
            )
        }
        sendWelcomeMessage(savedPersona)
    }

    private fun sendWelcomeMessage(persona: AiPersona) {
        val welcomeMsg = ChatMessage(
            sender = MessageSender.AI,
            text = "¡Hola! Soy tu ${persona.title} ${persona.iconEmoji}.\n${persona.description}\n\n¿En qué te puedo colaborar hoy?",
            persona = persona
        )
        _uiState.update { it.copy(messages = listOf(welcomeMsg)) }
    }

    fun onPersonaSelected(persona: AiPersona) {
        preferencesManager.saveActivePersona(persona)
        _uiState.update { it.copy(activePersona = persona) }
        sendWelcomeMessage(persona)
    }

    fun onInputTextChanged(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun sendMessage(userText: String = _uiState.value.inputText) {
        val trimmed = userText.trim()
        if (trimmed.isBlank() || _uiState.value.isGenerating) return

        val userMessage = ChatMessage(
            sender = MessageSender.USER,
            text = trimmed,
            persona = _uiState.value.activePersona
        )

        val aiMessageId = java.util.UUID.randomUUID().toString()
        val aiPlaceholder = ChatMessage(
            id = aiMessageId,
            sender = MessageSender.AI,
            text = "Pensando...",
            isStreaming = true,
            persona = _uiState.value.activePersona
        )

        _uiState.update {
            it.copy(
                messages = it.messages + userMessage + aiPlaceholder,
                inputText = "",
                isGenerating = true
            )
        }

        generationJob?.cancel()
        generationJob = viewModelScope.launch {
            val currentState = _uiState.value
            aiService.generateResponseStream(
                prompt = trimmed,
                persona = currentState.activePersona,
                history = currentState.messages,
                apiKey = currentState.effectiveApiKey
            ).collect { streamedText ->
                _uiState.update { state ->
                    val updatedList = state.messages.map { msg ->
                        if (msg.id == aiMessageId) {
                            msg.copy(text = streamedText, isStreaming = true)
                        } else {
                            msg
                        }
                    }
                    state.copy(messages = updatedList)
                }
            }

            _uiState.update { state ->
                val finalizedList = state.messages.map { msg ->
                    if (msg.id == aiMessageId) msg.copy(isStreaming = false) else msg
                }
                state.copy(messages = finalizedList, isGenerating = false)
            }
        }
    }

    fun saveUserApiKey(key: String) {
        preferencesManager.saveUserApiKey(key)
        val effectiveKey = preferencesManager.getEffectiveApiKey()
        _uiState.update {
            it.copy(
                userApiKey = key,
                effectiveApiKey = effectiveKey,
                showApiKeyDialog = false
            )
        }
    }

    fun clearUserApiKey() {
        saveUserApiKey("")
    }

    fun setShowApiKeyDialog(show: Boolean) {
        _uiState.update { it.copy(showApiKeyDialog = show) }
    }

    fun clearChat() {
        sendWelcomeMessage(_uiState.value.activePersona)
    }
}

