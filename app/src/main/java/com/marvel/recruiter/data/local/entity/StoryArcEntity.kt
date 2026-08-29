package com.marvel.recruiter.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "story_arc")
data class StoryArcEntity(
    @PrimaryKey val cvId: Long,
    val name: String,
    val deck: String?,
    val numIssues: Int,     // y limpo (pós-curadoria) — RF-11
    val difficulty: Double  // RF-11, congelada (C-08)
)
