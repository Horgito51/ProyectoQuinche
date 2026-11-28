package com.example.proyectoquinche.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.proyectoquinche.data.local.entity.PersonaEntity
import com.example.proyectoquinche.domain.enums.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonaDao {

    @Query("SELECT * FROM persona WHERE persona_id = :personaId LIMIT 1")
    suspend fun getById(personaId: String): PersonaEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(persona: PersonaEntity)

    @Update
    suspend fun update(persona: PersonaEntity)

    @Upsert
    suspend fun upsertAll(personas: List<PersonaEntity>)

    @Query("SELECT * FROM persona WHERE syncStatus IN (:statuses)")
    suspend fun getBySyncStatuses(statuses: List<SyncStatus>): List<PersonaEntity>

    @Query("SELECT persona_id FROM persona WHERE syncStatus != 'SYNCED'")
    suspend fun getUnsyncedIds(): List<String>

    /** Solo marca como sincronizado si nadie volvió a editar el registro mientras subía. */
    @Query(
        "UPDATE persona SET syncStatus = 'SYNCED', remoteId = persona_id " +
            "WHERE persona_id = :personaId AND updatedAt = :updatedAt"
    )
    suspend fun markSynced(personaId: String, updatedAt: Long)

    @Query("UPDATE persona SET syncStatus = :status WHERE persona_id IN (:ids)")
    suspend fun updateSyncStatus(ids: List<String>, status: SyncStatus)

    @Query("SELECT COUNT(*) FROM persona WHERE syncStatus != 'SYNCED'")
    fun observePendingCount(): Flow<Int>
}
