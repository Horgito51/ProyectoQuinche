package com.example.proyectoquinche.data.repository

import androidx.room.withTransaction
import com.example.proyectoquinche.data.local.database.AppDatabase
import com.example.proyectoquinche.data.local.entity.ComuneroEntity
import com.example.proyectoquinche.data.local.entity.IntegranteHogarEntity
import com.example.proyectoquinche.data.local.entity.PersonaEntity
import com.example.proyectoquinche.domain.enums.SyncStatus
import com.example.proyectoquinche.domain.model.ComunidadResumen
import com.example.proyectoquinche.domain.model.ComuneroDetalle
import com.example.proyectoquinche.domain.model.ComuneroForm
import com.example.proyectoquinche.domain.model.ComuneroResumen
import com.example.proyectoquinche.domain.repository.ComuneroRepository
import com.example.proyectoquinche.domain.repository.GuardarComuneroResult
import com.example.proyectoquinche.domain.usecase.CampoComunero
import com.example.proyectoquinche.domain.usecase.ValidarComuneroUseCase
import com.example.proyectoquinche.sync.SyncManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.UUID

class ComuneroRepositoryImpl(
    private val db: AppDatabase,
    private val syncManager: SyncManager,
    private val validar: ValidarComuneroUseCase
) : ComuneroRepository {

    private val personaDao = db.personaDao()
    private val comuneroDao = db.comuneroDao()
    private val integranteDao = db.integranteHogarDao()
    private val hogarDao = db.hogarDao()

    override fun observeComuneros(): Flow<List<ComuneroResumen>> = comuneroDao.observeResumenes()

    override fun observeComunero(comuneroId: String): Flow<ComuneroDetalle?> =
        comuneroDao.observeDetalle(comuneroId)

    override fun observeResumenComunidad(): Flow<ComunidadResumen> = comuneroDao.observeResumenComunidad()

    override suspend fun siguienteCodigo(): String {
        val ultimo = comuneroDao.getCodigos()
            .mapNotNull { NUMERO_FINAL.find(it)?.value?.toIntOrNull() }
            .maxOrNull() ?: 0
        return PREFIJO_CODIGO + (ultimo + 1).toString().padStart(3, '0')
    }

    override suspend fun guardar(form: ComuneroForm): GuardarComuneroResult {
        val existente = form.comuneroId?.let { comuneroDao.getById(it) }
        val hogar = form.hogarId?.let { hogarDao.observeResumen(it).first() }
        val codigo = form.codigoComunero.trim()

        val errores = validar(form, hogar, existente?.personaId).toMutableMap()
        if (codigo.isNotEmpty() && comuneroDao.countByCodigo(codigo, existente?.comuneroId.orEmpty()) > 0) {
            errores[CampoComunero.CODIGO] = "El código $codigo ya está registrado"
        }
        if (errores.isNotEmpty()) return GuardarComuneroResult.Invalido(errores)

        val hogarId = requireNotNull(form.hogarId)
        val nombres = form.nombres.limpiar()
        val apellidos = form.apellidos.limpiar()
        val telefono = ValidarComuneroUseCase.normalizarTelefono(form.telefono).ifEmpty { null }
        val correo = form.correo.trim().lowercase().ifEmpty { null }
        val parentesco = form.parentesco?.ifBlank { null }
        val ahora = System.currentTimeMillis()

        val comuneroId = db.withTransaction {
            if (existente == null) {
                val personaId = UUID.randomUUID().toString()
                val nuevoId = UUID.randomUUID().toString()
                personaDao.insert(
                    PersonaEntity(personaId, nombres, apellidos, telefono, correo, updatedAt = ahora)
                )
                comuneroDao.insert(
                    ComuneroEntity(nuevoId, personaId, codigo, form.estado, updatedAt = ahora)
                )
                integranteDao.insert(
                    IntegranteHogarEntity(hogarId, personaId, parentesco, updatedAt = ahora)
                )
                nuevoId
            } else {
                val personaId = existente.personaId
                val persona = requireNotNull(personaDao.getById(personaId))
                val personaEditada = persona.copy(
                    nombres = nombres, apellidos = apellidos, telefono = telefono, correo = correo
                )
                if (personaEditada != persona) {
                    personaDao.update(personaEditada.copy(syncStatus = SyncStatus.PENDING, updatedAt = ahora))
                }

                val comuneroEditado = existente.copy(codigoComunero = codigo, estado = form.estado)
                if (comuneroEditado != existente) {
                    comuneroDao.update(comuneroEditado.copy(syncStatus = SyncStatus.PENDING, updatedAt = ahora))
                }

                val actuales = integranteDao.getByPersona(personaId)
                val sinCambios = actuales.size == 1 &&
                    actuales[0].hogarId == hogarId && actuales[0].parentesco == parentesco
                if (!sinCambios) {
                    integranteDao.deleteByPersona(personaId)
                    integranteDao.insert(
                        IntegranteHogarEntity(hogarId, personaId, parentesco, updatedAt = ahora)
                    )
                }
                existente.comuneroId
            }
        }

        syncManager.requestSync()
        return GuardarComuneroResult.Guardado(comuneroId)
    }

    private fun String.limpiar() = trim().replace(ESPACIOS, " ")

    private companion object {
        const val PREFIJO_CODIGO = "SJQ-"
        val NUMERO_FINAL = Regex("\\d+$")
        val ESPACIOS = Regex("\\s+")
    }
}
