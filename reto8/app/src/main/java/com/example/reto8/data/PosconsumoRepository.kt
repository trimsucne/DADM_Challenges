package com.example.reto8.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PosconsumoRepository {
    private val service = DatosGovService()

    val departamentosList = listOf(
        "Todos",
        "BOYACA",
        "CUNDINAMARCA",
        "ANTIOQUIA",
        "VALLE",
        "SANTANDER",
        "ATLANTICO"
    )

    val categoriasList = listOf(
        "Todos",
        "HORTALIZAS",
        "CEREALES",
        "FRUTALES",
        "TUBERCULOS Y PLATANOS",
        "LEGUMINOSAS"
    )

    suspend fun getPuntosPosconsumo(
        departamento: String,
        tipoResiduo: String,
        query: String,
        page: Int = 1,
        pageSize: Int = 100
    ): Result<List<PosconsumoPoint>> = withContext(Dispatchers.IO) {
        service.fetchPuntos(departamento, tipoResiduo, query, page, pageSize)
    }
}
