package com.example.proyectoquinche.data.remote.mapper

import com.example.proyectoquinche.data.local.entity.ComuneroEntity
import com.example.proyectoquinche.data.local.entity.HogarEntity
import com.example.proyectoquinche.data.local.entity.IntegranteHogarEntity
import com.example.proyectoquinche.data.local.entity.PersonaEntity
import com.example.proyectoquinche.data.local.entity.RolEntity
import com.example.proyectoquinche.data.local.entity.UsuarioEntity
import com.example.proyectoquinche.domain.enums.EstadoComunero
import com.example.proyectoquinche.domain.enums.SyncStatus
import org.json.JSONObject

/** Conversión entre filas JSON de Supabase (nombres del SQL) y entidades Room. */

fun JSONObject.toPersonaEntity() = PersonaEntity(
    personaId = getString("persona_id"),
    nombres = getString("nombres"),
    apellidos = getString("apellidos"),
    telefono = stringOrNull("telefono"),
    correo = stringOrNull("correo"),
    syncStatus = SyncStatus.SYNCED,
    remoteId = getString("persona_id")
)

fun PersonaEntity.toJson(): JSONObject = JSONObject()
    .put("persona_id", personaId)
    .put("nombres", nombres)
    .put("apellidos", apellidos)
    .put("telefono", telefono.orJsonNull())
    .put("correo", correo.orJsonNull())

fun JSONObject.toHogarEntity() = HogarEntity(
    hogarId = getString("hogar_id"),
    numeroCasa = getString("numero_casa"),
    sector = getString("sector"),
    direccion = getString("direccion"),
    referencia = stringOrNull("referencia"),
    responsableId = getString("responsable_id")
)

fun JSONObject.toIntegranteHogarEntity() = IntegranteHogarEntity(
    hogarId = getString("hogar_id"),
    personaId = getString("persona_id"),
    parentesco = stringOrNull("parentesco"),
    syncStatus = SyncStatus.SYNCED,
    remoteId = "${getString("hogar_id")}:${getString("persona_id")}"
)

fun IntegranteHogarEntity.toJson(): JSONObject = JSONObject()
    .put("hogar_id", hogarId)
    .put("persona_id", personaId)
    .put("parentesco", parentesco.orJsonNull())

fun JSONObject.toComuneroEntity() = ComuneroEntity(
    comuneroId = getString("comunero_id"),
    personaId = getString("persona_id"),
    codigoComunero = stringOrNull("codigo_comunero"),
    estado = EstadoComunero.from(getString("estado")),
    syncStatus = SyncStatus.SYNCED,
    remoteId = getString("comunero_id")
)

fun ComuneroEntity.toJson(): JSONObject = JSONObject()
    .put("comunero_id", comuneroId)
    .put("persona_id", personaId)
    .put("codigo_comunero", codigoComunero.orJsonNull())
    .put("estado", estado.name)

fun JSONObject.toRolEntity() = RolEntity(
    rolId = getInt("rol_id"),
    nombre = getString("nombre")
)

fun JSONObject.toUsuarioEntity() = UsuarioEntity(
    usuarioId = getString("usuario_id"),
    personaId = getString("persona_id"),
    rolId = getInt("rol_id"),
    nombreUsuario = getString("nombre_usuario"),
    claveHash = getString("clave_hash"),
    activo = getBoolean("activo")
)
