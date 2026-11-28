package com.example.proyectoquinche.sync.handler

import androidx.room.withTransaction
import com.example.proyectoquinche.data.local.database.AppDatabase
import com.example.proyectoquinche.data.remote.SupabaseClient
import com.example.proyectoquinche.data.remote.mapper.toFichaCensalEntity
import com.example.proyectoquinche.data.remote.mapper.toOpcionPreguntaEntity
import com.example.proyectoquinche.data.remote.mapper.toPreguntaCensoEntity
import com.example.proyectoquinche.data.remote.mapper.toRespuestaCensoEntity
import com.example.proyectoquinche.data.remote.mapper.toRespuestaOpcionEntity
import com.example.proyectoquinche.data.remote.mapper.toSeccionCensoEntity
import com.example.proyectoquinche.sync.SyncHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Bloque 2 del esquema: ficha del censo del hogar. Es información ya recolectada,
 * por eso solo se descarga (la app no la modifica). Debe registrarse después de
 * [PadronSyncHandler] porque depende de hogar y persona.
 */
class CensoSyncHandler(
    private val db: AppDatabase,
    private val client: SupabaseClient
) : SyncHandler {

    override fun observePendingCount(): Flow<Int> = flowOf(0)

    override suspend fun push() = Unit

    override suspend fun pull() {
        val secciones = client.selectAll("seccion_censo", orderBy = "seccion_id").map { it.toSeccionCensoEntity() }
        val preguntas = client.selectAll("pregunta_censo", orderBy = "pregunta_id").map { it.toPreguntaCensoEntity() }
        val opciones = client.selectAll("opcion_pregunta", orderBy = "opcion_id").map { it.toOpcionPreguntaEntity() }
        val fichas = client.selectAll("ficha_censal", orderBy = "ficha_id").map { it.toFichaCensalEntity() }
        val respuestas = client.selectAll("respuesta_censo", orderBy = "respuesta_id").map { it.toRespuestaCensoEntity() }
        val respuestaOpciones = client.selectAll("respuesta_opcion", orderBy = "respuesta_id,opcion_id")
            .map { it.toRespuestaOpcionEntity() }

        val censoDao = db.censoDao()
        db.withTransaction {
            censoDao.upsertSecciones(secciones)
            censoDao.upsertPreguntas(preguntas)
            censoDao.upsertOpciones(opciones)
            censoDao.upsertFichas(fichas)
            censoDao.upsertRespuestas(respuestas)
            censoDao.upsertRespuestaOpciones(respuestaOpciones)
        }
    }
}
