package com.example.reto8

import com.example.reto8.data.PosconsumoRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PosconsumoRepositoryTest {

    private val repository = PosconsumoRepository()

    @Test
    fun testGetPuntosPosconsumo_returnsResult() {
        runBlocking {
            val result = repository.getPuntosPosconsumo(
                departamento = "Todos",
                tipoResiduo = "Todos",
                query = ""
            )
            assertNotNull(result)
        }
    }

    @Test
    fun testGetPuntosPosconsumo_filtersByDepartment() {
        runBlocking {
            val resultBoyaca = repository.getPuntosPosconsumo(
                departamento = "BOYACA",
                tipoResiduo = "Todos",
                query = ""
            )
            resultBoyaca.onSuccess { list ->
                assertTrue(list.all { it.departamento.contains("BOYACA", ignoreCase = true) })
            }
        }
    }
}
