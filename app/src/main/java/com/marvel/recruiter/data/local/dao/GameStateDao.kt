package com.marvel.recruiter.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.marvel.recruiter.data.local.entity.GameStateEntity
import kotlinx.coroutines.flow.Flow

/** Estado do jogo — linha única (id = 1). Criada no onCreate (RF-21). */
@Dao
interface GameStateDao {

    @Query("SELECT * FROM game_state WHERE id = 1")
    fun observe(): Flow<GameStateEntity?>

    @Query("SELECT * FROM game_state WHERE id = 1")
    suspend fun get(): GameStateEntity?

    @Upsert
    suspend fun upsert(state: GameStateEntity)

    @Query("UPDATE game_state SET seedCompleted = 1 WHERE id = 1")
    suspend fun markSeedCompleted()
}
