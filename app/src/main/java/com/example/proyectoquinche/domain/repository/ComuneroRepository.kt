package com.example.proyectoquinche.domain.repository

import com.example.proyectoquinche.domain.model.ComunidadResumen
import com.example.proyectoquinche.domain.model.ComuneroDetalle
import com.example.proyectoquinche.domain.model.ComuneroForm
import com.example.proyectoquinche.domain.model.ComuneroResumen
import com.example.proyectoquinche.domain.usecase.CampoComunero
import kotlinx.coroutines.flow.Flow

interface ComuneroRepository {

    fun observeComuneros(): Flow<List<ComuneroResumen>>

    fun observeComunero(comuneroId: String): Flow<ComuneroDetalle?>

    fun observeResumenComunidad(): Flow<ComunidadResumen>

    /** Siguiente código libre con el formato SJQ-000. */
    suspend fun siguienteCodigo(): String

    /**
     * Valida y guarda en Room (estado PENDING) y agenda la sincronización.
     * Devuelve el comunero_id guardado o los errores por campo.
     */
    suspend fun guardar(form: ComuneroForm): GuardarComuneroResult
}

sealed interface GuardarComuneroResult {
    data class Guardado(val comuneroId: String) : GuardarComuneroResult
    data class Invalido(val errores: Map<CampoComunero, String>) : GuardarComuneroResult
}
