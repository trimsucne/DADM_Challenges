package com.example.reto8

import com.example.reto8.data.PosconsumoRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PosconsumoRepositoryTest {

    private val repository = PosconsumoRepository()

    @Test
    fun testGetPuntosPosconsumo_returnsPuntosList() = runBlocking {
        val list = repository.getPuntosPosconsumo(
            departamento = "Todos",
            tipoResiduo = "Todos",
            query = ""
        )
        assertNotNull(list)
        assertTrue(list.isNotEmpty())
    }

    @Test
    fun testGetPuntosPosconsumo_filtersByDepartment() = runBlocking {
        val listAntioquia = repository.getPuntosPosconsumo(
            departamento = "Antioquia",
            tipoResiduo = "Todos",
            query = ""
        )
        assertTrue(listAntioquia.all { it.departamento.equals("Antioquia", ignoreCase = true) })
    }
}
