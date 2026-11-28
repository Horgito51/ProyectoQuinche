package com.example.proyectoquinche.sync

import kotlinx.coroutines.flow.Flow

/**
 * Sincronización de un grupo de tablas [com.example.proyectoquinche.domain.model.Syncable].
 *
 * Cada módulo registra su handler en AppContainer; SyncWorker ejecuta primero
 * todos los [push] (subir cambios PENDING/ERROR) y luego todos los [pull], en el
 * orden de registro (respetar dependencias de claves foráneas).
 */
interface SyncHandler {

    /** Cantidad de registros locales pendientes de subir. */
    fun observePendingCount(): Flow<Int>

    suspend fun push()

    suspend fun pull()
}
