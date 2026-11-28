package com.example.proyectoquinche.ui.common

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.proyectoquinche.QuincheApp
import com.example.proyectoquinche.di.AppContainer

/** Acceso al contenedor de dependencias desde un `viewModelFactory { initializer { ... } }`. */
val CreationExtras.appContainer: AppContainer
    get() = (requireNotNull(this[APPLICATION_KEY]) as QuincheApp).container
