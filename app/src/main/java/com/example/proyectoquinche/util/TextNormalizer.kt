package com.example.proyectoquinche.util

import java.text.Normalizer

/** Normaliza texto para búsquedas: sin tildes, en minúsculas y con espacios simples. */
object TextNormalizer {

    private val marks = Regex("\\p{Mn}+")
    private val spaces = Regex("\\s+")

    fun normalize(value: String): String =
        Normalizer.normalize(value, Normalizer.Form.NFD)
            .replace(marks, "")
            .lowercase()
            .replace(spaces, " ")
            .trim()

    /** true si cada palabra de [query] aparece en alguno de los [campos]. */
    fun matches(query: String, vararg campos: String?): Boolean {
        val tokens = normalize(query).split(' ').filter { it.isNotEmpty() }
        if (tokens.isEmpty()) return true
        val haystack = normalize(campos.filterNotNull().joinToString(" "))
        return tokens.all { haystack.contains(it) }
    }
}
