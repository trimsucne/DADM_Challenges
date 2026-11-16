package com.example.reto8.data

import com.google.gson.annotations.SerializedName

data class PosconsumoPoint(
    @SerializedName("id", alternate = ["_id", "id_punto"])
    val id: String = "",

    @SerializedName("nombre_punto", alternate = ["nombre", "punto_posconsumo", "nombre_del_punto"])
    val nombrePunto: String = "",

    @SerializedName("departamento", alternate = ["nom_dept", "depa_nombre"])
    val departamento: String = "",

    @SerializedName("municipio", alternate = ["nom_muni", "muni_nombre", "ciudad"])
    val municipio: String = "",

    @SerializedName("direccion", alternate = ["direccion_punto", "ubicacion"])
    val direccion: String = "",

    @SerializedName("tipo_residuo", alternate = ["categoria", "mecanismo_posconsumo", "programa"])
    val tipoResiduo: String = "",

    @SerializedName("horario", alternate = ["horario_atencion", "horarios"])
    val horario: String = "Lunes a Viernes 8:00 AM - 5:00 PM",

    @SerializedName("contacto", alternate = ["telefono", "responsable"])
    val contacto: String = "Línea de atención ambiental",

    @SerializedName("latitud", alternate = ["lat", "location_latitude"])
    val latitud: Double = 0.0,

    @SerializedName("longitud", alternate = ["lon", "lng", "location_longitude"])
    val longitud: Double = 0.0
)
