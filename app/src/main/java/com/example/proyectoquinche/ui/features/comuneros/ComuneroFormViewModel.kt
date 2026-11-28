package com.example.proyectoquinche.ui.features.comuneros

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyectoquinche.domain.enums.EstadoComunero
import com.example.proyectoquinche.domain.model.ComuneroForm
import com.example.proyectoquinche.domain.model.HogarResumen
import com.example.proyectoquinche.domain.repository.ComuneroRepository
import com.example.proyectoquinche.domain.repository.GuardarComuneroResult
import com.example.proyectoquinche.domain.repository.HogarRepository
import com.example.proyectoquinche.domain.usecase.CampoComunero
import com.example.proyectoquinche.ui.common.appContainer
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface ComuneroFormEvent {
    data object Guardado : ComuneroFormEvent
    data class Fallo(val mensaje: String) : ComuneroFormEvent
}

class ComuneroFormViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val comuneroRepository: ComuneroRepository,
    hogarRepository: HogarRepository
) : ViewModel() {

    /** null = registro nuevo. */
    val comuneroId: String? = savedStateHandle["comuneroId"]
    val esEdicion: Boolean get() = comuneroId != null

    // Cada campo vive en SavedStateHandle: sobrevive a rotación y a la muerte del proceso.
    val nombres: StateFlow<String> = savedStateHandle.getStateFlow(KEY_NOMBRES, "")
    val apellidos: StateFlow<String> = savedStateHandle.getStateFlow(KEY_APELLIDOS, "")
    val telefono: StateFlow<String> = savedStateHandle.getStateFlow(KEY_TELEFONO, "")
    val correo: StateFlow<String> = savedStateHandle.getStateFlow(KEY_CORREO, "")
    val codigo: StateFlow<String> = savedStateHandle.getStateFlow(KEY_CODIGO, "")
    val hogarId: StateFlow<String?> = savedStateHandle.getStateFlow(KEY_HOGAR, null)
    val parentesco: StateFlow<String?> = savedStateHandle.getStateFlow(KEY_PARENTESCO, null)
    val estado: StateFlow<EstadoComunero> = savedStateHandle.getStateFlow(KEY_ESTADO, EstadoComunero.ACTIVO)

    val hogares: StateFlow<List<HogarResumen>> = hogarRepository.observeHogares()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _errores = MutableStateFlow<Map<CampoComunero, String>>(emptyMap())
    val errores: StateFlow<Map<CampoComunero, String>> = _errores.asStateFlow()

    private val _guardando = MutableStateFlow(false)
    val guardando: StateFlow<Boolean> = _guardando.asStateFlow()

    private val _events = Channel<ComuneroFormEvent>(Channel.BUFFERED)
    val events: Flow<ComuneroFormEvent> = _events.receiveAsFlow()

    init {
        if (savedStateHandle.get<Boolean>(KEY_CARGADO) != true) cargarInicial()
    }

    private fun cargarInicial() = viewModelScope.launch {
        val id = comuneroId
        if (id == null) {
            savedStateHandle[KEY_CODIGO] = comuneroRepository.siguienteCodigo()
        } else {
            comuneroRepository.observeComunero(id).firstOrNull()?.let { detalle ->
                savedStateHandle[KEY_NOMBRES] = detalle.nombres
                savedStateHandle[KEY_APELLIDOS] = detalle.apellidos
                savedStateHandle[KEY_TELEFONO] = detalle.telefono.orEmpty()
                savedStateHandle[KEY_CORREO] = detalle.correo.orEmpty()
                savedStateHandle[KEY_CODIGO] = detalle.codigoComunero.orEmpty()
                savedStateHandle[KEY_HOGAR] = detalle.hogarId
                savedStateHandle[KEY_PARENTESCO] = detalle.parentesco
                savedStateHandle[KEY_ESTADO] = detalle.estado
            }
        }
        savedStateHandle[KEY_CARGADO] = true
    }

    fun onNombresChanged(value: String) = update(KEY_NOMBRES, value, CampoComunero.NOMBRES)
    fun onApellidosChanged(value: String) = update(KEY_APELLIDOS, value, CampoComunero.APELLIDOS)
    fun onTelefonoChanged(value: String) = update(KEY_TELEFONO, value, CampoComunero.TELEFONO)
    fun onCorreoChanged(value: String) = update(KEY_CORREO, value, CampoComunero.CORREO)

    fun onHogarSelected(hogarId: String) {
        savedStateHandle[KEY_HOGAR] = hogarId
        limpiarError(CampoComunero.HOGAR)
        limpiarError(CampoComunero.PARENTESCO)
    }

    fun onParentescoSelected(value: String) {
        savedStateHandle[KEY_PARENTESCO] = value
        limpiarError(CampoComunero.PARENTESCO)
    }

    fun onEstadoSelected(value: EstadoComunero) {
        savedStateHandle[KEY_ESTADO] = value
    }

    fun guardar() {
        if (_guardando.value) return
        _guardando.value = true
        viewModelScope.launch {
            val form = ComuneroForm(
                comuneroId = comuneroId,
                nombres = nombres.value,
                apellidos = apellidos.value,
                telefono = telefono.value,
                correo = correo.value,
                codigoComunero = codigo.value,
                hogarId = hogarId.value,
                parentesco = parentesco.value,
                estado = estado.value
            )
            runCatching { comuneroRepository.guardar(form) }
                .onSuccess { result ->
                    when (result) {
                        is GuardarComuneroResult.Guardado -> {
                            _errores.value = emptyMap()
                            _events.send(ComuneroFormEvent.Guardado)
                        }
                        is GuardarComuneroResult.Invalido -> _errores.value = result.errores
                    }
                }
                .onFailure { _events.send(ComuneroFormEvent.Fallo(it.message ?: it.javaClass.simpleName)) }
            _guardando.value = false
        }
    }

    private fun update(key: String, value: String, campo: CampoComunero) {
        if (savedStateHandle.get<String>(key) == value) return
        savedStateHandle[key] = value
        limpiarError(campo)
    }

    private fun limpiarError(campo: CampoComunero) {
        if (campo in _errores.value) _errores.value = _errores.value - campo
    }

    companion object {
        private const val KEY_NOMBRES = "form_nombres"
        private const val KEY_APELLIDOS = "form_apellidos"
        private const val KEY_TELEFONO = "form_telefono"
        private const val KEY_CORREO = "form_correo"
        private const val KEY_CODIGO = "form_codigo"
        private const val KEY_HOGAR = "form_hogar"
        private const val KEY_PARENTESCO = "form_parentesco"
        private const val KEY_ESTADO = "form_estado"
        private const val KEY_CARGADO = "form_cargado"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val container = appContainer
                ComuneroFormViewModel(createSavedStateHandle(), container.comuneroRepository, container.hogarRepository)
            }
        }
    }
}
