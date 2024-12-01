package com.example.casino.models
import kotlinx.serialization.*


@Serializable
data class UsuarioLoginData(
    val userName : String,
    val password : String,
    val idUsuario: String,
    val isAdmin: Boolean = false
) {
}