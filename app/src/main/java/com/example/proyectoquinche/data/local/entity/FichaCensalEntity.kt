package com.example.proyectoquinche.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Tabla FICHA_CENSAL. Las fechas se guardan tal como las entrega el servidor
 * (ISO-8601, p. ej. "2026-10-04T10:15:00").
 */
@Entity(
    tableName = "ficha_censal",
    foreignKeys = [
        ForeignKey(
            entity = HogarEntity::class,
            parentColumns = ["hogar_id"],
            childColumns = ["hogar_id"]
        ),
        ForeignKey(
            entity = PersonaEntity::class,
            parentColumns = ["persona_id"],
            childColumns = ["informante_id"]
        )
    ],
    indices = [Index(value = ["hogar_id"]), Index(value = ["informante_id"])]
)
data class FichaCensalEntity(
    @PrimaryKey
    @ColumnInfo(name = "ficha_id")
    val fichaId: String,
    @ColumnInfo(name = "hogar_id")
    val hogarId: String,
    @ColumnInfo(name = "informante_id")
    val informanteId: String,
    @ColumnInfo(name = "fecha_registro")
    val fechaRegistro: String,
    val consentimiento: Boolean,
    @ColumnInfo(name = "fecha_consentimiento")
    val fechaConsentimiento: String,
    @ColumnInfo(name = "version_formulario")
    val versionFormulario: String
)
