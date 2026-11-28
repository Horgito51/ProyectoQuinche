package com.example.proyectoquinche.ui.features.comuneros

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyectoquinche.domain.enums.AppModule
import com.example.proyectoquinche.domain.enums.EstadoComunero
import com.example.proyectoquinche.domain.model.ComuneroResumen
import com.example.proyectoquinche.domain.repository.AuthRepository
import com.example.proyectoquinche.domain.repository.ComuneroRepository
import com.example.proyectoquinche.ui.common.appContainer
import com.example.proyectoquinche.util.TextNormalizer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

enum class FiltroEstado { TODOS, ACTIVOS, INACTIVOS }

data class ComunerosUiState(
    val cargando: Boolean = true,
    val comuneros: List<ComuneroResumen> = emptyList(),
    val total: Int = 0,
    val activos: Int = 0,
    val inactivos: Int = 0,
    val filtro: FiltroEstado = FiltroEstado.TODOS,
    val puedeEditar: Boolean = false
)

class ComunerosViewModel(
    private val savedStateHandle: SavedStateHandle,
    comuneroRepository: ComuneroRepository,
    authRepository: AuthRepository
) : ViewModel() {

    /** Búsqueda y filtro sobreviven a la rotación y a la muerte del proceso. */
    val busqueda: StateFlow<String> = savedStateHandle.getStateFlow(KEY_BUSQUEDA, "")
    private val filtro: StateFlow<FiltroEstado> = savedStateHandle.getStateFlow(KEY_FILTRO, FiltroEstado.TODOS)

    val state: StateFlow<ComunerosUiState> = combine(
        comuneroRepository.observeComuneros(),
        busqueda,
        filtro,
        authRepository.session
    ) { todos, busqueda, filtro, user ->
        val visibles = todos.filter { comunero ->
            val coincideEstado = when (filtro) {
                FiltroEstado.TODOS -> true
                FiltroEstado.ACTIVOS -> comunero.estado == EstadoComunero.ACTIVO
                FiltroEstado.INACTIVOS -> comunero.estado == EstadoComunero.INACTIVO
            }
            coincideEstado && TextNormalizer.matches(
                busqueda,
                comunero.nombreCompleto,
                comunero.codigoComunero,
                comunero.numeroCasa?.let { "casa $it" },
                comunero.sector
            )
        }
        val activos = todos.count { it.estado == EstadoComunero.ACTIVO }
        ComunerosUiState(
            cargando = false,
            comuneros = visibles,
            total = todos.size,
            activos = activos,
            inactivos = todos.size - activos,
            filtro = filtro,
            puedeEditar = user?.role?.puedeEditar(AppModule.COMUNEROS) == true
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ComunerosUiState())

    fun onBusquedaChanged(value: String) {
        savedStateHandle[KEY_BUSQUEDA] = value
    }

    fun onFiltroChanged(value: FiltroEstado) {
        savedStateHandle[KEY_FILTRO] = value
    }

    companion object {
        private const val KEY_BUSQUEDA = "busqueda"
        private const val KEY_FILTRO = "filtro"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val container = appContainer
                ComunerosViewModel(createSavedStateHandle(), container.comuneroRepository, container.authRepository)
            }
        }
    }
}
