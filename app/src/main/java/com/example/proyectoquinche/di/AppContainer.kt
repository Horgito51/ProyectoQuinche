package com.example.proyectoquinche.di

import android.content.Context
import com.example.proyectoquinche.data.local.database.AppDatabase
import com.example.proyectoquinche.data.local.session.SessionManager
import com.example.proyectoquinche.data.remote.SupabaseClient
import com.example.proyectoquinche.data.remote.SupabaseConfig
import com.example.proyectoquinche.data.repository.AuthRepositoryImpl
import com.example.proyectoquinche.data.repository.ComuneroRepositoryImpl
import com.example.proyectoquinche.data.repository.HogarRepositoryImpl
import com.example.proyectoquinche.domain.repository.AuthRepository
import com.example.proyectoquinche.domain.repository.ComuneroRepository
import com.example.proyectoquinche.domain.repository.HogarRepository
import com.example.proyectoquinche.domain.usecase.ValidarComuneroUseCase
import com.example.proyectoquinche.sync.SyncManager
import com.example.proyectoquinche.sync.handler.CensoSyncHandler
import com.example.proyectoquinche.sync.handler.PadronSyncHandler
import com.example.proyectoquinche.util.NetworkMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Dependencias compartidas de la app (inyección manual).
 * Los demás módulos agregan aquí sus repositorios y su SyncHandler.
 */
class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val database: AppDatabase = AppDatabase.getInstance(appContext)

    val networkMonitor = NetworkMonitor(appContext, appScope)

    val supabaseClient = SupabaseClient(SupabaseConfig.fromBuildConfig())

    private val padronSyncHandler = PadronSyncHandler(database, supabaseClient)

    val syncManager = SyncManager(
        context = appContext,
        client = supabaseClient,
        // El orden importa: el censo depende de hogar y persona.
        handlers = listOf(
            padronSyncHandler,
            CensoSyncHandler(database, supabaseClient)
        )
    )

    val sessionManager = SessionManager(appContext)

    val authRepository: AuthRepository = AuthRepositoryImpl(
        usuarioDao = database.usuarioDao(),
        sessionManager = sessionManager,
        networkMonitor = networkMonitor,
        remotoConfigurado = { supabaseClient.isConfigured },
        descargarUsuarios = padronSyncHandler::pull,
        scope = appScope
    )

    val comuneroRepository: ComuneroRepository = ComuneroRepositoryImpl(
        db = database,
        syncManager = syncManager,
        validar = ValidarComuneroUseCase()
    )

    val hogarRepository: HogarRepository = HogarRepositoryImpl(database)
}
