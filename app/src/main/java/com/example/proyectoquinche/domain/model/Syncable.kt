package com.example.proyectoquinche.domain.model

import com.example.proyectoquinche.domain.enums.SyncStatus

/**
 * Contrato común para registros que viven en Room y luego se sincronizan.
 */
interface Syncable {
    val syncStatus: SyncStatus
    val updatedAt: Long
    val remoteId: String?
}
