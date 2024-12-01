package com.example.casino.models

import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    val id: String,
    val nombre: String,
    val idioma: String,
    val saldo: String,
    val isAdmin: Boolean = false
)