package com.example.reto9.config

object AppConfig {
    /**
     * Clave API por defecto de Google Gemini cargada desde el archivo .env o local.properties.
     * Se accede dinámicamente para evitar errores de referencia en el IDE de Android Studio.
     */
    val DEFAULT_GEMINI_API_KEY: String by lazy {
        try {
            val clazz = Class.forName("com.example.reto9.BuildConfig")
            val field = clazz.getField("GEMINI_API_KEY")
            (field.get(null) as? String)?.trim() ?: ""
        } catch (_: Throwable) {
            ""
        }

    }
}
