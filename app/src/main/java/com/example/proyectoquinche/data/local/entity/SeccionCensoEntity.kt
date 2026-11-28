package com.example.proyectoquinche.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/** Tabla SECCION_CENSO (catálogo del formulario del hogar). */
@Entity(tableName = "seccion_censo")
data class SeccionCensoEntity(
    @PrimaryKey
    @ColumnInfo(name = "seccion_id")
    val seccionId: Int,
    val titulo: String,
    val orden: Int
)
