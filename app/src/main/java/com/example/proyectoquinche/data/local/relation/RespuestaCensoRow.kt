package com.example.proyectoquinche.data.local.relation

/** Respuesta del censo con su pregunta, sección y opciones elegidas (concatenadas). */
data class RespuestaCensoRow(
    val seccionId: Int,
    val seccionTitulo: String,
    val preguntaId: Int,
    val enunciado: String,
    val texto: String?,
    val numero: Double?,
    val booleano: Boolean?,
    val opciones: String?
)
