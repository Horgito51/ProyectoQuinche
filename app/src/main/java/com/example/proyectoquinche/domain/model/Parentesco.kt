package com.example.proyectoquinche.domain.model

/** Valores de integrante_hogar.parentesco usados por el formulario del censo. */
object Parentesco {
    const val RESPONSABLE = "Responsable"

    val opciones: List<String> = listOf(
        RESPONSABLE,
        "Esposo/a o pareja",
        "Hijo/a",
        "Papá o mamá",
        "Otro familiar"
    )
}
