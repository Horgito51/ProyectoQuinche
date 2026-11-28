package com.example.proyectoquinche.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** Tabla ROL (Presidente, Secretaria, Tesorera). */
@Entity(
    tableName = "rol",
    indices = [Index(value = ["nombre"], unique = true)]
)
data class RolEntity(
    @PrimaryKey
    @ColumnInfo(name = "rol_id")
    val rolId: Int,
    val nombre: String
)
