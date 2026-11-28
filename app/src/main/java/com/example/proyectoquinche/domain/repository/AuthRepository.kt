package com.example.proyectoquinche.domain.repository

import com.example.proyectoquinche.domain.model.SessionUser
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {

    /** Usuario autenticado o null. Se invalida si la cuenta se desactiva en el servidor. */
    val session: StateFlow<SessionUser?>

    /** true mientras exista una sesión guardada en el dispositivo (sobrevive al cierre de la app). */
    val isLoggedIn: StateFlow<Boolean>

    suspend fun login(nombreUsuario: String, clave: String): Result<SessionUser>

    fun logout()
}

/** Errores de autenticación con mensajes listos para mostrar. */
sealed class AuthException(message: String) : Exception(message) {
    class CredencialesInvalidas : AuthException("Usuario o contraseña incorrectos")
    class UsuarioInactivo : AuthException("El usuario está inactivo. Comuníquese con la directiva.")
    class RolNoSoportado(rol: String) : AuthException("El rol \"$rol\" no tiene acceso a la aplicación")
    class SinDatosLocales(detalle: String) : AuthException(detalle)
}
