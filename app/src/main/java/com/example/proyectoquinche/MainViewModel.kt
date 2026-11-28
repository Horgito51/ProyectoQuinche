package com.example.proyectoquinche

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyectoquinche.domain.model.SessionUser
import com.example.proyectoquinche.domain.repository.AuthRepository
import com.example.proyectoquinche.sync.SyncManager
import com.example.proyectoquinche.sync.SyncState
import com.example.proyectoquinche.ui.common.appContainer
import com.example.proyectoquinche.util.NetworkMonitor
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * Estado compartido por toda la actividad: sesión, conectividad y sincronización.
 * Lo usan el encabezado, el menú lateral, "Sincronizar" y "Mi perfil".
 */
class MainViewModel(
    private val authRepository: AuthRepository,
    private val syncManager: SyncManager,
    networkMonitor: NetworkMonitor
) : ViewModel() {

    val isLoggedIn: StateFlow<Boolean> = authRepository.isLoggedIn
    val session: StateFlow<SessionUser?> = authRepository.session
    val isOnline: StateFlow<Boolean> = networkMonitor.isOnline
    val isSyncConfigured: Boolean get() = syncManager.isConfigured
    val lastSyncAt: StateFlow<Long?> = syncManager.lastSyncAt

    val pendingCount: StateFlow<Int> = syncManager.observePendingCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val syncState: StateFlow<SyncState> = syncManager.observeState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SyncState.IDLE)

    fun requestSync() = syncManager.requestSync()

    fun logout() = authRepository.logout()

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val container = appContainer
                MainViewModel(container.authRepository, container.syncManager, container.networkMonitor)
            }
        }
    }
}
