package com.example.proyectoquinche.data.local.converter

import androidx.room.TypeConverter
import com.example.proyectoquinche.domain.enums.EstadoComunero
import com.example.proyectoquinche.domain.enums.MovimientoTipo
import com.example.proyectoquinche.domain.enums.SyncStatus
import com.example.proyectoquinche.domain.enums.UserRole

class Converters {

    @TypeConverter
    fun fromUserRole(value: UserRole): String = value.name

    @TypeConverter
    fun toUserRole(value: String): UserRole = UserRole.valueOf(value)

    @TypeConverter
    fun fromSyncStatus(value: SyncStatus): String = value.name

    @TypeConverter
    fun toSyncStatus(value: String): SyncStatus = SyncStatus.valueOf(value)

    @TypeConverter
    fun fromMovimientoTipo(value: MovimientoTipo): String = value.name

    @TypeConverter
    fun toMovimientoTipo(value: String): MovimientoTipo = MovimientoTipo.valueOf(value)

    @TypeConverter
    fun fromEstadoComunero(value: EstadoComunero): String = value.name

    @TypeConverter
    fun toEstadoComunero(value: String): EstadoComunero = EstadoComunero.from(value)
}
