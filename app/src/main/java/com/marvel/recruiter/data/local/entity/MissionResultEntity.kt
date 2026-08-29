package com.marvel.recruiter.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "mission_result",
    foreignKeys = [
        ForeignKey(
            entity = StoryArcEntity::class,
            parentColumns = ["cvId"],
            childColumns = ["arcCvId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("arcCvId")],
)
data class MissionResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val arcCvId: Long,
    val teamStrength: Double,
    val difficulty: Double,
    val chance: Double,
    val rollValue: Double,
    val success: Boolean,
    val xpEarned: Int,
    val coinsEarned: Int,
    val createdAt: Long,
)
