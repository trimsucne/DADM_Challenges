package com.example.reto7.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "reto7_gps_prefs"
        private const val KEY_RADIUS_KM = "searchRadiusKm"
        private const val KEY_CATEGORY = "selectedCategory"
    }

    fun getSearchRadiusKm(): Double {
        return prefs.getFloat(KEY_RADIUS_KM, 5.0f).toDouble()
    }

    fun setSearchRadiusKm(radiusKm: Double) {
        prefs.edit().putFloat(KEY_RADIUS_KM, radiusKm.toFloat()).apply()
    }

    fun getSelectedCategory(): String {
        return prefs.getString(KEY_CATEGORY, "All") ?: "All"
    }

    fun setSelectedCategory(category: String) {
        prefs.edit().putString(KEY_CATEGORY, category).apply()
    }
}
