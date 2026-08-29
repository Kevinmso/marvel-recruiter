package com.marvel.recruiter.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "character_unlock",
    primaryKeys = ["characterCvId", "arcCvId"],
    foreignKeys = [
        ForeignKey(
            entity = CharacterEntity::class,
            parentColumns = ["cvId"],
            childColumns = ["characterCvId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = StoryArcEntity::class,
            parentColumns = ["cvId"],
            childColumns = ["arcCvId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("arcCvId")],
)
data class CharacterUnlockEntity(
    val characterCvId: Long,
    val arcCvId: Long,
)
