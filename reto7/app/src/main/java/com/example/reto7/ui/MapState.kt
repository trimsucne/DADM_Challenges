package com.example.reto7.ui

import com.example.reto7.data.PoiItem

data class MapState(
    val userLatitude: Double = 4.6097,
    val userLongitude: Double = -74.0817,
    val isGpsActive: Boolean = false,
    val searchRadiusKm: Double = 5.0,
    val selectedCategory: String = "All",
    val nearbyPois: List<Pair<PoiItem, Double>> = emptyList(),
    val selectedPoi: Pair<PoiItem, Double>? = null,
    val showRadiusSettingsDialog: Boolean = false,
    val showPermissionDeniedMessage: Boolean = false
)
