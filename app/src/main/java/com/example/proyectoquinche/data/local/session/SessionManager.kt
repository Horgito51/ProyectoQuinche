package com.example.proyectoquinche.data.local.session

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Guarda el usuario_id de la sesión activa; persiste aunque se cierre la app. */
class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private val _usuarioId = MutableStateFlow(prefs.getString(KEY_USUARIO_ID, null))
    val usuarioId: StateFlow<String?> = _usuarioId.asStateFlow()

    fun save(usuarioId: String) {
        prefs.edit().putString(KEY_USUARIO_ID, usuarioId).apply()
        _usuarioId.value = usuarioId
    }

    fun clear() {
        prefs.edit().clear().apply()
        _usuarioId.value = null
    }

    private companion object {
        const val PREFS = "session_prefs"
        const val KEY_USUARIO_ID = "usuario_id"
    }
}
