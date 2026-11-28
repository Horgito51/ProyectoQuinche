package com.example.proyectoquinche.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.proyectoquinche.domain.enums.EstadoComunero
import com.example.proyectoquinche.domain.enums.SyncStatus
import com.example.proyectoquinche.domain.model.Syncable

/** Tabla COMUNERO: una PERSONA inscrita en el padrón (1 a 0..1). */
@Entity(
    tableName = "comunero",
    foreignKeys = [
        ForeignKey(
            entity = PersonaEntity::class,
            parentColumns = ["persona_id"],
            childColumns = ["persona_id"]
        )
    ],
    indices = [
        Index(value = ["persona_id"], unique = true),
        Index(value = ["codigo_comunero"], unique = true)
    ]
)
data class ComuneroEntity(
    @PrimaryKey
    @ColumnInfo(name = "comunero_id")
    val comuneroId: String,
    @ColumnInfo(name = "persona_id")
    val personaId: String,
    @ColumnInfo(name = "codigo_comunero")
    val codigoComunero: String? = null,
    val estado: EstadoComunero = EstadoComunero.ACTIVO,
    override val syncStatus: SyncStatus = SyncStatus.PENDING,
    override val updatedAt: Long = System.currentTimeMillis(),
    override val remoteId: String? = null
) : Syncable
