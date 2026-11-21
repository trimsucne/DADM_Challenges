package com.example.reto9.ui

import com.example.reto9.data.AiPersona
import com.example.reto9.data.ChatMessage

data class ChatState(
    val messages: List<ChatMessage> = emptyList(),
    val activePersona: AiPersona = AiPersona.GENERAL,
    val userApiKey: String = "",
    val effectiveApiKey: String = "",
    val inputText: String = "",
    val isGenerating: Boolean = false,
    val showApiKeyDialog: Boolean = false
) {
    val isUsingCustomKey: Boolean
        get() = userApiKey.isNotBlank()

    val isUsingDefaultKey: Boolean
        get() = userApiKey.isBlank() && effectiveApiKey.isNotBlank()

    val isDemoMode: Boolean
        get() = effectiveApiKey.isBlank()
}

