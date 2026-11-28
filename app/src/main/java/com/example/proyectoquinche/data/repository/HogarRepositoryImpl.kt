package com.example.proyectoquinche.data.repository

import com.example.proyectoquinche.data.local.database.AppDatabase
import com.example.proyectoquinche.data.local.relation.RespuestaCensoRow
import com.example.proyectoquinche.domain.model.CensoHogar
import com.example.proyectoquinche.domain.model.CensoRespuesta
import com.example.proyectoquinche.domain.model.CensoSeccion
import com.example.proyectoquinche.domain.model.FichaHogar
import com.example.proyectoquinche.domain.model.HogarResumen
import com.example.proyectoquinche.domain.model.HogarListado
import com.example.proyectoquinche.domain.repository.HogarRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class HogarRepositoryImpl(db: AppDatabase) : HogarRepository {

    private val hogarDao = db.hogarDao()
    private val integranteDao = db.integranteHogarDao()
    private val censoDao = db.censoDao()

    override fun observeHogares(): Flow<List<HogarResumen>> = hogarDao.observeResumenes()

    override fun observeListadoHogares(): Flow<List<HogarListado>> = hogarDao.observeListado()

    override fun observeFichaHogar(hogarId: String): Flow<FichaHogar?> = combine(
        hogarDao.observeResumen(hogarId),
        integranteDao.observeIntegrantes(hogarId),
        observeCenso(hogarId)
    ) { hogar, integrantes, censo ->
        hogar?.let { FichaHogar(it, integrantes, censo) }
    }

    private fun observeCenso(hogarId: String): Flow<CensoHogar?> =
        censoDao.observeUltimaFicha(hogarId).flatMapLatest { ficha ->
            if (ficha == null) {
                flowOf(null)
            } else {
                censoDao.observeRespuestas(ficha.fichaId).map { filas ->
                    CensoHogar(
                        fichaId = ficha.fichaId,
                        fechaRegistro = runCatching { LocalDate.parse(ficha.fechaRegistro.take(10)) }.getOrNull(),
                        consentimiento = ficha.consentimiento,
                        // Sin consentimiento válido no se exponen las respuestas.
                        secciones = if (ficha.consentimiento) agruparPorSeccion(filas) else emptyList()
                    )
                }
            }
        }

    private fun agruparPorSeccion(filas: List<RespuestaCensoRow>): List<CensoSeccion> =
        filas.groupBy { it.seccionId }.map { (seccionId, respuestas) ->
            CensoSeccion(
                seccionId = seccionId,
                titulo = respuestas.first().seccionTitulo,
                respuestas = respuestas.map { CensoRespuesta(it.preguntaId, it.enunciado, it.valorLegible()) }
            )
        }

    private fun RespuestaCensoRow.valorLegible(): String {
        val textoLibre = texto?.takeIf { it.isNotBlank() }
        val opcionesElegidas = opciones?.takeIf { it.isNotBlank() }
        return when {
            opcionesElegidas != null ->
                if (textoLibre != null) "$opcionesElegidas ($textoLibre)" else opcionesElegidas
            textoLibre != null -> textoLibre
            numero != null -> BigDecimal.valueOf(numero).stripTrailingZeros().toPlainString()
            booleano != null -> if (booleano) "Sí" else "No"
            else -> "Sin respuesta"
        }
    }
}
