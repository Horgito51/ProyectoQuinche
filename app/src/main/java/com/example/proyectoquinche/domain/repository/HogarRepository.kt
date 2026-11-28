package com.example.proyectoquinche.domain.repository

import com.example.proyectoquinche.domain.model.FichaHogar
import com.example.proyectoquinche.domain.model.HogarResumen
import com.example.proyectoquinche.domain.model.HogarListado
import kotlinx.coroutines.flow.Flow

interface HogarRepository {

    fun observeHogares(): Flow<List<HogarResumen>>

    fun observeListadoHogares(): Flow<List<HogarListado>>

    fun observeFichaHogar(hogarId: String): Flow<FichaHogar?>
}
