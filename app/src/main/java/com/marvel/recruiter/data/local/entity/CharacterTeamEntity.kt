package com.marvel.recruiter.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "character_team",
    primaryKeys = ["characterCvId", "teamCvId"],
    foreignKeys = [
        ForeignKey(
            entity = CharacterEntity::class,
            parentColumns = ["cvId"],
            childColumns = ["characterCvId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = TeamEntity::class,
            parentColumns = ["cvId"],
            childColumns = ["teamCvId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("teamCvId")],
)
data class CharacterTeamEntity(
    val characterCvId: Long,
    val teamCvId: Long,
)