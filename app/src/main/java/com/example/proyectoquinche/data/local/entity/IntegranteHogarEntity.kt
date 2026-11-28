package com.example.proyectoquinche.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.proyectoquinche.domain.enums.SyncStatus
import com.example.proyectoquinche.domain.model.Syncable

/** Tabla INTEGRANTE_HOGAR (PK compuesta hogar_id + persona_id). */
@Entity(
    tableName = "integrante_hogar",
    primaryKeys = ["hogar_id", "persona_id"],
    foreignKeys = [
        ForeignKey(
            entity = HogarEntity::class,
            parentColumns = ["hogar_id"],
            childColumns = ["hogar_id"]
        ),
        ForeignKey(
            entity = PersonaEntity::class,
            parentColumns = ["persona_id"],
            childColumns = ["persona_id"]
        )
    ],
    indices = [Index(value = ["persona_id"])]
)
data class IntegranteHogarEntity(
    @ColumnInfo(name = "hogar_id")
    val hogarId: String,
    @ColumnInfo(name = "persona_id")
    val personaId: String,
    val parentesco: String? = null,
    override val syncStatus: SyncStatus = SyncStatus.PENDING,
    override val updatedAt: Long = System.currentTimeMillis(),
    override val remoteId: String? = null
) : Syncable
