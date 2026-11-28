package com.example.proyectoquinche.util

import org.mindrot.jbcrypt.BCrypt
import java.security.MessageDigest

/**
 * Verifica usuario.clave_hash en el dispositivo. Acepta los formatos que se
 * pueden generar en Supabase:
 * - bcrypt de pgcrypto: crypt('clave', gen_salt('bf'))  → "$2a$..."
 * - SHA-256 hexadecimal: encode(digest('clave', 'sha256'), 'hex')
 */
object PasswordVerifier {

    private val sha256Hex = Regex("^[0-9a-fA-F]{64}$")

    fun verify(clave: String, claveHash: String): Boolean {
        val hash = claveHash.trim()
        return when {
            hash.startsWith("$2") -> verifyBcrypt(clave, hash)
            sha256Hex.matches(hash) -> MessageDigest.isEqual(
                sha256(clave).toByteArray(),
                hash.lowercase().toByteArray()
            )
            else -> false
        }
    }

    private fun verifyBcrypt(clave: String, hash: String): Boolean = try {
        // jBCrypt solo reconoce la revisión 2a; 2b/2y usan el mismo algoritmo.
        BCrypt.checkpw(clave, hash.replaceFirst(Regex("^\\$2[by]\\$"), "\\$2a\\$"))
    } catch (e: IllegalArgumentException) {
        false
    }

    private fun sha256(value: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(value.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
}
