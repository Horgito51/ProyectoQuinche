package com.example.proyectoquinche.data.repository

import com.example.proyectoquinche.data.local.dao.UsuarioDao
import com.example.proyectoquinche.data.local.relation.CuentaUsuario
import com.example.proyectoquinche.data.local.session.SessionManager
import com.example.proyectoquinche.domain.enums.UserRole
import com.example.proyectoquinche.domain.model.SessionUser
import com.example.proyectoquinche.domain.repository.AuthException
import com.example.proyectoquinche.domain.repository.AuthRepository
import com.example.proyectoquinche.util.NetworkMonitor
import com.example.proyectoquinche.util.PasswordVerifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

/**
 * Autenticación Offline-first: las credenciales se validan contra la copia local
 * de usuario (Room). Solo se consulta Supabase si el usuario aún no existe en el
 * dispositivo o su clave pudo haber cambiado.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AuthRepositoryImpl(
    private val usuarioDao: UsuarioDao,
    private val sessionManager: SessionManager,
    private val networkMonitor: NetworkMonitor,
    private val remotoConfigurado: () -> Boolean,
    private val descargarUsuarios: suspend () -> Unit,
    scope: CoroutineScope
) : AuthRepository {

    override val isLoggedIn: StateFlow<Boolean> = sessionManager.usuarioId
        .map { it != null }
        .stateIn(scope, SharingStarted.Eagerly, sessionManager.usuarioId.value != null)

    override val session: StateFlow<SessionUser?> = sessionManager.usuarioId
        .flatMapLatest { usuarioId ->
            if (usuarioId == null) {
                flowOf(null)
            } else {
                usuarioDao.observeCuenta(usuarioId).map { cuenta ->
                    val user = cuenta?.takeIf { it.activo }?.toSessionUser()
                    // Cuenta eliminada, desactivada o con rol inválido: se cierra la sesión.
                    if (user == null) sessionManager.clear()
                    user
                }
            }
        }
        .stateIn(scope, SharingStarted.Eagerly, null)

    override suspend fun login(nombreUsuario: String, clave: String): Result<SessionUser> = runCatching {
        val usuario = nombreUsuario.trim()
        var cuenta = usuarioDao.getCuenta(usuario)
        var errorDescarga: Throwable? = null

        if ((cuenta == null || !verificar(clave, cuenta)) && puedeDescargar()) {
            errorDescarga = runCatching { descargarUsuarios() }.exceptionOrNull()
            cuenta = usuarioDao.getCuenta(usuario)
        }

        if (cuenta == null) {
            if (usuarioDao.count() == 0) throw AuthException.SinDatosLocales(mensajeSinDatos(errorDescarga))
            throw AuthException.CredencialesInvalidas()
        }
        if (!verificar(clave, cuenta)) throw AuthException.CredencialesInvalidas()
        if (!cuenta.activo) throw AuthException.UsuarioInactivo()

        val user = cuenta.toSessionUser() ?: throw AuthException.RolNoSoportado(cuenta.rolNombre)
        sessionManager.save(user.usuarioId)
        user
    }

    override fun logout() = sessionManager.clear()

    private fun puedeDescargar() = remotoConfigurado() && networkMonitor.isCurrentlyOnline()

    private suspend fun verificar(clave: String, cuenta: CuentaUsuario) =
        withContext(Dispatchers.Default) { PasswordVerifier.verify(clave, cuenta.claveHash) }

    private fun mensajeSinDatos(errorDescarga: Throwable?): String = when {
        !remotoConfigurado() ->
            "No hay usuarios en este dispositivo y la conexión con Supabase no está configurada."
        !networkMonitor.isCurrentlyOnline() ->
            "Primer ingreso: conéctese a Internet para descargar los datos de la comuna."
        errorDescarga != null ->
            "No se pudo descargar los usuarios del servidor. Intente nuevamente."
        else -> "No existen usuarios registrados en el servidor."
    }

    private fun CuentaUsuario.toSessionUser(): SessionUser? {
        val role = UserRole.fromNombreRol(rolNombre) ?: return null
        return SessionUser(
            usuarioId = usuarioId,
            personaId = personaId,
            nombreUsuario = nombreUsuario,
            nombres = nombres,
            apellidos = apellidos,
            role = role
        )
    }
}
