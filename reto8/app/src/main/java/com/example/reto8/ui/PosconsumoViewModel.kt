package com.example.reto8.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.reto8.data.PosconsumoPoint
import com.example.reto8.data.PosconsumoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PosconsumoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PosconsumoRepository()

    private val _uiState = MutableStateFlow(PosconsumoState())
    val uiState: StateFlow<PosconsumoState> = _uiState.asStateFlow()

    val departamentosList = repository.departamentosList
    val categoriasList = repository.categoriasList

    init {
        fetchPuntos()
    }

    fun fetchPuntos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val state = _uiState.value
            val result = repository.getPuntosPosconsumo(
                departamento = state.selectedDepartamento,
                tipoResiduo = state.selectedCategoria,
                query = state.searchQuery
            )
            _uiState.update { it.copy(puntos = result, isLoading = false) }
        }
    }

    fun onDepartamentoSelected(dep: String) {
        _uiState.update { it.copy(selectedDepartamento = dep) }
        fetchPuntos()
    }

    fun onCategoriaSelected(cat: String) {
        _uiState.update { it.copy(selectedCategoria = cat) }
        fetchPuntos()
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        fetchPuntos()
    }

    fun onTabSelected(tab: MainTab) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    fun onPointSelected(point: PosconsumoPoint?) {
        _uiState.update { it.copy(selectedPoint = point) }
    }
}
