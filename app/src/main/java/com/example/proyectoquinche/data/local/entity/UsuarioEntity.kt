package com.example.proyectoquinche.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.proyectoquinche.domain.enums.SyncStatus
import com.example.proyectoquinche.domain.enums.UserRole
import com.example.proyectoquinche.domain.model.Syncable

@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["username"], unique = true)]
)
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val nombreCompleto: String,
    val rol: UserRole,
    val pinHash: String,
    val activo: Boolean = true,
    override val syncStatus: SyncStatus = SyncStatus.PENDING,
    override val updatedAt: Long = System.currentTimeMillis(),
    override val remoteId: String? = null
) : Syncable
