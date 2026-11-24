package com.example.proyectoquinche.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.proyectoquinche.data.local.entity.ComuneroEntity
import com.example.proyectoquinche.domain.enums.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ComuneroDao {

    @Query("SELECT * FROM comuneros WHERE activo = 1 ORDER BY apellidos ASC, nombres ASC")
    fun observeActivos(): Flow<List<ComuneroEntity>>

    @Query("SELECT * FROM comuneros WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): ComuneroEntity?

    @Query("SELECT * FROM comuneros WHERE cedula = :cedula LIMIT 1")
    suspend fun getByCedula(cedula: String): ComuneroEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(comunero: ComuneroEntity): Long

    @Update
    suspend fun update(comunero: ComuneroEntity)

    @Query("SELECT * FROM comuneros WHERE syncStatus IN (:statuses)")
    suspend fun getBySyncStatuses(statuses: List<SyncStatus>): List<ComuneroEntity>
}
