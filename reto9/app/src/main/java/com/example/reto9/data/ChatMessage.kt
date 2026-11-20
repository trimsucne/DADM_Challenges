package com.example.reto9.data

enum class MessageSender {
    USER,
    AI,
    SYSTEM
}

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isStreaming: Boolean = false,
    val persona: AiPersona = AiPersona.GENERAL
)
