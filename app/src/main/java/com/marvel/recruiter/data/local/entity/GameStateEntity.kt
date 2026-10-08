package com.marvel.recruiter.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_state")
data class GameStateEntity(
    @PrimaryKey val id: Int = 1,
    val coinBalance: Int,
    val xpTotal: Int,
    val seedCompleted: Boolean,
    val packsOpened: Int = 0,
)
