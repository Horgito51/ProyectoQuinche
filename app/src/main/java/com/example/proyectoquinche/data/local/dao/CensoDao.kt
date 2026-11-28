package com.example.proyectoquinche.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.proyectoquinche.data.local.entity.FichaCensalEntity
import com.example.proyectoquinche.data.local.entity.OpcionPreguntaEntity
import com.example.proyectoquinche.data.local.entity.PreguntaCensoEntity
import com.example.proyectoquinche.data.local.entity.RespuestaCensoEntity
import com.example.proyectoquinche.data.local.entity.RespuestaOpcionEntity
import com.example.proyectoquinche.data.local.entity.SeccionCensoEntity
import com.example.proyectoquinche.data.local.relation.RespuestaCensoRow
import kotlinx.coroutines.flow.Flow

/** Ficha del censo del hogar: se descarga del servidor y se consulta en modo lectura. */
@Dao
interface CensoDao {

    @Query("SELECT * FROM ficha_censal WHERE hogar_id = :hogarId ORDER BY fecha_registro DESC LIMIT 1")
    fun observeUltimaFicha(hogarId: String): Flow<FichaCensalEntity?>

    @Query(
        """
        SELECT s.seccion_id AS seccionId, s.titulo AS seccionTitulo, q.pregunta_id AS preguntaId,
               q.enunciado, r.texto, r.numero, r.booleano,
               (SELECT group_concat(etiqueta, ', ') FROM (
                    SELECT o.etiqueta FROM respuesta_opcion ro
                    JOIN opcion_pregunta o ON o.opcion_id = ro.opcion_id
                    WHERE ro.respuesta_id = r.respuesta_id
                    ORDER BY o.orden
               )) AS opciones
        FROM respuesta_censo r
        JOIN pregunta_censo q ON q.pregunta_id = r.pregunta_id
        JOIN seccion_censo s ON s.seccion_id = q.seccion_id
        WHERE r.ficha_id = :fichaId
        ORDER BY s.orden, q.orden
        """
    )
    fun observeRespuestas(fichaId: String): Flow<List<RespuestaCensoRow>>

    @Upsert
    suspend fun upsertSecciones(secciones: List<SeccionCensoEntity>)

    @Upsert
    suspend fun upsertPreguntas(preguntas: List<PreguntaCensoEntity>)

    @Upsert
    suspend fun upsertOpciones(opciones: List<OpcionPreguntaEntity>)

    @Upsert
    suspend fun upsertFichas(fichas: List<FichaCensalEntity>)

    @Upsert
    suspend fun upsertRespuestas(respuestas: List<RespuestaCensoEntity>)

    @Upsert
    suspend fun upsertRespuestaOpciones(respuestaOpciones: List<RespuestaOpcionEntity>)
}
