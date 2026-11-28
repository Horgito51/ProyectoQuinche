package com.example.proyectoquinche.ui.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyectoquinche.domain.enums.AppModule
import com.example.proyectoquinche.domain.model.ComunidadResumen
import com.example.proyectoquinche.domain.model.SessionUser
import com.example.proyectoquinche.domain.repository.AuthRepository
import com.example.proyectoquinche.domain.repository.ComuneroRepository
import com.example.proyectoquinche.sync.SyncManager
import com.example.proyectoquinche.ui.common.appContainer
import com.example.proyectoquinche.util.NetworkMonitor
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class DashboardUiState(
    val user: SessionUser? = null,
    val resumen: ComunidadResumen = ComunidadResumen(0, 0, 0, 0),
    /** Módulos visibles según el rol del usuario autenticado. */
    val modulos: List<AppModule> = emptyList(),
    val pendientes: Int = 0,
    val enLinea: Boolean = false
)

class DashboardViewModel(
    authRepository: AuthRepository,
    comuneroRepository: ComuneroRepository,
    syncManager: SyncManager,
    networkMonitor: NetworkMonitor
) : ViewModel() {

    val state: StateFlow<DashboardUiState> = combine(
        authRepository.session,
        comuneroRepository.observeResumenComunidad(),
        syncManager.observePendingCount(),
        networkMonitor.isOnline
    ) { user, resumen, pendientes, enLinea ->
        DashboardUiState(
            user = user,
            resumen = resumen,
            modulos = user?.role?.modulos.orEmpty(),
            pendientes = pendientes,
            enLinea = enLinea
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardUiState())

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val container = appContainer
                DashboardViewModel(
                    container.authRepository,
                    container.comuneroRepository,
                    container.syncManager,
                    container.networkMonitor
                )
            }
        }
    }
}
