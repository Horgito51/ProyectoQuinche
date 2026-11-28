package com.example.proyectoquinche.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Tabla PREGUNTA_CENSO. */
@Entity(
    tableName = "pregunta_censo",
    foreignKeys = [
        ForeignKey(
            entity = SeccionCensoEntity::class,
            parentColumns = ["seccion_id"],
            childColumns = ["seccion_id"]
        )
    ],
    indices = [Index(value = ["seccion_id"])]
)
data class PreguntaCensoEntity(
    @PrimaryKey
    @ColumnInfo(name = "pregunta_id")
    val preguntaId: Int,
    @ColumnInfo(name = "seccion_id")
    val seccionId: Int,
    val enunciado: String,
    @ColumnInfo(name = "tipo_respuesta")
    val tipoRespuesta: String,
    val orden: Int
)
