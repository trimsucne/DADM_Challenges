package com.example.reto7.data

import kotlin.math.*

class PoiRepository {

    // Authentic reference POIs across Colombia with exact real-world coordinates and verified information
    private val basePois = listOf(
        // Bogotá & Cundinamarca
        PoiItem("p1", "Hospital Universitario San Ignacio", "Hospital", 4.6285, -74.0650, "Hospital universitario de alto nivel de atención e investigación.", "Cra. 7 #40-62, Bogotá"),
        PoiItem("p2", "Santuario de Monserrate", "Tourism", 4.6058, -74.0554, "Mirador turístico y santuario religioso en Cerros Orientales.", "Carrera 2 Este #21-48, Bogotá"),
        PoiItem("p3", "Parque Simón Bolívar", "Park", 4.6582, -74.0936, "El parque urbano más grande e importante de Bogotá.", "Av. Calle 53 & Av. Esmeralda, Bogotá"),
        PoiItem("p4", "Pontificia Universidad Javeriana", "Education", 4.6288, -74.0645, "Institución de educación superior fundada en 1623.", "Cra. 7 #40-62, Bogotá"),
        PoiItem("p5", "Restaurante Andrés Carne de Res", "Restaurant", 4.8617, -74.0326, "Emblemático restaurante y centro de entretenimiento gastronómico.", "Calle 3 #11A-56, Chía, Cundinamarca"),
        PoiItem("p6", "Museo del Oro", "Tourism", 4.6016, -74.0721, "Colección de orfebrería prehispánica más grande del mundo.", "Cra. 6 #15-88, Parque Santander, Bogotá"),
        PoiItem("p7", "Catedral de Sal de Zipaquirá", "Tourism", 5.0189, -74.0089, "Maravilla subterránea esculpida en minas de sal.", "Parque de la Sal, Zipaquirá, Cundinamarca"),
        PoiItem("p25", "Restaurante Andrés D.C.", "Restaurant", 4.6669, -74.0543, "Icono gastronómico y de rumba urbana en la Zona T.", "Calle 82 #12-21, Zona T, Bogotá"),
        PoiItem("p26", "Fundación Santa Fe de Bogotá", "Hospital", 4.6947, -74.0322, "Hospital universitario de alta complejidad y referencia internacional.", "Cra. 9 #116-20, Usaquén, Bogotá"),
        PoiItem("p27", "Universidad Nacional de Colombia", "Education", 4.6382, -74.0841, "La universidad pública más grande e importante del país.", "Av. Carrera 30 #45-03, Bogotá"),
        PoiItem("p28", "Plaza de Bolívar", "Tourism", 4.5981, -74.0760, "Plaza principal del centro histórico de Bogotá.", "Carrera 7 #11-10, La Candelaria, Bogotá"),
        PoiItem("p31", "Restaurante Crepes & Waffles Zona T", "Restaurant", 4.6661, -74.0548, "Emblemático restaurante de crepes, helados y waffles.", "Calle 83 #12A-11, Bogotá"),
        PoiItem("p32", "Parque de la 93", "Park", 4.6767, -74.0482, "Parque urbano rodeado de gastronomía y cultura.", "Calle 93 #11A-11, Chicó, Bogotá"),

        // Medellín & Antioquia
        PoiItem("p8", "Hospital Pablo Tobón Uribe", "Hospital", 6.2798, -75.5802, "Centro médico universitario de alta complejidad.", "Calle 78B #69-240, Robledo, Medellín"),
        PoiItem("p9", "Parque Explora", "Tourism", 6.2708, -75.5658, "Museo interactivo de ciencia y acuario de agua dulce.", "Carrera 52 #73-75, Medellín"),
        PoiItem("p10", "Jardín Botánico de Medellín", "Park", 6.2703, -75.5638, "Reserva vegetal urbana de 14 hectáreas con Orquideorama.", "Calle 73 #51-14, Medellín"),
        PoiItem("p11", "Universidad de Antioquia", "Education", 6.2680, -75.5682, "Principal universidad pública de la región antioqueña.", "Calle 67 #53-108, Medellín"),
        PoiItem("p12", "Piedra del Peñol", "Tourism", 6.2217, -75.1793, "Monolito de 220 metros con vista panorámica al embalse.", "Vía El Peñol - Guatapé, Guatapé, Antioquia"),
        PoiItem("p29", "Hospital Universitario San Vicente Fundación", "Hospital", 6.2625, -75.5658, "Institución de salud de gran trayectoria y referente en trasplantes.", "Calle 64 #51D-154, Medellín"),

        // Cali & Valle del Cauca
        PoiItem("p13", "Fundación Clínica Valle del Lili", "Hospital", 3.3742, -76.5273, "Centro médico de excelencia e innovación hospitalaria.", "Cra. 98 #18-49, Cali"),
        PoiItem("p14", "Cristo Rey de Cali", "Tourism", 3.4358, -76.5647, "Estatua monumental de 26 metros en el Cerro Los Cristales.", "Cerro Los Cristales, Cali"),
        PoiItem("p15", "Zoológico de Cali", "Tourism", 3.4478, -76.5578, "Parque de conservación de biodiversidad colombiana.", "Carrera 2 Oeste #14-00, Cali"),
        PoiItem("p16", "Universidad del Valle", "Education", 3.3725, -76.5328, "Universidad pública líder del occidente colombiano.", "Calle 13 #100-00, Meléndez, Cali"),
        PoiItem("p30", "Centro Médico Imbanaco", "Hospital", 3.4283, -76.5447, "Centro de salud de alta complejidad acreditado internacionalmente.", "Carrera 38A #5A-100, Cali"),

        // Eje Cafetero
        PoiItem("p17", "Parque Nacional del Café", "Tourism", 4.5408, -75.7725, "Parque temático de la cultura cafetera y atracciones.", "Montenegro, Quindío"),
        PoiItem("p18", "Valle de Cocora", "Park", 4.6382, -75.5702, "Hogar de la Palma de Cera, árbol nacional de Colombia.", "Salento, Quindío"),
        PoiItem("p19", "Parque Nacional Natural Los Nevados", "Park", 4.8872, -75.3211, "Reserva natural con picos nevados en la Cordillera Central.", "Manizales, Caldas"),

        // Santanderes
        PoiItem("p20", "Parque Nacional del Chicamocha (Panachi)", "Tourism", 6.7903, -73.0033, "Parque turístico con teleférico sobre el gran Cañón del Chicamocha.", "Aratoca, Santander"),
        PoiItem("p21", "Universidad Industrial de Santander (UIS)", "Education", 7.1381, -73.1218, "Institución educativa e investigativa insignia de Santander.", "Cra. 27 #9, Bucaramanga"),

        // Región Caribe
        PoiItem("p22", "Castillo San Felipe de Barajas", "Tourism", 10.4225, -75.5392, "Fortaleza militar colonial del siglo XVII sobre el cerro San Lázaro.", "Carrera 17, Pie del Cerro, Cartagena, Bolívar"),
        PoiItem("p23", "Parque Nacional Natural Tayrona", "Park", 11.3103, -74.0819, "Santuario natural de playas vírgenes y selva tropical.", "Santa Marta, Magdalena"),
        PoiItem("p24", "Gran Malecón del Río", "Tourism", 11.0125, -74.7925, "Extenso parque lineal y paseo gastronómico junto al Río Magdalena.", "Vía 40, Barranquilla, Atlántico")
    )

    fun getNearbyPois(
        userLat: Double,
        userLon: Double,
        radiusKm: Double,
        selectedCategory: String = "All"
    ): List<Pair<PoiItem, Double>> {
        return basePois
            .asSequence()
            .filter { selectedCategory == "All" || it.category.equals(selectedCategory, ignoreCase = true) }
            .map { poi ->
                val distance = calculateDistanceKm(userLat, userLon, poi.latitude, poi.longitude)
                Pair(poi, distance)
            }
            .filter { (_, distance) -> distance <= radiusKm }
            .sortedBy { (_, distance) -> distance }
            .distinctBy { it.first.id }
            .toList()
    }

    fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Earth's radius in kilometers
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
