package com.example.proyectoquinche.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.proyectoquinche.data.local.entity.HogarEntity
import com.example.proyectoquinche.domain.model.HogarResumen
import com.example.proyectoquinche.domain.model.HogarListado
import kotlinx.coroutines.flow.Flow

@Dao
interface HogarDao {

    @Query(
        """
        SELECT h.hogar_id AS hogarId, h.numero_casa AS numeroCasa, h.sector, h.direccion,
               p.nombres AS responsableNombres, p.apellidos AS responsableApellidos,
               (SELECT COUNT(*) FROM integrante_hogar i WHERE i.hogar_id = h.hogar_id) AS integrantes,
               EXISTS(SELECT 1 FROM ficha_censal f WHERE f.hogar_id = h.hogar_id) AS tieneCenso
        FROM hogar h
        JOIN persona p ON p.persona_id = h.responsable_id
        ORDER BY h.numero_casa
        """
    )
    fun observeListado(): Flow<List<HogarListado>>

    @Query(
        """
        SELECT h.hogar_id AS hogarId, h.numero_casa AS numeroCasa, h.sector, h.direccion,
               h.referencia, h.responsable_id AS responsableId,
               p.nombres AS responsableNombres, p.apellidos AS responsableApellidos
        FROM hogar h
        JOIN persona p ON p.persona_id = h.responsable_id
        ORDER BY h.numero_casa
        """
    )
    fun observeResumenes(): Flow<List<HogarResumen>>

    @Query(
        """
        SELECT h.hogar_id AS hogarId, h.numero_casa AS numeroCasa, h.sector, h.direccion,
               h.referencia, h.responsable_id AS responsableId,
               p.nombres AS responsableNombres, p.apellidos AS responsableApellidos
        FROM hogar h
        JOIN persona p ON p.persona_id = h.responsable_id
        WHERE h.hogar_id = :hogarId
        LIMIT 1
        """
    )
    fun observeResumen(hogarId: String): Flow<HogarResumen?>

    @Query("SELECT * FROM hogar WHERE hogar_id = :hogarId LIMIT 1")
    suspend fun getById(hogarId: String): HogarEntity?

    @Upsert
    suspend fun upsertAll(hogares: List<HogarEntity>)
}
