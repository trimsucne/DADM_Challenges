package com.example.reto9.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "reto9_ai_prefs"
        private const val KEY_API_KEY = "gemini_api_key"
        private const val KEY_PERSONA = "active_persona"
    }

    fun getApiKey(): String {
        return prefs.getString(KEY_API_KEY, "") ?: ""
    }

    fun saveApiKey(apiKey: String) {
        prefs.edit().putString(KEY_API_KEY, apiKey.trim()).apply()
    }

    fun getActivePersona(): AiPersona {
        val name = prefs.getString(KEY_PERSONA, AiPersona.GENERAL.name) ?: AiPersona.GENERAL.name
        return try {
            AiPersona.valueOf(name)
        } catch (e: Exception) {
            AiPersona.GENERAL
        }
    }

    fun saveActivePersona(persona: AiPersona) {
        prefs.edit().putString(KEY_PERSONA, persona.name).apply()
    }
}
