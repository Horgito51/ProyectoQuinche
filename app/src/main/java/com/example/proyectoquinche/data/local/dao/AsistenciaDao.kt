package com.example.proyectoquinche.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.proyectoquinche.data.local.entity.AsistenciaEntity
import com.example.proyectoquinche.domain.enums.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface AsistenciaDao {

    @Query("SELECT * FROM asistencias WHERE fechaEpochDay = :fechaEpochDay ORDER BY id ASC")
    fun observePorFecha(fechaEpochDay: Long): Flow<List<AsistenciaEntity>>

    @Query("SELECT * FROM asistencias WHERE comuneroId = :comuneroId ORDER BY fechaEpochDay DESC")
    fun observePorComunero(comuneroId: Long): Flow<List<AsistenciaEntity>>

    @Query("SELECT * FROM asistencias WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): AsistenciaEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(asistencia: AsistenciaEntity): Long

    @Update
    suspend fun update(asistencia: AsistenciaEntity)

    @Query("SELECT * FROM asistencias WHERE syncStatus IN (:statuses)")
    suspend fun getBySyncStatuses(statuses: List<SyncStatus>): List<AsistenciaEntity>
}
