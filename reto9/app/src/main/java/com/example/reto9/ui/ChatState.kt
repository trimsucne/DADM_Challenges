package com.example.reto9.ui

import com.example.reto9.data.AiPersona
import com.example.reto9.data.ChatMessage

data class ChatState(
    val messages: List<ChatMessage> = emptyList(),
    val activePersona: AiPersona = AiPersona.GENERAL,
    val apiKey: String = "",
    val inputText: String = "",
    val isGenerating: Boolean = false,
    val showApiKeyDialog: Boolean = false
)
