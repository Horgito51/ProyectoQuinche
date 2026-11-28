package com.example.proyectoquinche.ui.features.hogar

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyectoquinche.domain.model.FichaHogar
import com.example.proyectoquinche.domain.repository.HogarRepository
import com.example.proyectoquinche.ui.common.appContainer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HogarDetailUiState(
    val cargando: Boolean = true,
    val ficha: FichaHogar? = null,
    val seccionesAbiertas: Set<Int> = emptySet()
) {
    val noEncontrado: Boolean get() = !cargando && ficha == null
}

class HogarDetailViewModel(
    private val savedStateHandle: SavedStateHandle,
    hogarRepository: HogarRepository
) : ViewModel() {

    private val hogarId: String = requireNotNull(savedStateHandle["hogarId"])

    /** null = el usuario aún no tocó ninguna sección: se abre la primera por defecto. */
    private val abiertas: StateFlow<ArrayList<Int>?> = savedStateHandle.getStateFlow(KEY_ABIERTAS, null)

    val state: StateFlow<HogarDetailUiState> = combine(
        hogarRepository.observeFichaHogar(hogarId),
        abiertas
    ) { ficha, abiertas ->
        val secciones = ficha?.censo?.secciones.orEmpty()
        HogarDetailUiState(
            cargando = false,
            ficha = ficha,
            seccionesAbiertas = abiertas?.toSet() ?: setOfNotNull(secciones.firstOrNull()?.seccionId)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HogarDetailUiState())

    fun toggleSeccion(seccionId: Int) {
        val actuales = state.value.seccionesAbiertas
        val nuevas = if (seccionId in actuales) actuales - seccionId else actuales + seccionId
        savedStateHandle[KEY_ABIERTAS] = ArrayList(nuevas)
    }

    companion object {
        private const val KEY_ABIERTAS = "secciones_abiertas"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                HogarDetailViewModel(createSavedStateHandle(), appContainer.hogarRepository)
            }
        }
    }
}
