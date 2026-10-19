package com.example.reto6.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CompanyRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    suspend fun getCompanies(query: String = "", classification: String = "All"): List<Company> =
        withContext(Dispatchers.IO) {
            val list = dbHelper.searchCompanies(query, classification)
            if (list.isEmpty() && query.isBlank() && classification == "All") {
                seedInitialData()
                dbHelper.searchCompanies("", "All")
            } else {
                list
            }
        }

    suspend fun addCompany(company: Company): Long = withContext(Dispatchers.IO) {
        dbHelper.insertCompany(company)
    }

    suspend fun updateCompany(company: Company): Int = withContext(Dispatchers.IO) {
        dbHelper.updateCompany(company)
    }

    suspend fun deleteCompany(id: Long): Int = withContext(Dispatchers.IO) {
        dbHelper.deleteCompany(id)
    }

    private fun seedInitialData() {
        val initialCompanies = listOf(
            Company(
                name = "Globant Colombia",
                url = "https://www.globant.com",
                phone = "+57 601 743 0000",
                email = "contact@globant.com",
                services = "IA, Cloud Computing, Transformación Digital",
                classification = "Consultoría"
            ),
            Company(
                name = "Endava Colombia",
                url = "https://www.endava.com",
                phone = "+57 604 444 8899",
                email = "info.colombia@endava.com",
                services = "Desarrollo de Software a Medida, DevOps, QA Automation",
                classification = "Desarrollo a la medida"
            ),
            Company(
                name = "PSL S.A.S / BairesDev",
                url = "https://www.bairesdev.com",
                phone = "+57 604 321 4500",
                email = "sales@bairesdev.com",
                services = "Fábrica de Software, Staff Augmentation, QA Testing",
                classification = "Fábrica de software"
            )
        )
        initialCompanies.forEach { dbHelper.insertCompany(it) }
    }
}
