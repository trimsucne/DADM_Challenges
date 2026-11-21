package com.example.reto9.data

import android.content.Context
import android.content.SharedPreferences
import com.example.reto9.config.AppConfig

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "reto9_ai_prefs"
        private const val KEY_USER_API_KEY = "gemini_api_key"
        private const val KEY_PERSONA = "active_persona"
    }

    private fun sanitizeKey(key: String): String {
        return key
            .replace("\r", "")
            .replace("\n", "")
            .replace(" ", "")
            .trim()
            .removeSurrounding("\"")
            .removeSurrounding("'")
            .trim()
    }

    /**
     * Retorna la API Key personalizada ingresada manualmente por el usuario.
     */
    fun getUserApiKey(): String {
        val raw = prefs.getString(KEY_USER_API_KEY, "") ?: ""
        return sanitizeKey(raw)
    }

    /**
     * Guarda o borra la API Key personalizada del usuario.
     */
    fun saveUserApiKey(apiKey: String) {
        prefs.edit().putString(KEY_USER_API_KEY, sanitizeKey(apiKey)).apply()
    }

    /**
     * Retorna la API Key efectiva:
     * 1. Si el usuario ingresó su propia clave, usa esa.
     * 2. Si no, utiliza la clave por defecto de la app (AppConfig.DEFAULT_GEMINI_API_KEY).
     */
    fun getEffectiveApiKey(): String {
        val userKey = getUserApiKey()
        return if (userKey.isNotBlank()) {
            userKey
        } else {
            sanitizeKey(AppConfig.DEFAULT_GEMINI_API_KEY)
        }
    }

    fun getActivePersona(): AiPersona {
        val name = prefs.getString(KEY_PERSONA, AiPersona.GENERAL.name) ?: AiPersona.GENERAL.name
        return try {
            AiPersona.valueOf(name)
        } catch (_: Exception) {
            AiPersona.GENERAL
        }
    }

    fun saveActivePersona(persona: AiPersona) {
        prefs.edit().putString(KEY_PERSONA, persona.name).apply()
    }
}
