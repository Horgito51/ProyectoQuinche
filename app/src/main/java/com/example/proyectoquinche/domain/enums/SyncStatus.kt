package com.example.proyectoquinche.domain.enums

/**
 * Estado de sincronización Offline First.
 * Room persiste estos valores para sobrevivir cierre de app y reintentar sync.
 */
enum class SyncStatus {
    /** Cambio local pendiente de subir. */
    PENDING,

    /** Sincronización en curso. */
    SYNCING,

    /** Ya alineado con el origen remoto / compartido. */
    SYNCED,

    /** Falló el último intento; se reintentará con WorkManager. */
    ERROR
}
