package com.marvel.recruiter.game

import org.junit.Assert.assertEquals
import org.junit.Test

class PlayerLevelTest {

    @Test
    fun `nivel 1 ate 199 xp e nivel 2 a partir de 200`() {
        assertEquals(1, levelOf(0))
        assertEquals(1, levelOf(199))
        assertEquals(2, levelOf(200))
    }

    @Test
    fun `pacote de nivel a cada 5 niveis`() {
        assertEquals(0, packsEarnedAt(4))
        assertEquals(1, packsEarnedAt(5))
        assertEquals(1, packsEarnedAt(9))
        assertEquals(2, packsEarnedAt(10))
    }

    @Test
    fun `pendentes descontam os pacotes ja abertos`() {
        val xpLevel6 = 5 * XP_PER_LEVEL // nivel 6
        assertEquals(1, pendingLevelPacks(xpLevel6, packsOpened = 0))
        assertEquals(0, pendingLevelPacks(xpLevel6, packsOpened = 1))
        assertEquals(0, pendingLevelPacks(0, packsOpened = 0))
    }
}
