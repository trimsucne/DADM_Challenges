package com.example.reto6.data

data class Company(
    val id: Long = 0,
    val name: String,
    val url: String,
    val phone: String,
    val email: String,
    val services: String,
    val classification: String
)
