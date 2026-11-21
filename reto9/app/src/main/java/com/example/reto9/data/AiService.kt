package com.example.reto9.data

import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class AiService {

    // Modelos actualizados compatibles con la API Key del usuario (gemini-3.8-flash)
    private val candidateModels = listOf(
        "gemini-3.8-flash",
        "gemini-3.5-flash",
        "gemini-flash-latest",
        "gemini-pro-latest"
    )

    suspend fun generateResponseStream(
        prompt: String,
        persona: AiPersona,
        history: List<ChatMessage>,
        apiKey: String
    ): Flow<String> = flow {
        val cleanKey = apiKey
            .replace("\r", "")
            .replace("\n", "")
            .replace(" ", "")
            .trim()
            .removeSurrounding("\"")
            .removeSurrounding("'")
            .trim()

        if (cleanKey.isNotBlank()) {
            val fullPrompt = "${persona.systemPrompt}\n\nConsulta del usuario: $prompt"
            var success = false
            var lastErrorMsg = ""

            for (modelName in candidateModels) {
                try {
                    val generativeModel = GenerativeModel(
                        modelName = modelName,
                        apiKey = cleanKey
                    )

                    val responseStream = generativeModel.generateContentStream(fullPrompt)
                    var accumulated = ""
                    
                    responseStream.collect { chunk ->
                        val textChunk = chunk.text ?: ""
                        accumulated += textChunk
                        emit(accumulated)
                    }

                    if (accumulated.isNotBlank()) {
                        success = true
                        break
                    }
                } catch (e: Exception) {
                    val msg = e.localizedMessage ?: e.message ?: "Error de API"
                    lastErrorMsg = "[$modelName] $msg"
                    e.printStackTrace()
                }
            }

            if (!success) {
                val cleanError = "⚠️ Error Gemini SDK: $lastErrorMsg"
                emit("$cleanError\n\n${generateDemoResponse(prompt, persona)}")
            }
        } else {
            val fullDemo = generateDemoResponse(prompt, persona)
            val chunks = fullDemo.chunked(12)
            var current = ""
            for (chunk in chunks) {
                current += chunk
                emit(current)
                delay(30)
            }
        }
    }.flowOn(Dispatchers.IO)

    private fun generateDemoResponse(prompt: String, persona: AiPersona): String {
        val lower = prompt.lowercase()
        return when (persona) {
            AiPersona.GENERAL -> {
                when {
                    "hola" in lower || "buenos" in lower -> "¡Hola! ¿En qué puedo ayudarte hoy? Estoy aquí para responder preguntas, generar textos o ayudarte con tus proyectos."
                    "inteligencia" in lower -> "La Inteligencia Artificial Generativa (IAG) es una rama de la IA capaz de crear contenido original como texto, imágenes, código o audio a partir de patrones aprendidos en grandes volúmenes de datos."
                    else -> "Has dicho: \"$prompt\". Como asistente general, puedo ayudarte a analizar este tema, resumir información o darte ideas creativas. ¿Qué aspecto te gustaría profundizar?"
                }
            }
            AiPersona.TRAVEL -> {
                when {
                    "cartagena" in lower -> "¡Cartagena de Indias es espectacular! Te recomiendo:\n1. Recorrer la Ciudad Amurallada y el barrio Getsemaní.\n2. Visitar el Castillo de San Felipe de Barajas.\n3. Pasar un día de playa en las Islas del Rosario.\n4. Disfrutar de una arepa de huevo y mariscos frescos."
                    "eje" in lower || "cafetero" in lower || "salento" in lower -> "El Eje Cafetero ofrece paisajes increíbles:\n1. Valle de Cocora con sus Palmas de Cera gigantes.\n2. Recorrido por fincas cafeteras tradicionales en Salento y Filandia.\n3. Relajación en los Termales de Santa Rosa de Cabal.\n4. Probar un café de origen especial de alta montaña."
                    else -> "Sobre tu consulta de viaje en Colombia ($prompt):\nColombia es el país más biodiverso por metro cuadrado. Te sugiero explorar destinos icónicos como el Parque Tayrona, Medellín, Monserrate en Bogotá, o el Desierto de la Tatacoa."
                }
            }
            AiPersona.TUTOR -> {
                "Explicación Pedagógica sobre tu consulta ($prompt):\n\n1. Concepto Clave: Para entender esto fácilmente, imagínalo como un sistema estructurado por pasos.\n2. Ejemplo Práctico: Imagina que estás organizando una biblioteca donde cada elemento tiene una etiqueta única.\n3. Resumen: Los puntos fundamentales son la preparación, la ejecución estructurada y la revisión de resultados."
            }
            AiPersona.CODE -> {
                when {
                    "kotlin" in lower || "compose" in lower -> "```kotlin\n// Ejemplo idiomático en Kotlin / Jetpack Compose\n@Composable\nfun GreetingCard(name: String) {\n    Card(modifier = Modifier.padding(16.dp)) {\n        Text(\n            text = \"Hola, \$name!\",\n            style = MaterialTheme.typography.titleMedium\n        )\n    }\n}\n```\n*Explicación*: Este componente declarativo recibe un parámetro `name` y renderiza una tarjeta estilizada en Jetpack Compose."
                    else -> "```python\n# Solución idiomática para: $prompt\ndef procesar_datos(lista):\n    return [item.strip().capitalize() for item in lista if item]\n\nresultado = procesar_datos([\"hola \", \" mundo \", \"\"]) \nprint(resultado)  # ['Hola', 'Mundo']\n```"
                }
            }
            AiPersona.TRANSLATOR -> {
                "```text\n[Traducción al Inglés]\n\"${prompt.replace("Traduce al inglés:", "").replace("Traduce:", "").trim()}\"\n--> \"I would be happy to assist you with your request as soon as possible.\"\n```\n*Nota Gramatical*: Se ha adaptado el tono formal para mantener cortesía y naturalidad lingüística."
            }
        }
    }
}
