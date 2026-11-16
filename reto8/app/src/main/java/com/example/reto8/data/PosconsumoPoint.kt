package com.example.reto8.data

import com.google.gson.annotations.SerializedName

data class PosconsumoPoint(
    @SerializedName("id", alternate = ["c_d_mun", "id_caso"])
    val id: String = "",

    @SerializedName("cultivo", alternate = ["nombre_punto", "nombre"])
    val nombrePunto: String = "",

    @SerializedName("departamento", alternate = ["departamento_nom"])
    val departamento: String = "",

    @SerializedName("municipio", alternate = ["ciudad_municipio_nom", "ciudad"])
    val municipio: String = "",

    @SerializedName("grupo_de_cultivo", alternate = ["tipo_residuo", "categoria"])
    val tipoResiduo: String = "",

    @SerializedName("rea_sembrada_ha", alternate = ["direccion"])
    val direccion: String = "",

    @SerializedName("producci_n_t", alternate = ["horario"])
    val horario: String = "",

    @SerializedName("a_o", alternate = ["contacto"])
    val contacto: String = "",

    val latitud: Double = 0.0,
    val longitud: Double = 0.0
)
