package com.example.proyectoquinche.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.proyectoquinche.domain.enums.SyncStatus
import com.example.proyectoquinche.domain.model.Syncable

@Entity(
    tableName = "comuneros",
    indices = [Index(value = ["cedula"], unique = true)]
)
data class ComuneroEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cedula: String,
    val nombres: String,
    val apellidos: String,
    val telefono: String? = null,
    val direccion: String? = null,
    val activo: Boolean = true,
    override val syncStatus: SyncStatus = SyncStatus.PENDING,
    override val updatedAt: Long = System.currentTimeMillis(),
    override val remoteId: String? = null
) : Syncable
