package com.example.proyectoquinche.sync.handler

import androidx.room.withTransaction
import com.example.proyectoquinche.data.local.database.AppDatabase
import com.example.proyectoquinche.data.remote.SupabaseClient
import com.example.proyectoquinche.data.remote.mapper.toComuneroEntity
import com.example.proyectoquinche.data.remote.mapper.toHogarEntity
import com.example.proyectoquinche.data.remote.mapper.toIntegranteHogarEntity
import com.example.proyectoquinche.data.remote.mapper.toJson
import com.example.proyectoquinche.data.remote.mapper.toPersonaEntity
import com.example.proyectoquinche.data.remote.mapper.toRolEntity
import com.example.proyectoquinche.data.remote.mapper.toUsuarioEntity
import com.example.proyectoquinche.domain.enums.SyncStatus
import com.example.proyectoquinche.sync.SyncHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Bloque 1 del esquema: padrón, hogares y acceso.
 * Sube persona, comunero e integrante_hogar; descarga además rol, hogar y usuario.
 */
class PadronSyncHandler(
    private val db: AppDatabase,
    private val client: SupabaseClient
) : SyncHandler {

    private val personaDao = db.personaDao()
    private val comuneroDao = db.comuneroDao()
    private val integranteDao = db.integranteHogarDao()

    override fun observePendingCount(): Flow<Int> = combine(
        personaDao.observePendingCount(),
        comuneroDao.observePendingCount(),
        integranteDao.observePendingCount()
    ) { personas, comuneros, integrantes -> maxOf(personas, comuneros, integrantes) }

    override suspend fun push() {
        pushPersonas()
        pushComuneros()
        pushIntegrantes()
    }

    private suspend fun pushPersonas() {
        val personas = personaDao.getBySyncStatuses(PENDIENTES)
        if (personas.isEmpty()) return
        marcarErrorSiFalla({ personaDao.updateSyncStatus(personas.map { it.personaId }, SyncStatus.ERROR) }) {
            client.upsert("persona", personas.map { it.toJson() }, onConflict = "persona_id")
        }
        personas.forEach { personaDao.markSynced(it.personaId, it.updatedAt) }
    }

    private suspend fun pushComuneros() {
        val comuneros = comuneroDao.getBySyncStatuses(PENDIENTES)
        if (comuneros.isEmpty()) return
        marcarErrorSiFalla({ comuneroDao.updateSyncStatus(comuneros.map { it.comuneroId }, SyncStatus.ERROR) }) {
            client.upsert("comunero", comuneros.map { it.toJson() }, onConflict = "comunero_id")
        }
        comuneros.forEach { comuneroDao.markSynced(it.comuneroId, it.updatedAt) }
    }

    /**
     * integrante_hogar no tiene columna de borrado: por cada persona modificada se
     * reemplaza en el servidor su conjunto de hogares por el que hay en el dispositivo.
     */
    private suspend fun pushIntegrantes() {
        for (personaId in integranteDao.getUnsyncedPersonaIds()) {
            val filas = integranteDao.getByPersona(personaId)
            marcarErrorSiFalla({ integranteDao.updateSyncStatusByPersona(listOf(personaId), SyncStatus.ERROR) }) {
                val hogares = filas.joinToString(",") { it.hogarId }
                client.delete("integrante_hogar", "persona_id=eq.$personaId&hogar_id=not.in.($hogares)")
                client.upsert("integrante_hogar", filas.map { it.toJson() }, onConflict = "hogar_id,persona_id")
            }
            filas.forEach { integranteDao.markSynced(it.hogarId, it.personaId, it.updatedAt) }
        }
    }

    override suspend fun pull() {
        val roles = client.selectAll("rol", orderBy = "rol_id").map { it.toRolEntity() }
        val personas = client.selectAll("persona", orderBy = "persona_id").map { it.toPersonaEntity() }
        val hogares = client.selectAll("hogar", orderBy = "hogar_id").map { it.toHogarEntity() }
        val integrantes = client.selectAll("integrante_hogar", orderBy = "hogar_id,persona_id")
            .map { it.toIntegranteHogarEntity() }
        val comuneros = client.selectAll("comunero", orderBy = "comunero_id").map { it.toComuneroEntity() }
        val usuarios = client.selectAll("usuario", orderBy = "usuario_id").map { it.toUsuarioEntity() }

        // Los cambios locales aún no subidos prevalecen sobre lo descargado.
        db.withTransaction {
            val personasLocales = personaDao.getUnsyncedIds().toSet()
            val comunerosLocales = comuneroDao.getUnsyncedIds().toSet()
            val integrantesLocales = integranteDao.getUnsyncedPersonaIds().toSet()

            db.rolDao().upsertAll(roles)
            personaDao.upsertAll(personas.filterNot { it.personaId in personasLocales })
            db.hogarDao().upsertAll(hogares)
            integranteDao.deleteSynced()
            integranteDao.upsertAll(integrantes.filterNot { it.personaId in integrantesLocales })
            comuneroDao.upsertAll(comuneros.filterNot { it.comuneroId in comunerosLocales })
            db.usuarioDao().upsertAll(usuarios)
        }
    }

    private suspend fun marcarErrorSiFalla(marcarError: suspend () -> Unit, bloque: suspend () -> Unit) {
        try {
            bloque()
        } catch (e: Exception) {
            marcarError()
            throw e
        }
    }

    private companion object {
        val PENDIENTES = listOf(SyncStatus.PENDING, SyncStatus.ERROR)
    }
}
