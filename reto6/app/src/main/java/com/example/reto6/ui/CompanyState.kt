package com.example.reto6.ui

import com.example.reto6.data.Company

data class CompanyState(
    val companies: List<Company> = emptyList(),
    val searchQuery: String = "",
    val selectedClassification: String = "All",
    val isLoading: Boolean = false,
    val editingCompany: Company? = null,
    val viewingCompany: Company? = null,
    val deletingCompany: Company? = null,
    val showAddEditDialog: Boolean = false,
    val showDetailsDialog: Boolean = false,
    val showDeleteConfirmDialog: Boolean = false
)
