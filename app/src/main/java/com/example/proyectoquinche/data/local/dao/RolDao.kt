package com.example.proyectoquinche.data.local.dao

import androidx.room.Dao
import androidx.room.Upsert
import com.example.proyectoquinche.data.local.entity.RolEntity

@Dao
interface RolDao {

    @Upsert
    suspend fun upsertAll(roles: List<RolEntity>)
}
