package com.example.reto6

import com.example.reto6.data.Company
import org.junit.Assert.assertEquals
import org.junit.Test

class CompanyDatabaseTest {

    @Test
    fun testCompanyEntityModel() {
        val company = Company(
            id = 1,
            name = "Test Software Corp",
            url = "https://test.corp",
            phone = "123456",
            email = "info@test.corp",
            services = "Custom Apps",
            classification = "Consultoría"
        )

        assertEquals("Test Software Corp", company.name)
        assertEquals("Consultoría", company.classification)
        assertEquals("Custom Apps", company.services)
    }
}
