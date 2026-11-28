package com.example.proyectoquinche.data.remote

import com.example.proyectoquinche.BuildConfig

/** Credenciales del proyecto Supabase, definidas en local.properties. */
data class SupabaseConfig(
    val url: String,
    val anonKey: String
) {
    val isConfigured: Boolean get() = url.isNotBlank() && anonKey.isNotBlank()

    companion object {
        fun fromBuildConfig() = SupabaseConfig(
            url = BuildConfig.SUPABASE_URL.trimEnd('/'),
            anonKey = BuildConfig.SUPABASE_ANON_KEY
        )
    }
}
