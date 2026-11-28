package com.example.proyectoquinche.ui.features.hogar

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyectoquinche.domain.model.HogarListado
import com.example.proyectoquinche.domain.repository.HogarRepository
import com.example.proyectoquinche.ui.common.appContainer
import com.example.proyectoquinche.util.TextNormalizer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

enum class FiltroCenso { TODOS, CON_CENSO, SIN_CENSO }

data class HogaresUiState(
    val hogares: List<HogarListado> = emptyList(),
    val total: Int = 0,
    val conCenso: Int = 0,
    val sinCenso: Int = 0,
    val filtro: FiltroCenso = FiltroCenso.TODOS
)

class HogaresViewModel(
    private val savedStateHandle: SavedStateHandle,
    repository: HogarRepository
) : ViewModel() {
    val busqueda: StateFlow<String> = savedStateHandle.getStateFlow("busqueda_hogar", "")
    private val filtro: StateFlow<FiltroCenso> = savedStateHandle.getStateFlow("filtro_censo", FiltroCenso.TODOS)

    val state: StateFlow<HogaresUiState> = combine(repository.observeListadoHogares(), busqueda, filtro) { todos, texto, filtro ->
        val visibles = todos.filter { hogar ->
            val pasaFiltro = when (filtro) {
                FiltroCenso.TODOS -> true
                FiltroCenso.CON_CENSO -> hogar.tieneCenso
                FiltroCenso.SIN_CENSO -> !hogar.tieneCenso
            }
            pasaFiltro && TextNormalizer.matches(texto, hogar.numeroCasa, hogar.sector, hogar.direccion, hogar.responsableNombre)
        }
        val conCenso = todos.count { it.tieneCenso }
        HogaresUiState(visibles, todos.size, conCenso, todos.size - conCenso, filtro)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HogaresUiState())

    fun onBusquedaChanged(value: String) { savedStateHandle["busqueda_hogar"] = value }
    fun onFiltroChanged(value: FiltroCenso) { savedStateHandle["filtro_censo"] = value }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { HogaresViewModel(createSavedStateHandle(), appContainer.hogarRepository) }
        }
    }
}
