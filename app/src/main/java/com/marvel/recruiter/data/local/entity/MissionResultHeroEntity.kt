package com.marvel.recruiter.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index


@Entity(
    tableName = "mission_result_hero",
    primaryKeys = ["missionResultId", "characterCvId"],
    foreignKeys = [
        ForeignKey(
            entity = MissionResultEntity::class,
            parentColumns = ["id"],
            childColumns = ["missionResultId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = CharacterEntity::class,
            parentColumns = ["cvId"],
            childColumns = ["characterCvId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("characterCvId")],
)
data class MissionResultHeroEntity(
    val missionResultId: Long,
    val characterCvId: Long,
)
