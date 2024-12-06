package com.example.casino.models

import kotlinx.serialization.Serializable

@Serializable
data class Partido(
    val id: Int,
    val equipo1: String,
    val equipo2: String,
    var momio1: Float,
    var momio2: Float,
    var momio3: Float
)