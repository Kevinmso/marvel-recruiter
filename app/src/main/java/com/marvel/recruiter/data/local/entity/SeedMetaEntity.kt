package com.marvel.recruiter.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "seed_meta")
data class SeedMetaEntity(
    @PrimaryKey val key: String,
    val value: String
)
