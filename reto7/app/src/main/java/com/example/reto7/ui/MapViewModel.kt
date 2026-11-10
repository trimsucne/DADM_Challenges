package com.example.reto7.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.reto7.data.PoiItem
import com.example.reto7.data.PoiRepository
import com.example.reto7.data.PreferencesManager
import com.example.reto7.location.LocationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val poiRepository = PoiRepository()
    private val preferencesManager = PreferencesManager(application)
    private val locationHelper = LocationHelper(application)

    private val _uiState = MutableStateFlow(MapState())
    val uiState: StateFlow<MapState> = _uiState.asStateFlow()

    init {
        val savedRadius = preferencesManager.getSearchRadiusKm()
        val savedCategory = preferencesManager.getSelectedCategory()
        _uiState.update {
            it.copy(
                searchRadiusKm = savedRadius,
                selectedCategory = savedCategory
            )
        }
        refreshNearbyPois()
    }

    fun startLocationUpdates() {
        locationHelper.startLocationUpdates { lat, lon ->
            _uiState.update {
                it.copy(
                    userLatitude = lat,
                    userLongitude = lon,
                    isGpsActive = true
                )
            }
            refreshNearbyPois()
        }
    }

    fun fetchCurrentLocation() {
        startLocationUpdates()
    }

    fun onRadiusChanged(newRadiusKm: Double) {
        preferencesManager.setSearchRadiusKm(newRadiusKm)
        _uiState.update {
            it.copy(
                searchRadiusKm = newRadiusKm,
                showRadiusSettingsDialog = false
            )
        }
        refreshNearbyPois()
    }

    fun onCategorySelected(category: String) {
        preferencesManager.setSelectedCategory(category)
        _uiState.update { it.copy(selectedCategory = category) }
        refreshNearbyPois()
    }

    fun onPoiSelected(poiPair: Pair<PoiItem, Double>?) {
        _uiState.update { it.copy(selectedPoi = poiPair) }
    }

    fun setShowRadiusSettingsDialog(show: Boolean) {
        _uiState.update { it.copy(showRadiusSettingsDialog = show) }
    }

    private fun refreshNearbyPois() {
        val state = _uiState.value
        val pois = poiRepository.getNearbyPois(
            userLat = state.userLatitude,
            userLon = state.userLongitude,
            radiusKm = state.searchRadiusKm,
            selectedCategory = state.selectedCategory
        )
        _uiState.update { it.copy(nearbyPois = pois) }
    }

    override fun onCleared() {
        super.onCleared()
        locationHelper.stopLocationUpdates()
    }
}
