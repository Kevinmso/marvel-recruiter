package com.marvel.recruiter.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.marvel.recruiter.data.local.entity.UserRosterEntity
import kotlinx.coroutines.flow.Flow

/** Heróis recrutados pelo jogador + cooldown (RF-15/16). */
@Dao
interface UserRosterDao {

    @Insert
    suspend fun insert(row: UserRosterEntity): Long

    @Query("SELECT * FROM user_roster ORDER BY recruitedAt DESC")
    fun observeAll(): Flow<List<UserRosterEntity>>

    @Query("SELECT * FROM user_roster")
    suspend fun getAll(): List<UserRosterEntity>

    /** Coloca um herói em cooldown até `availableAt` (epoch millis). */
    @Query("UPDATE user_roster SET availableAt = :availableAt WHERE characterCvId = :cvId")
    suspend fun setCooldown(cvId: Long, availableAt: Long)
}
