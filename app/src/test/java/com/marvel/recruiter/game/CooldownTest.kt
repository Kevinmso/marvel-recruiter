package com.marvel.recruiter.game

import org.junit.Assert.assertEquals
import org.junit.Test

class CooldownTest {

    @Test
    fun `dificuldade 100 da 150 minutos`() {
        assertEquals(150.0, cooldownMinutes(100.0), 0.001)
    }

    @Test
    fun `dificuldade 10 da 15 minutos`() {
        assertEquals(15.0, cooldownMinutes(10.0), 0.001)
    }

    @Test
    fun `dificuldade 50 da 75 minutos`() {
        assertEquals(75.0, cooldownMinutes(50.0), 0.001)
    }

    @Test
    fun `availableAt soma o cooldown em millis ao momento da missao`() {
        val now = 1_000_000L
        assertEquals(now + 9_000_000L, availableAtAfterMission(difficulty = 100.0, now = now))
    }
}
