package com.example.proyectoquinche.ui.features.auth

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyectoquinche.domain.model.SessionUser
import com.example.proyectoquinche.domain.repository.AuthRepository
import com.example.proyectoquinche.sync.SyncManager
import com.example.proyectoquinche.ui.common.UiState
import com.example.proyectoquinche.ui.common.appContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginFieldErrors(
    val usuario: Boolean = false,
    val clave: Boolean = false
)

class LoginViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
    private val syncManager: SyncManager
) : ViewModel() {

    /** El usuario escrito sobrevive a rotaciones y a la muerte del proceso; la clave no se guarda. */
    val usuario: StateFlow<String> = savedStateHandle.getStateFlow(KEY_USUARIO, "")

    private val _state = MutableStateFlow<UiState<SessionUser>>(UiState.Idle)
    val state: StateFlow<UiState<SessionUser>> = _state.asStateFlow()

    private val _fieldErrors = MutableStateFlow(LoginFieldErrors())
    val fieldErrors: StateFlow<LoginFieldErrors> = _fieldErrors.asStateFlow()

    fun onUsuarioChanged(value: String) {
        savedStateHandle[KEY_USUARIO] = value
        if (_fieldErrors.value.usuario) _fieldErrors.value = _fieldErrors.value.copy(usuario = false)
    }

    fun onClaveChanged() {
        if (_fieldErrors.value.clave) _fieldErrors.value = _fieldErrors.value.copy(clave = false)
    }

    fun login(clave: String) {
        if (_state.value is UiState.Loading) return
        val usuario = usuario.value.trim()
        val errores = LoginFieldErrors(
            usuario = usuario.isEmpty(),
            clave = clave.isEmpty()
        )
        _fieldErrors.value = errores
        if (errores.usuario || errores.clave) return

        _state.value = UiState.Loading
        viewModelScope.launch {
            _state.value = authRepository.login(usuario, clave).fold(
                onSuccess = {
                    syncManager.requestSync()
                    UiState.Success(it)
                },
                onFailure = { UiState.Error(it.message ?: "No se pudo iniciar sesión", it) }
            )
        }
    }

    companion object {
        private const val KEY_USUARIO = "usuario"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val container = appContainer
                LoginViewModel(createSavedStateHandle(), container.authRepository, container.syncManager)
            }
        }
    }
}
