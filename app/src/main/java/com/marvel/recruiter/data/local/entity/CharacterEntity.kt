package com.marvel.recruiter.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "character")
data class CharacterEntity(
    @PrimaryKey val cvId: Long,
    val name: String,
    val realName: String?,
    val deck: String?,
    val aliases: String?,
    val imageUrl: String?,
    val numPowers: Int,
    val numAppearances: Int,
    val adjustedPower: Double,
    val veterancy: Double
)