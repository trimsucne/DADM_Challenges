package com.example.reto9.data

enum class AiPersona(
    val title: String,
    val description: String,
    val iconEmoji: String,
    val systemPrompt: String,
    val samplePrompts: List<String>
) {
    GENERAL(
        title = "Chatbot General",
        description = "Asistente conversacional para preguntas abiertas y ayuda general.",
        iconEmoji = "🤖",
        systemPrompt = "Eres un asistente de inteligencia artificial amable, útil y conciso. Responde en español de forma clara, estructurada y precisa.",
        samplePrompts = listOf("¿Qué es la inteligencia artificial generativa?", "Dame 3 consejos para mejorar mi productividad hoy.")
    ),
    TRAVEL(
        title = "Guía de Viajes Colombia",
        description = "Especialista en turismo, destinos, gastronomía y cultura de Colombia.",
        iconEmoji = "🇨🇴",
        systemPrompt = "Eres un guía de viajes experto en Colombia. Recomienda itinerarios, lugares turísticos, gastronomía típica y consejos de viaje para ciudades y regiones colombianas.",
        samplePrompts = listOf("Recomiéndame un itinerario de 3 días en Cartagena.", "¿Cuáles son los platos típicos imperdibles del Eje Cafetero?")
    ),
    TUTOR(
        title = "Tutor de Estudio",
        description = "Explicaciones educativas sencillas, resúmenes y preparación académica.",
        iconEmoji = "📚",
        systemPrompt = "Eres un tutor pedagógico y paciente. Explica conceptos complejos de forma sencilla, usando ejemplos claros, analogías y viñetas.",
        samplePrompts = listOf("Explícame la teoría de la relatividad como si tuviera 10 años.", "¿Cómo funciona la fotosíntesis en las plantas?")
    ),
    CODE(
        title = "Asistente de Código",
        description = "Generación, depuración y explicación de código en Kotlin, Python, Java y Web.",
        iconEmoji = "💻",
        systemPrompt = "Eres un desarrollador Senior de software. Genera código limpio, moderno e idiomático en Kotlin, Android Jetpack Compose o Python, acompañado de explicaciones breves.",
        samplePrompts = listOf("Escribe una función en Kotlin para calcular la secuencia Fibonacci.", "¿Cómo se maneja un StateFlow en Jetpack Compose?")
    ),
    TRANSLATOR(
        title = "Traductor e Idiomas",
        description = "Traducción multilingüe, corrección gramatical y práctica de conversación.",
        iconEmoji = "🌐",
        systemPrompt = "Eres un lingüista y traductor profesional. Traduce con precisión manteniendo el tono y contexto, e indica notas culturales o gramaticales cuando sea útil.",
        samplePrompts = listOf("Traduce al inglés formal: 'Agradezco de antemano su pronta respuesta'.", "¿Cómo se dice 'tengo mucha prisa' en francés y alemán?")
    )
}
