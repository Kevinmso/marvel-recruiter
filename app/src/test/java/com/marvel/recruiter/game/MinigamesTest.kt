package com.marvel.recruiter.game

import org.junit.Assert.assertEquals
import org.junit.Test

class MinigamesTest {

    @Test
    fun `centro da barra da o maior bonus`() {
        assertEquals(0.10, coordinationBonus(0.5), 0.0001)
    }

    @Test
    fun `borda da zona da bonus menor`() {
        assertEquals(0.05, coordinationBonus(0.32), 0.0001)
    }

    @Test
    fun `fora da zona nao da bonus`() {
        assertEquals(0.0, coordinationBonus(0.05), 0.0001)
        assertEquals(0.0, coordinationBonus(0.95), 0.0001)
    }

    @Test
    fun `sequencia aumenta recompensa ate o limite`() {
        assertEquals(1.0, streakMultiplier(0), 0.0001)
        assertEquals(1.15, streakMultiplier(3), 0.0001)
        assertEquals(1.25, streakMultiplier(9), 0.0001)
    }
}
