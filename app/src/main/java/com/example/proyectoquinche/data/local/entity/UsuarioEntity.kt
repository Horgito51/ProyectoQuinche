package com.example.proyectoquinche.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Tabla USUARIO. Se descarga del servidor (con clave_hash) para poder
 * validar credenciales sin Internet.
 */
@Entity(
    tableName = "usuario",
    foreignKeys = [
        ForeignKey(
            entity = PersonaEntity::class,
            parentColumns = ["persona_id"],
            childColumns = ["persona_id"]
        ),
        ForeignKey(
            entity = RolEntity::class,
            parentColumns = ["rol_id"],
            childColumns = ["rol_id"]
        )
    ],
    indices = [
        Index(value = ["persona_id"], unique = true),
        Index(value = ["rol_id"]),
        Index(value = ["nombre_usuario"], unique = true)
    ]
)
data class UsuarioEntity(
    @PrimaryKey
    @ColumnInfo(name = "usuario_id")
    val usuarioId: String,
    @ColumnInfo(name = "persona_id")
    val personaId: String,
    @ColumnInfo(name = "rol_id")
    val rolId: Int,
    @ColumnInfo(name = "nombre_usuario")
    val nombreUsuario: String,
    @ColumnInfo(name = "clave_hash")
    val claveHash: String,
    val activo: Boolean
)
