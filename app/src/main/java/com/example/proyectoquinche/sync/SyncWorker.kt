package com.example.proyectoquinche.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.proyectoquinche.QuincheApp
import kotlinx.coroutines.CancellationException

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val syncManager = (applicationContext as QuincheApp).container.syncManager
        if (!syncManager.isConfigured) return Result.success()
        return try {
            syncManager.runSync()
            Result.success()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Sincronización fallida (intento ${runAttemptCount + 1})", e)
            if (runAttemptCount < MAX_RETRIES) Result.retry() else Result.failure()
        }
    }

    private companion object {
        const val TAG = "SyncWorker"
        const val MAX_RETRIES = 5
    }
}
