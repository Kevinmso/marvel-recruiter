package com.marvel.recruiter.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.marvel.recruiter.data.local.entity.CharacterEntity
import com.marvel.recruiter.data.local.entity.CharacterSynergyEntity
import com.marvel.recruiter.data.local.entity.CharacterTeamEntity
import com.marvel.recruiter.data.local.entity.TeamEntity

/** Catálogo de personagens + vínculos de equipe e sinergia. Inserts usados no seed (RF-01). */
@Dao
interface CharacterDao {

    @Insert
    suspend fun insertTeams(teams: List<TeamEntity>)

    @Insert
    suspend fun insertCharacters(characters: List<CharacterEntity>)

    @Insert
    suspend fun insertCharacterTeams(rows: List<CharacterTeamEntity>)

    /** Pares normalizados (low < high); IGNORE dedupica se o seed tentar o mesmo par 2x. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSynergies(rows: List<CharacterSynergyEntity>)

    @Query("SELECT * FROM character WHERE cvId = :cvId")
    suspend fun getByCvId(cvId: Long): CharacterEntity?

    @Query("SELECT * FROM character")
    suspend fun getAll(): List<CharacterEntity>
}
