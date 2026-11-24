package com.example.proyectoquinche.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.proyectoquinche.domain.enums.MovimientoTipo
import com.example.proyectoquinche.domain.enums.SyncStatus
import com.example.proyectoquinche.domain.model.Syncable

@Entity(
    tableName = "movimientos",
    indices = [Index(value = ["fechaEpochDay"])]
)
data class MovimientoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tipo: MovimientoTipo,
    val concepto: String,
    val monto: Double,
    val fechaEpochDay: Long,
    val registradoPorUsuarioId: Long? = null,
    override val syncStatus: SyncStatus = SyncStatus.PENDING,
    override val updatedAt: Long = System.currentTimeMillis(),
    override val remoteId: String? = null
) : Syncable
