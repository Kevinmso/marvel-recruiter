package com.marvel.recruiter.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.marvel.recruiter.data.local.entity.MissionResultEntity
import com.marvel.recruiter.data.local.entity.MissionResultHeroEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MissionResultDao {

    @Insert
    suspend fun insertResult(result: MissionResultEntity): Long

    @Insert
    suspend fun insertHeroes(rows: List<MissionResultHeroEntity>)

    @Query("SELECT * FROM mission_result ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<MissionResultEntity>>

    @Query("SELECT * FROM mission_result ORDER BY createdAt DESC LIMIT 50")
    suspend fun recent(): List<MissionResultEntity>

    @Query("SELECT * FROM mission_result WHERE id = :id")
    suspend fun getById(id: Long): MissionResultEntity?

    @Query("SELECT characterCvId FROM mission_result_hero WHERE missionResultId = :id")
    suspend fun heroIdsOf(id: Long): List<Long>
}
