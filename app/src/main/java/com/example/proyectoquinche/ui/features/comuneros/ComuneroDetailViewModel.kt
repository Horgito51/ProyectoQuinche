package com.example.proyectoquinche.ui.features.comuneros

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyectoquinche.domain.enums.AppModule
import com.example.proyectoquinche.domain.model.ComuneroDetalle
import com.example.proyectoquinche.domain.repository.AuthRepository
import com.example.proyectoquinche.domain.repository.ComuneroRepository
import com.example.proyectoquinche.ui.common.appContainer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class ComuneroDetailUiState(
    val cargando: Boolean = true,
    val detalle: ComuneroDetalle? = null,
    val puedeEditar: Boolean = false
) {
    val noEncontrado: Boolean get() = !cargando && detalle == null
}

class ComuneroDetailViewModel(
    savedStateHandle: SavedStateHandle,
    comuneroRepository: ComuneroRepository,
    authRepository: AuthRepository
) : ViewModel() {

    private val comuneroId: String = requireNotNull(savedStateHandle["comuneroId"])

    /** Room vuelve a emitir tras editar o sincronizar, así la ficha siempre está al día. */
    val state: StateFlow<ComuneroDetailUiState> = combine(
        comuneroRepository.observeComunero(comuneroId),
        authRepository.session
    ) { detalle, user ->
        ComuneroDetailUiState(
            cargando = false,
            detalle = detalle,
            puedeEditar = user?.role?.puedeEditar(AppModule.COMUNEROS) == true
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ComuneroDetailUiState())

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val container = appContainer
                ComuneroDetailViewModel(createSavedStateHandle(), container.comuneroRepository, container.authRepository)
            }
        }
    }
}
