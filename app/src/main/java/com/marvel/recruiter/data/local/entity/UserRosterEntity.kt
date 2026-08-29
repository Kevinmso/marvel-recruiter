package com.marvel.recruiter.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "user_roster",
    foreignKeys = [
        ForeignKey(
            entity = CharacterEntity::class,
            parentColumns = ["cvId"],
            childColumns = ["characterCvId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["characterCvId"], unique = true)],
)
data class UserRosterEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val characterCvId: Long,
    val availableAt: Long = 0,
    val recruitedAt: Long,
)
