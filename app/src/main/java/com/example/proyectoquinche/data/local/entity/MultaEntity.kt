package com.example.proyectoquinche.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.proyectoquinche.domain.enums.SyncStatus
import com.example.proyectoquinche.domain.model.Syncable

@Entity(
    tableName = "multas",
    foreignKeys = [
        ForeignKey(
            entity = ComuneroEntity::class,
            parentColumns = ["id"],
            childColumns = ["comuneroId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["comuneroId"])]
)
data class MultaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val comuneroId: Long,
    val motivo: String,
    val monto: Double,
    val fechaEpochDay: Long,
    val pagada: Boolean = false,
    val registradoPorUsuarioId: Long? = null,
    override val syncStatus: SyncStatus = SyncStatus.PENDING,
    override val updatedAt: Long = System.currentTimeMillis(),
    override val remoteId: String? = null
) : Syncable
