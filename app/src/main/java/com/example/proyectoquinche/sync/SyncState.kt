package com.example.proyectoquinche.sync

enum class SyncState {
    /** Sin trabajo en cola. */
    IDLE,

    /** En cola: esperando conexión o un reintento. */
    WAITING,

    RUNNING,

    /** Se agotaron los reintentos; quedan cambios en ERROR. */
    FAILED
}
