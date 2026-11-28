package com.example.proyectoquinche.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class RemoteException(val httpCode: Int, message: String) : IOException(message)

/**
 * Cliente mínimo de la API REST de Supabase (PostgREST): /rest/v1/{tabla}.
 * Lo comparten todos los SyncHandler de la app.
 */
class SupabaseClient(private val config: SupabaseConfig) {

    val isConfigured: Boolean get() = config.isConfigured

    /**
     * Descarga todas las filas de [table] paginando de [pageSize] en [pageSize]
     * (Supabase limita cada respuesta a 1000 filas). [orderBy] garantiza un
     * paginado estable.
     */
    suspend fun selectAll(
        table: String,
        orderBy: String,
        columns: String = "*",
        pageSize: Int = 1000
    ): List<JSONObject> {
        val rows = mutableListOf<JSONObject>()
        var offset = 0
        while (true) {
            val body = request(
                method = "GET",
                pathAndQuery = "$table?select=$columns&order=$orderBy&limit=$pageSize&offset=$offset"
            )
            val page = JSONArray(body)
            for (i in 0 until page.length()) rows += page.getJSONObject(i)
            if (page.length() < pageSize) break
            offset += pageSize
        }
        return rows
    }

    /** Inserta o actualiza filas usando la clave indicada en [onConflict]. */
    suspend fun upsert(table: String, rows: List<JSONObject>, onConflict: String) {
        if (rows.isEmpty()) return
        request(
            method = "POST",
            pathAndQuery = "$table?on_conflict=$onConflict",
            body = JSONArray(rows).toString(),
            prefer = "resolution=merge-duplicates,return=minimal"
        )
    }

    /** Elimina filas que cumplan el filtro PostgREST, p. ej. "persona_id=eq.123". */
    suspend fun delete(table: String, filter: String) {
        request(method = "DELETE", pathAndQuery = "$table?$filter", prefer = "return=minimal")
    }

    private suspend fun request(
        method: String,
        pathAndQuery: String,
        body: String? = null,
        prefer: String? = null
    ): String = withContext(Dispatchers.IO) {
        check(config.isConfigured) { "Supabase no está configurado en local.properties" }
        val connection = URL("${config.url}/rest/v1/$pathAndQuery").openConnection() as HttpURLConnection
        try {
            connection.requestMethod = method
            connection.connectTimeout = CONNECT_TIMEOUT_MS
            connection.readTimeout = READ_TIMEOUT_MS
            connection.setRequestProperty("apikey", config.anonKey)
            connection.setRequestProperty("Authorization", "Bearer ${config.anonKey}")
            connection.setRequestProperty("Accept", "application/json")
            prefer?.let { connection.setRequestProperty("Prefer", it) }
            if (body != null) {
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                connection.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
            }
            val code = connection.responseCode
            val stream = if (code in 200..299) connection.inputStream else connection.errorStream
            val response = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
            if (code !in 200..299) {
                throw RemoteException(code, "Supabase $method $pathAndQuery → $code: $response")
            }
            response
        } finally {
            connection.disconnect()
        }
    }

    private companion object {
        const val CONNECT_TIMEOUT_MS = 15_000
        const val READ_TIMEOUT_MS = 30_000
    }
}
