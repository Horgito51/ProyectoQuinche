package com.example.proyectoquinche.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.proyectoquinche.data.local.entity.UsuarioEntity
import com.example.proyectoquinche.data.local.relation.CuentaUsuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Query(
        """
        SELECT u.usuario_id AS usuarioId, u.persona_id AS personaId, u.nombre_usuario AS nombreUsuario,
               u.clave_hash AS claveHash, u.activo, r.nombre AS rolNombre, p.nombres, p.apellidos
        FROM usuario u
        JOIN rol r ON r.rol_id = u.rol_id
        JOIN persona p ON p.persona_id = u.persona_id
        WHERE u.nombre_usuario = :nombreUsuario COLLATE NOCASE
        LIMIT 1
        """
    )
    suspend fun getCuenta(nombreUsuario: String): CuentaUsuario?

    @Query(
        """
        SELECT u.usuario_id AS usuarioId, u.persona_id AS personaId, u.nombre_usuario AS nombreUsuario,
               u.clave_hash AS claveHash, u.activo, r.nombre AS rolNombre, p.nombres, p.apellidos
        FROM usuario u
        JOIN rol r ON r.rol_id = u.rol_id
        JOIN persona p ON p.persona_id = u.persona_id
        WHERE u.usuario_id = :usuarioId
        LIMIT 1
        """
    )
    fun observeCuenta(usuarioId: String): Flow<CuentaUsuario?>

    @Query("SELECT COUNT(*) FROM usuario")
    suspend fun count(): Int

    @Upsert
    suspend fun upsertAll(usuarios: List<UsuarioEntity>)
}
