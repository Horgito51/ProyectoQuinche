package com.example.proyectoquinche.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.proyectoquinche.data.local.converter.Converters
import com.example.proyectoquinche.data.local.dao.AsistenciaDao
import com.example.proyectoquinche.data.local.dao.ComuneroDao
import com.example.proyectoquinche.data.local.dao.MovimientoDao
import com.example.proyectoquinche.data.local.dao.MultaDao
import com.example.proyectoquinche.data.local.dao.UsuarioDao
import com.example.proyectoquinche.data.local.entity.AsistenciaEntity
import com.example.proyectoquinche.data.local.entity.ComuneroEntity
import com.example.proyectoquinche.data.local.entity.MovimientoEntity
import com.example.proyectoquinche.data.local.entity.MultaEntity
import com.example.proyectoquinche.data.local.entity.UsuarioEntity

/**
 * Base local Offline-first. Los datos viven aquí aunque no haya Internet
 * y sobreviven al cierre de la aplicación.
 */
@Database(
    entities = [
        UsuarioEntity::class,
        ComuneroEntity::class,
        AsistenciaEntity::class,
        MultaEntity::class,
        MovimientoEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun comuneroDao(): ComuneroDao
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
                ).build().also { instance = it }
            }
        }
    }
}
