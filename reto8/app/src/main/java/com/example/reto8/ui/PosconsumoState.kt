package com.example.reto8.ui

import com.example.reto8.data.PosconsumoPoint

enum class MainTab {
    LISTA,
    MAPA,
    ESTADISTICAS
}

data class PosconsumoState(
    val puntos: List<PosconsumoPoint> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedDepartamento: String = "Todos",
    val selectedCategoria: String = "Todos",
    val searchQuery: String = "",
    val activeTab: MainTab = MainTab.LISTA,
    val selectedPoint: PosconsumoPoint? = null
)
