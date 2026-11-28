package com.example.proyectoquinche.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.proyectoquinche.data.local.converter.Converters
import com.example.proyectoquinche.data.local.dao.AsistenciaDao
import com.example.proyectoquinche.data.local.dao.CensoDao
import com.example.proyectoquinche.data.local.dao.ComuneroDao
import com.example.proyectoquinche.data.local.dao.HogarDao
import com.example.proyectoquinche.data.local.dao.IntegranteHogarDao
import com.example.proyectoquinche.data.local.dao.MovimientoDao
import com.example.proyectoquinche.data.local.dao.MultaDao
import com.example.proyectoquinche.data.local.dao.PersonaDao
import com.example.proyectoquinche.data.local.dao.RolDao
import com.example.proyectoquinche.data.local.dao.UsuarioDao
import com.example.proyectoquinche.data.local.entity.AsistenciaEntity
import com.example.proyectoquinche.data.local.entity.ComuneroEntity
import com.example.proyectoquinche.data.local.entity.FichaCensalEntity
import com.example.proyectoquinche.data.local.entity.HogarEntity
import com.example.proyectoquinche.data.local.entity.IntegranteHogarEntity
import com.example.proyectoquinche.data.local.entity.MovimientoEntity
import com.example.proyectoquinche.data.local.entity.MultaEntity
import com.example.proyectoquinche.data.local.entity.OpcionPreguntaEntity
import com.example.proyectoquinche.data.local.entity.PersonaEntity
import com.example.proyectoquinche.data.local.entity.PreguntaCensoEntity
import com.example.proyectoquinche.data.local.entity.RespuestaCensoEntity
import com.example.proyectoquinche.data.local.entity.RespuestaOpcionEntity
import com.example.proyectoquinche.data.local.entity.RolEntity
import com.example.proyectoquinche.data.local.entity.SeccionCensoEntity
import com.example.proyectoquinche.data.local.entity.UsuarioEntity

/**
 * Base local Offline-first. Los datos viven aquí aunque no haya Internet
 * y sobreviven al cierre de la aplicación.
 */
@Database(
    entities = [
        // Padrón, hogares y acceso
        PersonaEntity::class,
        HogarEntity::class,
        IntegranteHogarEntity::class,
        ComuneroEntity::class,
        RolEntity::class,
        UsuarioEntity::class,
        // Ficha del censo del hogar
        SeccionCensoEntity::class,
        PreguntaCensoEntity::class,
        OpcionPreguntaEntity::class,
        FichaCensalEntity::class,
        RespuestaCensoEntity::class,
        RespuestaOpcionEntity::class,
        // Gestión administrativa
        AsistenciaEntity::class,
        MultaEntity::class,
        MovimientoEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun personaDao(): PersonaDao
    abstract fun hogarDao(): HogarDao
    abstract fun integranteHogarDao(): IntegranteHogarDao
    abstract fun comuneroDao(): ComuneroDao
    abstract fun rolDao(): RolDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun censoDao(): CensoDao
    abstract fun asistenciaDao(): AsistenciaDao
    abstract fun multaDao(): MultaDao
    abstract fun movimientoDao(): MovimientoDao

    companion object {
        private const val DB_NAME = "proyecto_quinche.db"

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    // Fase de desarrollo: el esquema v1 no tenía datos reales que migrar.
                    .fallbackToDestructiveMigration()
                    .build().also { instance = it }
            }
        }
    }
}
