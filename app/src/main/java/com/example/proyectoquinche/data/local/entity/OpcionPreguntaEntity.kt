package com.example.proyectoquinche.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Tabla OPCION_PREGUNTA. */
@Entity(
    tableName = "opcion_pregunta",
    foreignKeys = [
        ForeignKey(
            entity = PreguntaCensoEntity::class,
            parentColumns = ["pregunta_id"],
            childColumns = ["pregunta_id"]
        )
    ],
    indices = [Index(value = ["pregunta_id"])]
)
data class OpcionPreguntaEntity(
    @PrimaryKey
    @ColumnInfo(name = "opcion_id")
    val opcionId: Int,
    @ColumnInfo(name = "pregunta_id")
    val preguntaId: Int,
    val etiqueta: String,
    val orden: Int
)
