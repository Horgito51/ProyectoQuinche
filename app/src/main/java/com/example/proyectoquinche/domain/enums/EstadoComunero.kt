package com.example.proyectoquinche.domain.enums

/** Valores de comunero.estado en el esquema central. */
enum class EstadoComunero {
    ACTIVO,
    INACTIVO;

    companion object {
        fun from(value: String?): EstadoComunero =
            entries.firstOrNull { it.name.equals(value?.trim(), ignoreCase = true) } ?: INACTIVO
    }
}
