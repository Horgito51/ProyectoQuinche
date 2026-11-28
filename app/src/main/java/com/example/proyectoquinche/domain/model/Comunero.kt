package com.example.proyectoquinche.domain.model

import com.example.proyectoquinche.domain.enums.EstadoComunero
import com.example.proyectoquinche.domain.enums.SyncStatus

/** Fila del listado de comuneros (comunero + persona + hogar al que pertenece). */
data class ComuneroResumen(
    val comuneroId: String,
    val personaId: String,
    val nombres: String,
    val apellidos: String,
    val codigoComunero: String?,
    val estado: EstadoComunero,
    val numeroCasa: String?,
    val sector: String?
) {
    val nombreCompleto: String get() = "$nombres $apellidos".trim()
}

/** Información completa para la ficha del comunero y su edición. */
data class ComuneroDetalle(
    val comuneroId: String,
    val personaId: String,
    val nombres: String,
    val apellidos: String,
    val telefono: String?,
    val correo: String?,
    val codigoComunero: String?,
    val estado: EstadoComunero,
    val hogarId: String?,
    val numeroCasa: String?,
    val sector: String?,
    val direccion: String?,
    val parentesco: String?,
    val syncStatus: SyncStatus
) {
    val nombreCompleto: String get() = "$nombres $apellidos".trim()
}

/** Datos capturados en el formulario de registro / edición. */
data class ComuneroForm(
    val comuneroId: String?,
    val nombres: String,
    val apellidos: String,
    val telefono: String,
    val correo: String,
    val codigoComunero: String,
    val hogarId: String?,
    val parentesco: String?,
    val estado: EstadoComunero
)

/** Totales del padrón para el panel principal. */
data class ComunidadResumen(
    val total: Int,
    val activos: Int,
    val inactivos: Int,
    val hogares: Int
)
