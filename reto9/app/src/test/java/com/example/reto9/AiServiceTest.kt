package com.example.reto9

import com.example.reto9.data.AiPersona
import com.example.reto9.data.AiService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AiServiceTest {

    private val aiService = AiService()

    @Test
    fun testGenerateResponseStream_generatesResponse() {
        runBlocking {
            val responseFlow = aiService.generateResponseStream(
                prompt = "Hola, recomiéndame qué visitar en Cartagena",
                persona = AiPersona.TRAVEL,
                history = emptyList(),
                apiKey = ""
            )
            val responseText = responseFlow.first()
            assertNotNull(responseText)
            assertTrue(responseText.isNotBlank())
        }
    }

    @Test
    fun testPersonasPrompts_areConfigured() {
        val personas = AiPersona.values()
        assertTrue(personas.isNotEmpty())
        personas.forEach { persona ->
            assertTrue(persona.systemPrompt.isNotBlank())
            assertTrue(persona.samplePrompts.isNotEmpty())
        }
    }
}
