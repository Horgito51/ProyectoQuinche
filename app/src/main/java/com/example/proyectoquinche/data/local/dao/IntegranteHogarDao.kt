package com.example.proyectoquinche.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.example.proyectoquinche.data.local.entity.IntegranteHogarEntity
import com.example.proyectoquinche.domain.enums.SyncStatus
import com.example.proyectoquinche.domain.model.IntegranteHogar
import kotlinx.coroutines.flow.Flow

@Dao
interface IntegranteHogarDao {

    /** Integrantes de un hogar; el responsable aparece primero. */
    @Query(
        """
        SELECT ih.persona_id AS personaId, p.nombres, p.apellidos, ih.parentesco
        FROM integrante_hogar ih
        JOIN persona p ON p.persona_id = ih.persona_id
        JOIN hogar h ON h.hogar_id = ih.hogar_id
        WHERE ih.hogar_id = :hogarId
        ORDER BY CASE WHEN ih.persona_id = h.responsable_id THEN 0 ELSE 1 END,
                 p.nombres COLLATE NOCASE
        """
    )
    fun observeIntegrantes(hogarId: String): Flow<List<IntegranteHogar>>

    @Query("SELECT * FROM integrante_hogar WHERE persona_id = :personaId")
    suspend fun getByPersona(personaId: String): List<IntegranteHogarEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(integrante: IntegranteHogarEntity)

    @Query("DELETE FROM integrante_hogar WHERE persona_id = :personaId")
    suspend fun deleteByPersona(personaId: String)

    @Upsert
    suspend fun upsertAll(integrantes: List<IntegranteHogarEntity>)

    @Query("SELECT * FROM integrante_hogar WHERE syncStatus IN (:statuses)")
    suspend fun getBySyncStatuses(statuses: List<SyncStatus>): List<IntegranteHogarEntity>

    @Query("SELECT DISTINCT persona_id FROM integrante_hogar WHERE syncStatus != 'SYNCED'")
    suspend fun getUnsyncedPersonaIds(): List<String>

    @Query(
        "UPDATE integrante_hogar SET syncStatus = 'SYNCED', remoteId = hogar_id || ':' || persona_id " +
            "WHERE hogar_id = :hogarId AND persona_id = :personaId AND updatedAt = :updatedAt"
    )
    suspend fun markSynced(hogarId: String, personaId: String, updatedAt: Long)

    @Query("UPDATE integrante_hogar SET syncStatus = :status WHERE persona_id IN (:personaIds)")
    suspend fun updateSyncStatusByPersona(personaIds: List<String>, status: SyncStatus)

    /** Antes de aplicar una descarga completa se descartan las filas ya sincronizadas. */
    @Query("DELETE FROM integrante_hogar WHERE syncStatus = 'SYNCED'")
    suspend fun deleteSynced()

    @Query("SELECT COUNT(DISTINCT persona_id) FROM integrante_hogar WHERE syncStatus != 'SYNCED'")
    fun observePendingCount(): Flow<Int>
}
