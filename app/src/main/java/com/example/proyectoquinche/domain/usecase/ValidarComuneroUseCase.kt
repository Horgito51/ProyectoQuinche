package com.example.proyectoquinche.domain.usecase

import com.example.proyectoquinche.domain.model.ComuneroForm
import com.example.proyectoquinche.domain.model.HogarResumen
import com.example.proyectoquinche.domain.model.Parentesco

enum class CampoComunero { NOMBRES, APELLIDOS, TELEFONO, CORREO, CODIGO, HOGAR, PARENTESCO }

/**
 * Reglas del formulario de comunero. No depende de Android para poder
 * reutilizarse y probarse fuera de la UI.
 */
class ValidarComuneroUseCase {

    private val soloLetras = Regex("^[\\p{L}][\\p{L} .'-]*$")
    private val correoValido = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val celular = Regex("^09\\d{8}$")
    private val convencional = Regex("^0[2-7]\\d{7}$")

    /**
     * @param hogar hogar elegido (para validar la regla del responsable).
     * @param personaId persona que se está editando, o null si es un registro nuevo.
     */
    operator fun invoke(
        form: ComuneroForm,
        hogar: HogarResumen?,
        personaId: String?
    ): Map<CampoComunero, String> {
        val errores = mutableMapOf<CampoComunero, String>()

        validarNombre(form.nombres, "los nombres")?.let { errores[CampoComunero.NOMBRES] = it }
        validarNombre(form.apellidos, "los apellidos")?.let { errores[CampoComunero.APELLIDOS] = it }

        val telefono = normalizarTelefono(form.telefono)
        if (telefono.isNotEmpty() && !celular.matches(telefono) && !convencional.matches(telefono)) {
            errores[CampoComunero.TELEFONO] = "Use 10 dígitos (09…) o un fijo de 9 dígitos"
        }

        val correo = form.correo.trim()
        if (correo.isNotEmpty() && !correoValido.matches(correo)) {
            errores[CampoComunero.CORREO] = "Correo no válido"
        }

        if (form.codigoComunero.isBlank()) {
            errores[CampoComunero.CODIGO] = "Código requerido"
        }

        if (form.hogarId.isNullOrBlank() || hogar == null) {
            errores[CampoComunero.HOGAR] = "Seleccione la casa"
        } else if (form.parentesco == Parentesco.RESPONSABLE && hogar.responsableId != personaId) {
            errores[CampoComunero.PARENTESCO] =
                "La casa ${hogar.numeroCasa} ya tiene como responsable a ${hogar.responsableNombre}"
        }

        return errores
    }

    private fun validarNombre(valor: String, campo: String): String? {
        val limpio = valor.trim()
        return when {
            limpio.isEmpty() -> "Ingrese $campo"
            limpio.length < 2 -> "Mínimo 2 caracteres"
            !soloLetras.matches(limpio) -> "Solo letras y espacios"
            else -> null
        }
    }

    companion object {
        fun normalizarTelefono(valor: String): String = valor.filter { it.isDigit() }
    }
}
