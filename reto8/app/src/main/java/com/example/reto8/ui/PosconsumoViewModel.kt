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
                query = state.searchQuery,
                page = state.currentPage,
                pageSize = state.pageSize
            )

            result.onSuccess { data ->
                _uiState.update {
                    it.copy(
                        puntos = data,
                        isLoading = false,
                        errorMessage = if (data.isEmpty()) "No se encontraron registros en el servicio de datos.gov.co para la página ${state.currentPage}." else null
                    )
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        puntos = emptyList(),
                        isLoading = false,
                        errorMessage = "Error de conexión con el servicio web (datos.gov.co): ${exception.localizedMessage ?: exception.message ?: "Verifique la conexión a Internet"}"
                    )
                }
            }
        }
    }

    fun onDepartamentoSelected(dep: String) {
        _uiState.update { it.copy(selectedDepartamento = dep, currentPage = 1) }
        fetchPuntos()
    }

    fun onCategoriaSelected(cat: String) {
        _uiState.update { it.copy(selectedCategoria = cat, currentPage = 1) }
        fetchPuntos()
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query, currentPage = 1) }
        fetchPuntos()
    }

    fun onNextPage() {
        _uiState.update { it.copy(currentPage = it.currentPage + 1) }
        fetchPuntos()
    }

    fun onPreviousPage() {
        if (_uiState.value.currentPage > 1) {
            _uiState.update { it.copy(currentPage = it.currentPage - 1) }
            fetchPuntos()
        }
    }

    fun onGoToPage(targetPage: Int) {
        if (targetPage >= 1) {
            _uiState.update { it.copy(currentPage = targetPage) }
            fetchPuntos()
        }
    }

    fun onTabSelected(tab: MainTab) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    fun onPointSelected(point: PosconsumoPoint?) {
        _uiState.update { it.copy(selectedPoint = point) }
    }
}
