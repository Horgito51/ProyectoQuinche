package com.example.proyectoquinche.data.local.relation

/** Proyección usuario + rol + persona usada para autenticar y armar la sesión. */
data class CuentaUsuario(
    val usuarioId: String,
    val personaId: String,
    val nombreUsuario: String,
    val claveHash: String,
    val activo: Boolean,
    val rolNombre: String,
    val nombres: String,
    val apellidos: String
)
