package com.example.proyectoquinche.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.proyectoquinche.data.local.entity.UsuarioEntity
import com.example.proyectoquinche.domain.enums.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuarios WHERE activo = 1 ORDER BY nombreCompleto ASC")
    fun observeActivos(): Flow<List<UsuarioEntity>>

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE username = :username LIMIT 1")
    suspend fun getByUsername(username: String): UsuarioEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(usuario: UsuarioEntity): Long

    @Update
    suspend fun update(usuario: UsuarioEntity)

    @Query("SELECT * FROM usuarios WHERE syncStatus IN (:statuses)")
    suspend fun getBySyncStatuses(statuses: List<SyncStatus>): List<UsuarioEntity>
}
