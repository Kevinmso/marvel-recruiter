package com.marvel.recruiter.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.marvel.recruiter.data.local.entity.UserRosterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserRosterDao {

    @Insert
    suspend fun insert(row: UserRosterEntity): Long

    @Query("SELECT * FROM user_roster ORDER BY recruitedAt DESC")
    fun observeAll(): Flow<List<UserRosterEntity>>

    @Query("SELECT * FROM user_roster")
    suspend fun getAll(): List<UserRosterEntity>

    @Query("UPDATE user_roster SET availableAt = :availableAt WHERE characterCvId = :cvId")
    suspend fun setCooldown(cvId: Long, availableAt: Long)
}
