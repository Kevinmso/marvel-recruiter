package com.marvel.recruiter.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.marvel.recruiter.data.local.entity.CharacterUnlockEntity
import com.marvel.recruiter.data.local.entity.StoryArcEntity

/** Catálogo de arcos (missões) + unlocks resolvidos no seed (RF-19). */
@Dao
interface StoryArcDao {

    @Insert
    suspend fun insertArcs(arcs: List<StoryArcEntity>)

    @Insert
    suspend fun insertUnlocks(rows: List<CharacterUnlockEntity>)

    @Query("SELECT * FROM story_arc")
    suspend fun getAll(): List<StoryArcEntity>
}
