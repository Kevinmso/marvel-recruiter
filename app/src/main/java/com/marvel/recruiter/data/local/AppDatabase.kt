package com.marvel.recruiter.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.marvel.recruiter.data.local.dao.CharacterDao
import com.marvel.recruiter.data.local.dao.GameStateDao
import com.marvel.recruiter.data.local.dao.MissionResultDao
import com.marvel.recruiter.data.local.dao.SeedMetaDao
import com.marvel.recruiter.data.local.dao.StoryArcDao
import com.marvel.recruiter.data.local.dao.UserRosterDao
import com.marvel.recruiter.data.local.entity.CharacterEntity
import com.marvel.recruiter.data.local.entity.CharacterSynergyEntity
import com.marvel.recruiter.data.local.entity.CharacterTeamEntity
import com.marvel.recruiter.data.local.entity.CharacterUnlockEntity
import com.marvel.recruiter.data.local.entity.GameStateEntity
import com.marvel.recruiter.data.local.entity.MissionResultEntity
import com.marvel.recruiter.data.local.entity.MissionResultHeroEntity
import com.marvel.recruiter.data.local.entity.SeedMetaEntity
import com.marvel.recruiter.data.local.entity.StoryArcEntity
import com.marvel.recruiter.data.local.entity.TeamEntity
import com.marvel.recruiter.data.local.entity.UserRosterEntity

@Database(
    entities = [
        TeamEntity::class,
        CharacterEntity::class,
        CharacterTeamEntity::class,
        CharacterSynergyEntity::class,
        StoryArcEntity::class,
        CharacterUnlockEntity::class,
        SeedMetaEntity::class,
        GameStateEntity::class,
        UserRosterEntity::class,
        MissionResultEntity::class,
        MissionResultHeroEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun characterDao(): CharacterDao
    abstract fun storyArcDao(): StoryArcDao
    abstract fun gameStateDao(): GameStateDao
    abstract fun userRosterDao(): UserRosterDao
    abstract fun missionResultDao(): MissionResultDao
    abstract fun seedMetaDao(): SeedMetaDao

    companion object {
        const val NAME = "marvel_recruiter.db"
    }
}
