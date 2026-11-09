package com.example.reto7.data

import kotlin.math.*

class PoiRepository {

    // Default reference POIs in Colombia (Bogotá / Medellín / Cali) + dynamic generator around user's GPS
    private val basePois = listOf(
        // Bogotá Landmarks
        PoiItem("p1", "Hospital Universitario San Ignacio", "Hospital", 4.6281, -74.0653, "Hospital de alto nivel de atención e investigación.", "Cra 7 #40-62, Bogotá"),
        PoiItem("p2", "Monserrate", "Tourism", 4.6058, -74.0554, "Santuario y mirador turístico sobre los Cerros Orientales.", "Carrera 2 Este #21-48, Bogotá"),
        PoiItem("p3", "Parque Metropolitano Simón Bolívar", "Park", 4.6582, -74.0936, "El parque urbano más grande e importante de Bogotá.", "Av. Calle 53 & Av. Esmeralda, Bogotá"),
        PoiItem("p4", "Pontificia Universidad Javeriana", "Education", 4.6283, -74.0648, "Institución de educación superior fundada en 1623.", "Cra. 7 #40-62, Bogotá"),
        PoiItem("p5", "Restaurante Andrés Carne de Res", "Restaurant", 4.6645, -74.0538, "Icono gastronómico y de entretenimiento de la ciudad.", "Calle 82 #12-21, Bogotá"),

        // Medellín Landmarks
        PoiItem("p6", "Hospital Pablo Tobón Uribe", "Hospital", 6.2798, -75.5802, "Centro médico de alta complejidad.", "Calle 78B #69-240, Medellín"),
        PoiItem("p7", "Parque Explora", "Tourism", 6.2708, -75.5658, "Museo interactivo de ciencia y tecnología.", "Carrera 52 #73-75, Medellín"),
        PoiItem("p8", "Jardín Botánico de Medellín", "Park", 6.2703, -75.5638, "Espacio verde para la conservación ecológica.", "Calle 73 #51-14, Medellín"),
        PoiItem("p9", "Universidad de Antioquia", "Education", 6.2680, -75.5682, "Principal universidad pública del departamento.", "Calle 67 #53-108, Medellín")
    )

    fun getNearbyPois(
        userLat: Double,
        userLon: Double,
        radiusKm: Double,
        selectedCategory: String = "All"
    ): List<Pair<PoiItem, Double>> {
        val allPois = ArrayList(basePois)

        // Dynamically add POIs nearby user's GPS if user is in a different location
        if (basePois.none { calculateDistanceKm(userLat, userLon, it.latitude, it.longitude) <= radiusKm * 2 }) {
            allPois.addAll(generateLocalPois(userLat, userLon))
        }

        return allPois
            .asSequence()
            .filter { selectedCategory == "All" || it.category.equals(selectedCategory, ignoreCase = true) }
            .map { poi ->
                val distance = calculateDistanceKm(userLat, userLon, poi.latitude, poi.longitude)
                Pair(poi, distance)
            }
            .filter { (_, distance) -> distance <= radiusKm }
            .sortedBy { (_, distance) -> distance }
            .toList()
    }

    private fun generateLocalPois(lat: Double, lon: Double): List<PoiItem> {
        return listOf(
            PoiItem("loc1", "Centro Médico Regional", "Hospital", lat + 0.008, lon + 0.007, "Atención médica de urgencias y consulta externa.", "Av. Principal #12-34"),
            PoiItem("loc2", "Mirador del Valle", "Tourism", lat - 0.012, lon + 0.010, "Punto de interés turístico panorámico.", "Vía Panorámica Km 2"),
            PoiItem("loc3", "Parque Ecológico del Sol", "Park", lat + 0.005, lon - 0.009, "Zona verde para recreación y deporte.", "Calle 10 #5-20"),
            PoiItem("loc4", "Universidad Tecnológica Local", "Education", lat - 0.007, lon - 0.006, "Campus educativo universitario.", "Carrera 15 #8-45"),
            PoiItem("loc5", "Restaurante El Gourmet", "Restaurant", lat + 0.003, lon + 0.004, "Gastronomía típica e internacional.", "Calle Real #4-12")
        )
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
