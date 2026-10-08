package com.marvel.recruiter.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.marvel.recruiter.data.local.entity.SeedMetaEntity

/** RF-03: limites de normalização congelados no seed. */
@Dao
interface SeedMetaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rows: List<SeedMetaEntity>)

    @Query("DELETE FROM seed_meta")
    suspend fun clear()

    @Query("SELECT * FROM seed_meta")
    suspend fun getAll(): List<SeedMetaEntity>

    @Query("SELECT value FROM seed_meta WHERE key = :key")
    suspend fun getValue(key: String): String?
}
