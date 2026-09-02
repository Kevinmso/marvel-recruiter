package com.marvel.recruiter.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class MissionTest {

    @Test
    fun `time igual a dificuldade da 50 por cento`() {
        // (100 - 100) / 100 * 1 + 0.5 = 0.5
        assertEquals(0.5, successChance(teamStrength = 100.0, difficulty = 100.0), 0.001)
    }

    @Test
    fun `time mais forte que a dificuldade passa de 50 por cento`() {
        // (150 - 100) / 100 + 0.5 = 1.0 -> clamp -> 0.95
        assertEquals(0.95, successChance(teamStrength = 150.0, difficulty = 100.0), 0.001)
    }

    @Test
    fun `time mais fraco fica abaixo de 50 por cento`() {
        // (60 - 100) / 100 + 0.5 = 0.1
        assertEquals(0.1, successChance(teamStrength = 60.0, difficulty = 100.0), 0.001)
    }

    @Test
    fun `clamp inferior em 0,05`() {
        assertEquals(0.05, successChance(teamStrength = 0.0, difficulty = 100.0), 0.001)
    }

    @Test
    fun `clamp superior em 0,95`() {
        assertEquals(0.95, successChance(teamStrength = 9999.0, difficulty = 50.0), 0.001)
    }

    @Test
    fun `chance 1 sempre sucesso`() {
        val random = Random(1)
        repeat(1000) { assertTrue(resolveMission(chance = 1.0, random = random)) }
    }

    @Test
    fun `chance 0 sempre falha`() {
        val random = Random(1)
        repeat(1000) { assertFalse(resolveMission(chance = 0.0, random = random)) }
    }

    @Test
    fun `mesma seed produz o mesmo resultado (determinismo)`() {
        val a = resolveMission(0.5, Random(12345))
        val b = resolveMission(0.5, Random(12345))
        assertEquals(a, b)
    }

    @Test
    fun `frequencia de sucesso converge para a chance`() {
        val random = Random(42)
        val n = 100_000
        val successes = (1..n).count { resolveMission(chance = 0.73, random = random) }
        assertEquals(0.73, successes.toDouble() / n, 0.01)
    }
}
