package com.example.reto6.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.reto6.data.Company
import com.example.reto6.data.CompanyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CompanyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CompanyRepository(application)

    private val _uiState = MutableStateFlow(CompanyState())
    val uiState: StateFlow<CompanyState> = _uiState.asStateFlow()

    init {
        loadCompanies()
    }

    fun loadCompanies() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val query = _uiState.value.searchQuery
            val filter = _uiState.value.selectedClassification
            val result = repository.getCompanies(query, filter)
            _uiState.update { it.copy(companies = result, isLoading = false) }
        }
    }

    fun onSearchQueryChanged(newQuery: String) {
        _uiState.update { it.copy(searchQuery = newQuery) }
        loadCompanies()
    }

    fun onClassificationFilterChanged(classification: String) {
        _uiState.update { it.copy(selectedClassification = classification) }
        loadCompanies()
    }

    fun onOpenAddDialog() {
        _uiState.update {
            it.copy(
                editingCompany = null,
                showAddEditDialog = true
            )
        }
    }

    fun onOpenEditDialog(company: Company) {
        _uiState.update {
            it.copy(
                editingCompany = company,
                showAddEditDialog = true
            )
        }
    }

    fun onOpenDetailsDialog(company: Company) {
        _uiState.update {
            it.copy(
                viewingCompany = company,
                showDetailsDialog = true
            )
        }
    }

    fun onOpenDeleteConfirmDialog(company: Company) {
        _uiState.update {
            it.copy(
                deletingCompany = company,
                showDeleteConfirmDialog = true
            )
        }
    }

    fun onDismissDialogs() {
        _uiState.update {
            it.copy(
                showAddEditDialog = false,
                showDetailsDialog = false,
                showDeleteConfirmDialog = false,
                editingCompany = null,
                viewingCompany = null,
                deletingCompany = null
            )
        }
    }

    fun saveCompany(
        name: String,
        url: String,
        phone: String,
        email: String,
        services: String,
        classification: String
    ) {
        viewModelScope.launch {
            val currentEditing = _uiState.value.editingCompany
            if (currentEditing == null) {
                val newCompany = Company(
                    name = name,
                    url = url,
                    phone = phone,
                    email = email,
                    services = services,
                    classification = classification
                )
                repository.addCompany(newCompany)
            } else {
                val updatedCompany = currentEditing.copy(
                    name = name,
                    url = url,
                    phone = phone,
                    email = email,
                    services = services,
                    classification = classification
                )
                repository.updateCompany(updatedCompany)
            }
            onDismissDialogs()
            loadCompanies()
        }
    }

    fun confirmDeleteCompany() {
        val companyToDelete = _uiState.value.deletingCompany ?: return
        viewModelScope.launch {
            repository.deleteCompany(companyToDelete.id)
            onDismissDialogs()
            loadCompanies()
        }
    }
}
