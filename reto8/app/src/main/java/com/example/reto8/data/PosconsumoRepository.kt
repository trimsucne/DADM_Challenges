package com.example.reto8.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PosconsumoRepository {
    private val service = DatosGovService()

    val departamentosList = listOf(
        "Todos",
        "Cundinamarca",
        "Antioquia",
        "Valle del Cauca",
        "Santander",
        "Atlántico"
    )

    val categoriasList = listOf(
        "Todos",
        "Pilas y Baterías",
        "Medicamentos Vencidos",
        "Lámparas y Bombillos",
        "Electrodomésticos y RAEES",
        "Aceite Usado"
    )

    suspend fun getPuntosPosconsumo(
        departamento: String,
        tipoResiduo: String,
        query: String
    ): List<PosconsumoPoint> = withContext(Dispatchers.IO) {
        service.fetchPuntos(departamento, tipoResiduo, query)
    }
}
