package com.marvel.recruiter.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.marvel.recruiter.data.local.entity.MissionResultEntity
import com.marvel.recruiter.data.local.entity.MissionResultHeroEntity
import kotlinx.coroutines.flow.Flow

/** Histórico de execuções de missão (RF-22 — repetível). */
@Dao
interface MissionResultDao {

    @Insert
    suspend fun insertResult(result: MissionResultEntity): Long

    @Insert
    suspend fun insertHeroes(rows: List<MissionResultHeroEntity>)

    /** Grava o resultado + os heróis participantes atomicamente. */
    @Transaction
    suspend fun record(result: MissionResultEntity, heroCvIds: List<Long>) {
        val id = insertResult(result)
        insertHeroes(heroCvIds.map { MissionResultHeroEntity(id, it) })
    }

    @Query("SELECT * FROM mission_result ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<MissionResultEntity>>
}
