package com.marvel.recruiter.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

/**
 * Cria a linha única de `game_state` com o estado inicial (RF-21) no primeiro boot do banco.
 * `seedCompleted = 0` → o app roda o seed (RF-01) na sequência.
 */
object InitialStateCallback : RoomDatabase.Callback() {
    override fun onCreate(connection: SQLiteConnection) {
        connection.execSQL(
            "INSERT INTO game_state (id, coinBalance, xpTotal, seedCompleted, packsOpened) VALUES (1, 300, 0, 0, 0)",
        )
    }
}
