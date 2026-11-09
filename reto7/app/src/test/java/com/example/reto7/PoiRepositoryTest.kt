package com.example.reto7

import com.example.reto7.data.PoiRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PoiRepositoryTest {

    private val repository = PoiRepository()

    @Test
    fun testCalculateDistanceKm_samePoint_returnsZero() {
        val distance = repository.calculateDistanceKm(4.6097, -74.0817, 4.6097, -74.0817)
        assertEquals(0.0, distance, 0.0001)
    }

    @Test
    fun testCalculateDistanceKm_bogotaToMedellin_isApprox240Km() {
        // Bogotá (4.6097, -74.0817) to Medellín (6.2442, -75.5812)
        val distance = repository.calculateDistanceKm(4.6097, -74.0817, 6.2442, -75.5812)
        assertTrue(distance in 230.0..250.0)
    }

    @Test
    fun testGetNearbyPois_filtersByRadius() {
        // Center near Monserrate Bogotá (4.6058, -74.0554)
        val pois5km = repository.getNearbyPois(4.6058, -74.0554, radiusKm = 5.0)
        val pois1km = repository.getNearbyPois(4.6058, -74.0554, radiusKm = 1.0)

        assertTrue(pois5km.size >= pois1km.size)
        assertTrue(pois1km.all { it.second <= 1.0 })
    }
}
