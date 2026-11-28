package com.example.proyectoquinche.domain.enums

import java.text.Normalizer

/**
 * Roles de la directiva de la Comuna San José del Quinche.
 *
 * [modulos] define qué módulos ve cada rol en el panel y [modulosEditables]
 * en cuáles puede registrar o modificar información.
 */
enum class UserRole(
    val etiqueta: String,
    val modulos: List<AppModule>,
    val modulosEditables: Set<AppModule>
) {
    PRESIDENTE(
        etiqueta = "Presidente",
        modulos = listOf(AppModule.REPORTES),
        modulosEditables = emptySet()
    ),
    SECRETARIA(
        etiqueta = "Secretaria",
        modulos = listOf(AppModule.COMUNEROS, AppModule.ASISTENCIAS, AppModule.REPORTES),
        modulosEditables = setOf(AppModule.COMUNEROS, AppModule.ASISTENCIAS)
    ),
    TESORERA(
        etiqueta = "Tesorera",
        modulos = listOf(AppModule.MULTAS, AppModule.FINANZAS, AppModule.REPORTES),
        modulosEditables = setOf(AppModule.MULTAS, AppModule.FINANZAS)
    );

    fun puedeVer(modulo: AppModule): Boolean = modulo in modulos

    fun puedeEditar(modulo: AppModule): Boolean = modulo in modulosEditables

    companion object {
        /** Traduce rol.nombre ("Presidente", "SECRETARIA", "Tesorero/a"...) al enum. */
        fun fromNombreRol(nombre: String?): UserRole? {
            val normalizado = Normalizer.normalize(nombre.orEmpty(), Normalizer.Form.NFD)
                .replace(Regex("\\p{Mn}+"), "")
                .uppercase()
            return when {
                normalizado.startsWith("PRESIDENT") -> PRESIDENTE
                normalizado.startsWith("SECRETARI") -> SECRETARIA
                normalizado.startsWith("TESORER") -> TESORERA
                else -> null
            }
        }
    }
}
