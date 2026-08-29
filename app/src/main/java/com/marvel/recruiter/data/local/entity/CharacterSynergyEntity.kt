package com.marvel.recruiter.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(tableName = "character_synergy",
    primaryKeys = ["lowCvId", "highCvId"],
    foreignKeys = [
        ForeignKey(
            entity = CharacterEntity::class,
            parentColumns = ["cvId"],
            childColumns = ["lowCvId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CharacterEntity::class,
            parentColumns = ["cvId"],
            childColumns = ["highCvId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("highCvId")])
data class CharacterSynergyEntity(
    val lowCvId: Long,
    val highCvId: Long
)
