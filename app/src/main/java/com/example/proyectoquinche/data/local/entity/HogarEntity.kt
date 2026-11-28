package com.example.proyectoquinche.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Tabla HOGAR. Se descarga del servidor; la app no la modifica. */
@Entity(
    tableName = "hogar",
    foreignKeys = [
        ForeignKey(
            entity = PersonaEntity::class,
            parentColumns = ["persona_id"],
            childColumns = ["responsable_id"]
        )
    ],
    indices = [Index(value = ["responsable_id"])]
)
data class HogarEntity(
    @PrimaryKey
    @ColumnInfo(name = "hogar_id")
    val hogarId: String,
    @ColumnInfo(name = "numero_casa")
    val numeroCasa: String,
    val sector: String,
    val direccion: String,
    val referencia: String? = null,
    @ColumnInfo(name = "responsable_id")
    val responsableId: String
)
