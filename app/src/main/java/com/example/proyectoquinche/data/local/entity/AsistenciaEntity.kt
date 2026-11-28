package com.example.proyectoquinche.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.proyectoquinche.domain.enums.SyncStatus
import com.example.proyectoquinche.domain.model.Syncable

@Entity(
    tableName = "asistencias",
    foreignKeys = [
        ForeignKey(
            entity = ComuneroEntity::class,
            parentColumns = ["comunero_id"],
            childColumns = ["comuneroId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["comuneroId"]),
        Index(value = ["comuneroId", "fechaEpochDay"], unique = true)
    ]
)
data class AsistenciaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val comuneroId: String,
    /** Día de la asistencia en epoch-day (LocalDate.toEpochDay). */
    val fechaEpochDay: Long,
    val presente: Boolean,
    val observacion: String? = null,
    val registradoPorUsuarioId: Long? = null,
    override val syncStatus: SyncStatus = SyncStatus.PENDING,
    override val updatedAt: Long = System.currentTimeMillis(),
    override val remoteId: String? = null
) : Syncable
