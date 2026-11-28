package com.example.proyectoquinche.domain.model

import java.time.LocalDate

/** Hogar con su responsable; se usa en el selector de casa y en la ficha familiar. */
data class HogarResumen(
    val hogarId: String,
    val numeroCasa: String,
    val sector: String,
    val direccion: String,
    val referencia: String?,
    val responsableId: String,
    val responsableNombres: String,
    val responsableApellidos: String
) {
    val responsableNombre: String get() = "$responsableNombres $responsableApellidos".trim()
}

/** Fila de la pantalla Hogares y censos, calculada íntegramente desde Room. */
data class HogarListado(
    val hogarId: String,
    val numeroCasa: String,
    val sector: String,
    val direccion: String,
    val responsableNombres: String,
    val responsableApellidos: String,
    val integrantes: Int,
    val tieneCenso: Boolean
) {
    val responsableNombre: String get() = "$responsableNombres $responsableApellidos".trim()
}

data class IntegranteHogar(
    val personaId: String,
    val nombres: String,
    val apellidos: String,
    val parentesco: String?
)

/** Ficha familiar y domiciliaria: hogar, integrantes y censo (solo lectura). */
data class FichaHogar(
    val hogar: HogarResumen,
    val integrantes: List<IntegranteHogar>,
    val censo: CensoHogar?
)

data class CensoHogar(
    val fichaId: String,
    val fechaRegistro: LocalDate?,
    val consentimiento: Boolean,
    val secciones: List<CensoSeccion>
)

data class CensoSeccion(
    val seccionId: Int,
    val titulo: String,
    val respuestas: List<CensoRespuesta>
)

data class CensoRespuesta(
    val preguntaId: Int,
    val enunciado: String,
    val valor: String
)
