package com.example.reto8.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface DatosGovApi {
    // Active Colombian Open Data web service on datos.gov.co (Evaluaciones Agropecuarias)
    @GET("resource/2pnw-mmge.json")
    suspend fun getEvaluacionesAgropecuarias(
        @Query("\$limit") limit: Int = 100,
        @Query("\$offset") offset: Int = 0,
        @Query("departamento") departamento: String? = null,
        @Query("grupo_de_cultivo") grupoCultivo: String? = null,
        @Query("\$q") searchQuery: String? = null,
        @Header("User-Agent") userAgent: String = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/128.0.0.0 Safari/537.36"
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

    // Comprehensive Geocoding dictionary for Colombian Municipalities
    private val municipioCoordinatesMap = mapOf(
        // Cundinamarca
        "UBATÉ" to Pair(5.3081, -73.8144),
        "VILLA DE SAN DIEGO DE UBATÉ" to Pair(5.3081, -73.8144),
        "GRANADA" to Pair(4.5167, -74.3500),
        "ZIPAQUIRÁ" to Pair(5.0225, -74.0042),
        "SOACHA" to Pair(4.5794, -74.2169),
        "CHÍA" to Pair(4.8631, -74.0531),
        "GIRARDOT" to Pair(4.3056, -74.8017),
        "FUSAGASUGÁ" to Pair(4.3369, -74.3639),
        "FACATATIVÁ" to Pair(4.8108, -74.3542),
        "FUNZA" to Pair(4.7161, -74.2117),
        "MOSQUERA" to Pair(4.7058, -74.2303),
        "MADRID" to Pair(4.7328, -74.2642),
        "CAJICÁ" to Pair(4.9189, -74.0278),
        "VILLETA" to Pair(5.0153, -74.4722),
        "GUADUAS" to Pair(5.0683, -74.5958),
        "TOCANCIPÁ" to Pair(4.9661, -73.9583),
        "SOPÓ" to Pair(4.9083, -73.9439),
        "BOGOTÁ" to Pair(4.6097, -74.0817),
        "BOGOTA" to Pair(4.6097, -74.0817),

        // Antioquia
        "MEDELLÍN" to Pair(6.2442, -75.5812),
        "MEDELLIN" to Pair(6.2442, -75.5812),
        "RIONEGRO" to Pair(6.1550, -75.3739),
        "ENVIGADO" to Pair(6.1758, -75.5917),
        "BELLO" to Pair(6.3369, -75.5583),
        "ITAGÜÍ" to Pair(6.1844, -75.5989),
        "ITAGUI" to Pair(6.1844, -75.5989),
        "SABANETA" to Pair(6.1511, -75.6156),
        "TURBO" to Pair(8.0928, -76.7289),
        "APARTADÓ" to Pair(7.8828, -76.6258),

        // Valle del Cauca
        "CALI" to Pair(3.4516, -76.5320),
        "SANTIAGO DE CALI" to Pair(3.4516, -76.5320),
        "PALMIRA" to Pair(3.5394, -76.3036),
        "BUENAVENTURA" to Pair(3.8803, -77.0312),
        "TULUÁ" to Pair(4.0844, -76.1956),
        "CARTAGO" to Pair(4.7464, -75.9117),
        "JAMUNDÍ" to Pair(3.2606, -76.5414),
        "YUMBO" to Pair(3.5819, -76.4958),

        // Santander
        "BUCARAMANGA" to Pair(7.1254, -73.1198),
        "FLORIDABLANCA" to Pair(7.0622, -73.0864),
        "GIRÓN" to Pair(7.0683, -73.1694),
        "PIEDECUESTA" to Pair(6.9886, -73.0494),
        "BARRANCABERMEJA" to Pair(7.0653, -73.8547),
        "SAN GIL" to Pair(6.5572, -73.1369),

        // Atlántico
        "BARRANQUILLA" to Pair(10.9685, -74.7813),
        "SOLEDAD" to Pair(10.9183, -74.7628),
        "MALAMBO" to Pair(10.8589, -74.7739),
        "SABANALARGA" to Pair(10.6308, -74.9208),

        // Boyacá
        "TUNJA" to Pair(5.5353, -73.3678),
        "DUITAMA" to Pair(5.8269, -73.0339),
        "SOGAMOSO" to Pair(5.7147, -72.9339),
        "CHIQUINQUIRÁ" to Pair(5.6186, -73.8153),
        "PAIPA" to Pair(5.7811, -73.1169),
        "VILLA DE LEYVA" to Pair(5.6381, -73.5261),
        "BUSBANZÁ" to Pair(5.8208, -72.8808),
        "BUSBANZA" to Pair(5.8208, -72.8808)
    )

    // Complete 32 Departments Geocoding Dictionary for Colombia
    private val departmentCoordinatesMap = mapOf(
        "AMAZONAS" to Pair(-4.2078, -69.9406), // Leticia
        "ANTIOQUIA" to Pair(6.2442, -75.5812), // Medellín
        "ARAUCA" to Pair(7.0847, -70.7592), // Arauca
        "ATLANTICO" to Pair(10.9685, -74.7813), // Barranquilla
        "ATLÁNTICO" to Pair(10.9685, -74.7813),
        "BOLIVAR" to Pair(10.3997, -75.5144), // Cartagena
        "BOLÍVAR" to Pair(10.3997, -75.5144),
        "BOYACA" to Pair(5.5353, -73.3678), // Tunja
        "BOYACÁ" to Pair(5.5353, -73.3678),
        "CALDAS" to Pair(5.0689, -75.5173), // Manizales
        "CAQUETA" to Pair(1.6144, -75.6064), // Florencia
        "CAQUETÁ" to Pair(1.6144, -75.6064),
        "CASANARE" to Pair(5.3378, -72.3958), // Yopal
        "CAUCA" to Pair(2.4448, -76.6147), // Popayán
        "CESAR" to Pair(10.4631, -73.2533), // Valledupar
        "CHOCO" to Pair(5.6922, -76.6583), // Quibdó
        "CHOCÓ" to Pair(5.6922, -76.6583),
        "CORDOBA" to Pair(8.7511, -75.8822), // Montería
        "CÓRDOBA" to Pair(8.7511, -75.8822),
        "CUNDINAMARCA" to Pair(5.0000, -74.1000), // Cundinamarca Center
        "BOGOTA" to Pair(4.6097, -74.0817),
        "BOGOTÁ" to Pair(4.6097, -74.0817),
        "GUAINIA" to Pair(3.8653, -67.9239), // Inírida
        "GUAINÍA" to Pair(3.8653, -67.9239),
        "GUAVIARE" to Pair(2.5728, -72.6458), // San José del Guaviare
        "HUILA" to Pair(2.9273, -75.2819), // Neiva
        "LA GUAJIRA" to Pair(11.5442, -72.9072), // Riohacha
        "GUAJIRA" to Pair(11.5442, -72.9072),
        "MAGDALENA" to Pair(11.2408, -74.1997), // Santa Marta
        "META" to Pair(4.1420, -73.6266), // Villavicencio
        "NARIÑO" to Pair(1.2136, -77.2811), // Pasto
        "NARINO" to Pair(1.2136, -77.2811),
        "NORTE DE SANTANDER" to Pair(7.8939, -72.5078), // Cúcuta
        "NORTESANTANDER" to Pair(7.8939, -72.5078),
        "PUTUMAYO" to Pair(1.1528, -76.6522), // Mocoa
        "QUINDIO" to Pair(4.5339, -75.6811), // Armenia
        "QUINDÍO" to Pair(4.5339, -75.6811),
        "RISARALDA" to Pair(4.8133, -75.6961), // Pereira
        "SAN ANDRES" to Pair(12.5847, -81.7006), // San Andrés
        "SAN ANDRÉS" to Pair(12.5847, -81.7006),
        "SANTANDER" to Pair(7.1254, -73.1198), // Bucaramanga
        "SUCRE" to Pair(9.3047, -75.3978), // Sincelejo
        "TOLIMA" to Pair(4.4389, -75.2322), // Ibagué
        "VALLE" to Pair(3.4516, -76.5320), // Cali
        "VALLE DEL CAUCA" to Pair(3.4516, -76.5320),
        "VAUPES" to Pair(1.2522, -70.2333), // Mitú
        "VAUPÉS" to Pair(1.2522, -70.2333),
        "VICHADA" to Pair(6.1889, -67.4858) // Puerto Carreño
    )

    suspend fun fetchPuntos(
        departamento: String = "Todos",
        tipoResiduo: String = "Todos",
        query: String = "",
        page: Int = 1,
        pageSize: Int = 100
    ): Result<List<PosconsumoPoint>> {
        return try {
            val depParam = if (departamento != "Todos") departamento.uppercase() else null
            val catParam = if (tipoResiduo != "Todos") tipoResiduo.uppercase() else null
            val qParam = if (query.isNotBlank()) query else null
            val offset = (page - 1) * pageSize

            val remoteResults = api.getEvaluacionesAgropecuarias(
                limit = pageSize,
                offset = offset,
                departamento = depParam,
                grupoCultivo = catParam,
                searchQuery = qParam
            )

            // Geocode using exact Municipality or Department lookup
            val pointsWithGps = remoteResults.mapIndexed { idx, pt ->
                val (lat, lon) = getMunicipioCoordinates(pt.municipio, pt.departamento, idx)
                pt.copy(
                    id = if (pt.id.isNotBlank()) pt.id + "_p${page}_$idx" else "id_p${page}_$idx",
                    latitud = lat,
                    longitud = lon
                )
            }

            Result.success(pointsWithGps)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    private fun getMunicipioCoordinates(muni: String, dep: String, index: Int): Pair<Double, Double> {
        val muniClean = muni.trim().uppercase()
        val exactMatch = municipioCoordinatesMap[muniClean]
        if (exactMatch != null) {
            val scatterLat = (index % 5 - 2) * 0.005
            val scatterLon = (index % 7 - 3) * 0.005
            return Pair(exactMatch.first + scatterLat, exactMatch.second + scatterLon)
        }

        // Fallback to true department capital coordinates across all 32 Departments of Colombia
        val depClean = dep.trim().uppercase()
        val depMatch = departmentCoordinatesMap[depClean]
        if (depMatch != null) {
            val scatterLat = (index % 5 - 2) * 0.012
            val scatterLon = (index % 7 - 3) * 0.012
            return Pair(depMatch.first + scatterLat, depMatch.second + scatterLon)
        }

        // Default Colombia center scatter
        val scatterLat = (index % 5 - 2) * 0.02
        val scatterLon = (index % 7 - 3) * 0.02
        return Pair(4.5709 + scatterLat, -74.2973 + scatterLon)
    }
}
