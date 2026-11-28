package com.example.proyectoquinche.sync

import android.content.Context
import androidx.lifecycle.asFlow
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.proyectoquinche.data.remote.SupabaseClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.TimeUnit

/** Punto único para pedir, programar y observar la sincronización con Supabase. */
class SyncManager(
    context: Context,
    private val client: SupabaseClient,
    private val handlers: List<SyncHandler>
) {
    private val workManager = WorkManager.getInstance(context)
    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    private val mutex = Mutex()

    private val _lastSyncAt = MutableStateFlow(prefs.getLong(KEY_LAST_SYNC, 0L).takeIf { it > 0 })
    val lastSyncAt: StateFlow<Long?> = _lastSyncAt.asStateFlow()

    val isConfigured: Boolean get() = client.isConfigured

    fun observePendingCount(): Flow<Int> =
        combine(handlers.map { it.observePendingCount() }) { counts -> counts.sum() }

    fun observeState(): Flow<SyncState> =
        workManager.getWorkInfosForUniqueWorkLiveData(WORK_NOW).asFlow().map { infos ->
            when {
                infos.any { it.state == WorkInfo.State.RUNNING } -> SyncState.RUNNING
                infos.any { it.state == WorkInfo.State.ENQUEUED || it.state == WorkInfo.State.BLOCKED } ->
                    SyncState.WAITING
                infos.lastOrNull()?.state == WorkInfo.State.FAILED -> SyncState.FAILED
                else -> SyncState.IDLE
            }
        }

    /** Encola una sincronización que se ejecuta en cuanto haya Internet. */
    fun requestSync() {
        if (!isConfigured) return
        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(networkConstraints())
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, BACKOFF_SECONDS, TimeUnit.SECONDS)
            .build()
        workManager.enqueueUniqueWork(WORK_NOW, ExistingWorkPolicy.APPEND_OR_REPLACE, request)
    }

    fun schedulePeriodicSync() {
        if (!isConfigured) return
        val request = PeriodicWorkRequestBuilder<SyncWorker>(PERIODIC_HOURS, TimeUnit.HOURS)
            .setConstraints(networkConstraints())
            .build()
        workManager.enqueueUniquePeriodicWork(WORK_PERIODIC, ExistingPeriodicWorkPolicy.KEEP, request)
    }

    /** Sube todos los cambios locales y luego descarga el estado del servidor. */
    suspend fun runSync() = mutex.withLock {
        handlers.forEach { it.push() }
        handlers.forEach { it.pull() }
        val now = System.currentTimeMillis()
        prefs.edit().putLong(KEY_LAST_SYNC, now).apply()
        _lastSyncAt.value = now
    }

    private fun networkConstraints() = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    private companion object {
        const val PREFS = "sync_prefs"
        const val KEY_LAST_SYNC = "last_sync_at"
        const val WORK_NOW = "sync_now"
        const val WORK_PERIODIC = "sync_periodic"
        const val BACKOFF_SECONDS = 30L
        const val PERIODIC_HOURS = 6L
    }
}
