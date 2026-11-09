package com.example.reto7.data

data class PoiItem(
    val id: String,
    val name: String,
    val category: String, // "Hospital", "Tourism", "Park", "Education", "Restaurant"
    val latitude: Double,
    val longitude: Double,
    val description: String,
    val address: String
)
