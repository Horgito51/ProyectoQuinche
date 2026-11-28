package com.example.proyectoquinche.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.proyectoquinche.data.local.entity.ComuneroEntity
import com.example.proyectoquinche.domain.enums.SyncStatus
import com.example.proyectoquinche.domain.model.ComunidadResumen
import com.example.proyectoquinche.domain.model.ComuneroDetalle
import com.example.proyectoquinche.domain.model.ComuneroResumen
import kotlinx.coroutines.flow.Flow

@Dao
interface ComuneroDao {

    @Query(
        """
        SELECT c.comunero_id AS comuneroId, c.persona_id AS personaId, p.nombres, p.apellidos,
               c.codigo_comunero AS codigoComunero, c.estado,
               h.numero_casa AS numeroCasa, h.sector
        FROM comunero c
        JOIN persona p ON p.persona_id = c.persona_id
        LEFT JOIN hogar h ON h.hogar_id = (
            SELECT ih.hogar_id FROM integrante_hogar ih WHERE ih.persona_id = c.persona_id LIMIT 1
        )
        ORDER BY p.nombres COLLATE NOCASE, p.apellidos COLLATE NOCASE
        """
    )
    fun observeResumenes(): Flow<List<ComuneroResumen>>

    @Query(
        """
        SELECT c.comunero_id AS comuneroId, c.persona_id AS personaId, p.nombres, p.apellidos,
               p.telefono, p.correo, c.codigo_comunero AS codigoComunero, c.estado,
               h.hogar_id AS hogarId, h.numero_casa AS numeroCasa, h.sector, h.direccion,
               ih.parentesco,
               CASE
                   WHEN c.syncStatus = 'ERROR' OR p.syncStatus = 'ERROR' OR ih.syncStatus = 'ERROR' THEN 'ERROR'
                   WHEN c.syncStatus != 'SYNCED' OR p.syncStatus != 'SYNCED'
                        OR (ih.syncStatus IS NOT NULL AND ih.syncStatus != 'SYNCED') THEN 'PENDING'
                   ELSE 'SYNCED'
               END AS syncStatus
        FROM comunero c
        JOIN persona p ON p.persona_id = c.persona_id
        LEFT JOIN integrante_hogar ih ON ih.persona_id = c.persona_id
        LEFT JOIN hogar h ON h.hogar_id = ih.hogar_id
        WHERE c.comunero_id = :comuneroId
        LIMIT 1
        """
    )
    fun observeDetalle(comuneroId: String): Flow<ComuneroDetalle?>

    @Query(
        """
        SELECT COUNT(*) AS total,
               COALESCE(SUM(CASE WHEN estado = 'ACTIVO' THEN 1 ELSE 0 END), 0) AS activos,
               COALESCE(SUM(CASE WHEN estado != 'ACTIVO' THEN 1 ELSE 0 END), 0) AS inactivos,
               (SELECT COUNT(*) FROM hogar) AS hogares
        FROM comunero
        """
    )
    fun observeResumenComunidad(): Flow<ComunidadResumen>

    @Query("SELECT * FROM comunero WHERE comunero_id = :comuneroId LIMIT 1")
    suspend fun getById(comuneroId: String): ComuneroEntity?

    @Query("SELECT codigo_comunero FROM comunero WHERE codigo_comunero IS NOT NULL")
    suspend fun getCodigos(): List<String>

    @Query(
        "SELECT COUNT(*) FROM comunero " +
            "WHERE codigo_comunero = :codigo AND comunero_id != :excluirComuneroId"
    )
    suspend fun countByCodigo(codigo: String, excluirComuneroId: String): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(comunero: ComuneroEntity)

    @Update
    suspend fun update(comunero: ComuneroEntity)

    @Upsert
    suspend fun upsertAll(comuneros: List<ComuneroEntity>)

    @Query("SELECT * FROM comunero WHERE syncStatus IN (:statuses)")
    suspend fun getBySyncStatuses(statuses: List<SyncStatus>): List<ComuneroEntity>

    @Query("SELECT comunero_id FROM comunero WHERE syncStatus != 'SYNCED'")
    suspend fun getUnsyncedIds(): List<String>

    @Query(
        "UPDATE comunero SET syncStatus = 'SYNCED', remoteId = comunero_id " +
            "WHERE comunero_id = :comuneroId AND updatedAt = :updatedAt"
    )
    suspend fun markSynced(comuneroId: String, updatedAt: Long)

    @Query("UPDATE comunero SET syncStatus = :status WHERE comunero_id IN (:ids)")
    suspend fun updateSyncStatus(ids: List<String>, status: SyncStatus)

    @Query("SELECT COUNT(*) FROM comunero WHERE syncStatus != 'SYNCED'")
    fun observePendingCount(): Flow<Int>
}
