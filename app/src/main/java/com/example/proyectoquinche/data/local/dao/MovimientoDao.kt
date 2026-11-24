package com.example.proyectoquinche.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.proyectoquinche.data.local.entity.MovimientoEntity
import com.example.proyectoquinche.domain.enums.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface MovimientoDao {

    @Query("SELECT * FROM movimientos ORDER BY fechaEpochDay DESC, id DESC")
    fun observeTodos(): Flow<List<MovimientoEntity>>

    @Query(
        "SELECT * FROM movimientos WHERE fechaEpochDay BETWEEN :desde AND :hasta " +
            "ORDER BY fechaEpochDay DESC, id DESC"
    )
    fun observeEntreFechas(desde: Long, hasta: Long): Flow<List<MovimientoEntity>>

    @Query("SELECT * FROM movimientos WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): MovimientoEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(movimiento: MovimientoEntity): Long

    @Update
    suspend fun update(movimiento: MovimientoEntity)

    @Query("SELECT * FROM movimientos WHERE syncStatus IN (:statuses)")
    suspend fun getBySyncStatuses(statuses: List<SyncStatus>): List<MovimientoEntity>
}
