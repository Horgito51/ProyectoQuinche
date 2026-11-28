package com.example.proyectoquinche.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.proyectoquinche.data.local.entity.MultaEntity
import com.example.proyectoquinche.domain.enums.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface MultaDao {

    @Query("SELECT * FROM multas ORDER BY fechaEpochDay DESC, id DESC")
    fun observeTodas(): Flow<List<MultaEntity>>

    @Query("SELECT * FROM multas WHERE comuneroId = :comuneroId ORDER BY fechaEpochDay DESC")
    fun observePorComunero(comuneroId: String): Flow<List<MultaEntity>>

    @Query("SELECT * FROM multas WHERE pagada = 0 ORDER BY fechaEpochDay DESC")
    fun observePendientes(): Flow<List<MultaEntity>>

    @Query("SELECT * FROM multas WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): MultaEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(multa: MultaEntity): Long

    @Update
    suspend fun update(multa: MultaEntity)

    @Query("SELECT * FROM multas WHERE syncStatus IN (:statuses)")
    suspend fun getBySyncStatuses(statuses: List<SyncStatus>): List<MultaEntity>
}
