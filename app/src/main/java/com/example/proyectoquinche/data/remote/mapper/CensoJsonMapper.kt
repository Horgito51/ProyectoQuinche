package com.example.proyectoquinche.data.remote.mapper

import com.example.proyectoquinche.data.local.entity.FichaCensalEntity
import com.example.proyectoquinche.data.local.entity.OpcionPreguntaEntity
import com.example.proyectoquinche.data.local.entity.PreguntaCensoEntity
import com.example.proyectoquinche.data.local.entity.RespuestaCensoEntity
import com.example.proyectoquinche.data.local.entity.RespuestaOpcionEntity
import com.example.proyectoquinche.data.local.entity.SeccionCensoEntity
import org.json.JSONObject

fun JSONObject.toSeccionCensoEntity() = SeccionCensoEntity(
    seccionId = getInt("seccion_id"),
    titulo = getString("titulo"),
    orden = getInt("orden")
)

fun JSONObject.toPreguntaCensoEntity() = PreguntaCensoEntity(
    preguntaId = getInt("pregunta_id"),
    seccionId = getInt("seccion_id"),
    enunciado = getString("enunciado"),
    tipoRespuesta = getString("tipo_respuesta"),
    orden = getInt("orden")
)

fun JSONObject.toOpcionPreguntaEntity() = OpcionPreguntaEntity(
    opcionId = getInt("opcion_id"),
    preguntaId = getInt("pregunta_id"),
    etiqueta = getString("etiqueta"),
    orden = getInt("orden")
)

fun JSONObject.toFichaCensalEntity() = FichaCensalEntity(
    fichaId = getString("ficha_id"),
    hogarId = getString("hogar_id"),
    informanteId = getString("informante_id"),
    fechaRegistro = getString("fecha_registro"),
    consentimiento = getBoolean("consentimiento"),
    fechaConsentimiento = getString("fecha_consentimiento"),
    versionFormulario = getString("version_formulario")
)

fun JSONObject.toRespuestaCensoEntity() = RespuestaCensoEntity(
    respuestaId = getString("respuesta_id"),
    fichaId = getString("ficha_id"),
    preguntaId = getInt("pregunta_id"),
    texto = stringOrNull("texto"),
    numero = doubleOrNull("numero"),
    booleano = booleanOrNull("booleano")
)

fun JSONObject.toRespuestaOpcionEntity() = RespuestaOpcionEntity(
    respuestaId = getString("respuesta_id"),
    opcionId = getInt("opcion_id")
)
