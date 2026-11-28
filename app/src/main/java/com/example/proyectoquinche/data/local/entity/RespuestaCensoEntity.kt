package com.example.proyectoquinche.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Tabla RESPUESTA_CENSO. */
@Entity(
    tableName = "respuesta_censo",
    foreignKeys = [
        ForeignKey(
            entity = FichaCensalEntity::class,
            parentColumns = ["ficha_id"],
            childColumns = ["ficha_id"]
        ),
        ForeignKey(
            entity = PreguntaCensoEntity::class,
            parentColumns = ["pregunta_id"],
            childColumns = ["pregunta_id"]
        )
    ],
    indices = [Index(value = ["ficha_id"]), Index(value = ["pregunta_id"])]
)
data class RespuestaCensoEntity(
    @PrimaryKey
    @ColumnInfo(name = "respuesta_id")
    val respuestaId: String,
    @ColumnInfo(name = "ficha_id")
    val fichaId: String,
    @ColumnInfo(name = "pregunta_id")
    val preguntaId: Int,
    val texto: String? = null,
    val numero: Double? = null,
    val booleano: Boolean? = null
)
