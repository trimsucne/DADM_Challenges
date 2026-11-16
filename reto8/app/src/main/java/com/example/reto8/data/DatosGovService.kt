package com.example.reto8.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface DatosGovApi {
    @GET("resource/m9d2-sp3v.json")
    suspend fun getPuntosPosconsumo(
        @Query("\$limit") limit: Int = 100,
        @Query("departamento") departamento: String? = null,
        @Query("tipo_residuo") tipoResiduo: String? = null,
        @Query("\$q") searchQuery: String? = null
    ): List<PosconsumoPoint>
}

class DatosGovService {

    private val api: DatosGovApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.datos.gov.co/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DatosGovApi::class.java)
    }

    suspend fun fetchPuntos(
        departamento: String = "Todos",
        tipoResiduo: String = "Todos",
        query: String = ""
    ): List<PosconsumoPoint> {
        return try {
            val depParam = if (departamento != "Todos") departamento else null
            val catParam = if (tipoResiduo != "Todos") tipoResiduo else null
            val qParam = if (query.isNotBlank()) query else null

            val remoteResults = api.getPuntosPosconsumo(
                limit = 100,
                departamento = depParam,
                tipoResiduo = catParam,
                searchQuery = qParam
            )

            if (remoteResults.isNotEmpty()) remoteResults else getFallbackPuntos(departamento, tipoResiduo, query)
        } catch (e: Exception) {
            e.printStackTrace()
            getFallbackPuntos(departamento, tipoResiduo, query)
        }
    }

    private fun getFallbackPuntos(
        departamento: String,
        tipoResiduo: String,
        query: String
    ): List<PosconsumoPoint> {
        val fallbackList = listOf(
            PosconsumoPoint("fb1", "Punto Verde Lúmina - Centro Mayor", "Cundinamarca", "Bogotá", "Cl 38A Sur #34D-50", "Lámparas y Bombillos", "Lun-Sáb 8am-7pm", "+57 601 7430000", 4.5881, -74.1238),
            PosconsumoPoint("fb2", "EcoPunto Pilas con el Ambiente - Unicentro", "Cundinamarca", "Bogotá", "Av. 15 #124-30", "Pilas y Baterías", "Lun-Dom 9am-8pm", "+57 601 3214500", 4.7012, -74.0321),
            PosconsumoPoint("fb3", "Punto Azul Medicamentos - Droguería Cruz Verde Calima", "Cundinamarca", "Bogotá", "Cl. 19 #28-80", "Medicamentos Vencidos", "Lun-Sáb 7am-9pm", "018000 910200", 4.6189, -74.0882),
            PosconsumoPoint("fb4", "Recopa RAEES - Titán Plaza", "Cundinamarca", "Bogotá", "Av. Boyacá #80-94", "Electrodomésticos y RAEES", "Lun-Dom 10am-7pm", "+57 601 7421100", 4.6958, -74.0854),
            PosconsumoPoint("fb5", "Punto Recolección Aceite Usado - Carulla Pepe Sierra", "Cundinamarca", "Bogotá", "Cl. 116 #18B-08", "Aceite Usado", "Lun-Sáb 8am-8pm", "+57 601 6205500", 4.6982, -74.0498),

            PosconsumoPoint("fb6", "Pilas con el Ambiente - Centro Comercial Santafé", "Antioquia", "Medellín", "Cl. 7 Sur #42-70, El Poblado", "Pilas y Baterías", "Lun-Dom 10am-8pm", "+57 604 4448899", 6.1982, -75.5782),
            PosconsumoPoint("fb7", "Punto Azul Medicamentos - Droguería Pasteur El Poblado", "Antioquia", "Medellín", "Cra. 43A #5A-113", "Medicamentos Vencidos", "Lun-Sáb 8am-8pm", "+57 604 3122000", 6.2089, -75.5691),
            PosconsumoPoint("fb8", "Ecolombia Lúmina - Éxito Poblado", "Antioquia", "Medellín", "Cl. 10 #43E-135", "Lámparas y Bombillos", "Lun-Dom 8am-9pm", "+57 604 2685000", 6.2105, -75.5708),

            PosconsumoPoint("fb9", "Punto Recolección RAEES - Chipichape", "Valle del Cauca", "Cali", "Cl. 38 Norte #6N-35", "Electrodomésticos y RAEES", "Lun-Sáb 9am-7pm", "+57 602 6592000", 3.4752, -76.5282),
            PosconsumoPoint("fb10", "Punto Azul Medicamentos - Farmacenter Jardín Plaza", "Valle del Cauca", "Cali", "Cl. 16 #98-00", "Medicamentos Vencidos", "Lun-Dom 8am-8pm", "+57 602 3324000", 3.3712, -76.5298),

            PosconsumoPoint("fb11", "Pilas con el Ambiente - Centro Comercial Cacique", "Santander", "Bucaramanga", "Transversal 93 #34-99", "Pilas y Baterías", "Lun-Dom 10am-8pm", "+57 607 6315000", 7.1082, -73.1098),
            PosconsumoPoint("fb12", "Ecolombia Recolección - Buenavista", "Atlántico", "Barranquilla", "Cl. 98 #52-115", "Lámparas y Bombillos", "Lun-Dom 10am-8pm", "+57 605 3784000", 11.0112, -74.8258)
        )

        return fallbackList.filter { pt ->
            val matchDep = (departamento == "Todos" || pt.departamento.equals(departamento, ignoreCase = true))
            val matchCat = (tipoResiduo == "Todos" || pt.tipoResiduo.contains(tipoResiduo, ignoreCase = true))
            val matchQuery = query.isBlank() || pt.nombrePunto.contains(query, ignoreCase = true) || pt.direccion.contains(query, ignoreCase = true) || pt.municipio.contains(query, ignoreCase = true)
            matchDep && matchCat && matchQuery
        }
    }
}
