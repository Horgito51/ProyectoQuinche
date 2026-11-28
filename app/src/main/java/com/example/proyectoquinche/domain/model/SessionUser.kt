package com.example.proyectoquinche.domain.model

import com.example.proyectoquinche.domain.enums.UserRole

/** Usuario autenticado en el dispositivo (sin datos sensibles como la clave). */
data class SessionUser(
    val usuarioId: String,
    val personaId: String,
    val nombreUsuario: String,
    val nombres: String,
    val apellidos: String,
    val role: UserRole
) {
    val primerNombre: String get() = nombres.trim().substringBefore(' ')
    val nombreCompleto: String get() = "$nombres $apellidos".trim()
}
