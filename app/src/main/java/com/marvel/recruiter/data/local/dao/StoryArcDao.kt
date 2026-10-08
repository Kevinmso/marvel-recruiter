package com.marvel.recruiter.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.marvel.recruiter.data.local.entity.CharacterUnlockEntity
import com.marvel.recruiter.data.local.entity.StoryArcEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StoryArcDao {

    @Insert
    suspend fun insertArcs(arcs: List<StoryArcEntity>)

    @Insert
    suspend fun insertUnlocks(rows: List<CharacterUnlockEntity>)

    @Query("DELETE FROM character_unlock")
    suspend fun clearUnlocks()

    @Query("DELETE FROM story_arc")
    suspend fun clearArcs()

    @Query("SELECT * FROM story_arc")
    suspend fun getAll(): List<StoryArcEntity>

    @Query("SELECT * FROM story_arc WHERE cvId = :cvId")
    suspend fun getByCvId(cvId: Long): StoryArcEntity?

    @Query("SELECT * FROM story_arc ORDER BY difficulty")
    fun observeAll(): Flow<List<StoryArcEntity>>

    @Query(
        "SELECT DISTINCT sa.* FROM story_arc sa " +
            "JOIN character_unlock cu ON cu.arcCvId = sa.cvId " +
            "JOIN user_roster ur ON ur.characterCvId = cu.characterCvId " +
            "ORDER BY sa.difficulty",
    )
    fun observeUnlocked(): Flow<List<StoryArcEntity>>

    @Query(
        "SELECT sa.* FROM story_arc sa " +
            "JOIN character_unlock cu ON cu.arcCvId = sa.cvId " +
            "WHERE cu.characterCvId = :characterCvId ORDER BY sa.difficulty",
    )
    suspend fun getArcsOf(characterCvId: Long): List<StoryArcEntity>

    @Query("SELECT * FROM character_unlock")
    suspend fun getAllUnlocks(): List<CharacterUnlockEntity>
}
