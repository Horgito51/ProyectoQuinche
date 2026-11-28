package com.example.proyectoquinche.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.proyectoquinche.domain.enums.SyncStatus
import com.example.proyectoquinche.domain.model.Syncable

/** Tabla PERSONA del esquema central. */
@Entity(tableName = "persona")
data class PersonaEntity(
    @PrimaryKey
    @ColumnInfo(name = "persona_id")
    val personaId: String,
    val nombres: String,
    val apellidos: String,
    val telefono: String? = null,
    val correo: String? = null,
    override val syncStatus: SyncStatus = SyncStatus.PENDING,
    override val updatedAt: Long = System.currentTimeMillis(),
    override val remoteId: String? = null
) : Syncable
