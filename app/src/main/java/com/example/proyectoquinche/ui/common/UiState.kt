package com.example.proyectoquinche.ui.common

/**
 * Estado de pantalla expuesto por ViewModels.
 *
 * - Sobrevive a rotación / cambios de configuración mientras el ViewModel viva.
 * - Combinado con Room: al reabrir la app se puede volver a cargar desde la BD local.
 * - Para estado UI mínimo tras muerte de proceso, usar SavedStateHandle en el ViewModel.
 */
sealed interface UiState<out T> {
    data object Idle : UiState<Nothing>
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String, val cause: Throwable? = null) : UiState<Nothing>
}
