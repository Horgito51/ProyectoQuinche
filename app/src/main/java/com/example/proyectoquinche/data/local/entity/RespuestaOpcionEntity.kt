package com.example.proyectoquinche.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/** Tabla RESPUESTA_OPCION (opciones elegidas en una respuesta). */
@Entity(
    tableName = "respuesta_opcion",
    primaryKeys = ["respuesta_id", "opcion_id"],
    foreignKeys = [
        ForeignKey(
            entity = RespuestaCensoEntity::class,
            parentColumns = ["respuesta_id"],
            childColumns = ["respuesta_id"]
        ),
        ForeignKey(
            entity = OpcionPreguntaEntity::class,
            parentColumns = ["opcion_id"],
            childColumns = ["opcion_id"]
        )
    ],
    indices = [Index(value = ["opcion_id"])]
)
data class RespuestaOpcionEntity(
    @ColumnInfo(name = "respuesta_id")
    val respuestaId: String,
    @ColumnInfo(name = "opcion_id")
    val opcionId: Int
)
