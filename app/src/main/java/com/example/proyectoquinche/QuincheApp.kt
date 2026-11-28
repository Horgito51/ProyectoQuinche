package com.example.proyectoquinche

import android.app.Application
import com.example.proyectoquinche.di.AppContainer

class QuincheApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        container.syncManager.schedulePeriodicSync()
    }
}
