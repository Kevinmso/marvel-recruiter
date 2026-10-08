package com.marvel.recruiter.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.marvel.recruiter.data.local.entity.CharacterEntity
import com.marvel.recruiter.data.local.entity.CharacterSynergyEntity
import com.marvel.recruiter.data.local.entity.CharacterTeamEntity
import com.marvel.recruiter.data.local.entity.TeamEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {

    @Insert
    suspend fun insertTeams(teams: List<TeamEntity>)

    @Insert
    suspend fun insertCharacters(characters: List<CharacterEntity>)

    @Insert
    suspend fun insertCharacterTeams(rows: List<CharacterTeamEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSynergies(rows: List<CharacterSynergyEntity>)

    @Query("SELECT * FROM character WHERE cvId = :cvId")
    suspend fun getByCvId(cvId: Long): CharacterEntity?

    @Query("SELECT * FROM character")
    suspend fun getAll(): List<CharacterEntity>

    @Query("SELECT * FROM character ORDER BY name")
    fun observeAll(): Flow<List<CharacterEntity>>

    @Query("DELETE FROM character_synergy")
    suspend fun clearSynergies()

    @Query("DELETE FROM character_team")
    suspend fun clearCharacterTeams()

    @Query("DELETE FROM character")
    suspend fun clearCharacters()

    @Query("DELETE FROM team")
    suspend fun clearTeams()

    @Query("SELECT * FROM team")
    suspend fun getAllTeams(): List<TeamEntity>

    @Query("SELECT * FROM character_team")
    suspend fun getAllCharacterTeams(): List<CharacterTeamEntity>

    @Query("SELECT * FROM character_synergy")
    suspend fun getAllSynergies(): List<CharacterSynergyEntity>
}
