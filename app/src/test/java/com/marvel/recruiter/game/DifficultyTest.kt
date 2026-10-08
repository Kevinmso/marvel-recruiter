package com.marvel.recruiter.game

import org.junit.Assert.assertEquals
import org.junit.Test

class DifficultyTest {

    private val yMin = 6
    private val yMax = 122

    @Test
    fun `House of M (y=80) da 115,5`() {
        assertEquals(115.5, Normalization.difficulty(80, yMin, yMax), 0.1)
    }

    @Test
    fun `y igual ao minimo da o piso 30`() {
        assertEquals(30.0, Normalization.difficulty(yMin, yMin, yMax), 0.001)
    }

    @Test
    fun `y igual ao maximo da o teto 120`() {
        assertEquals(130.0, Normalization.difficulty(yMax, yMin, yMax), 0.001)
    }

    @Test
    fun `yMin igual yMax retorna 50`() {
        assertEquals(50.0, Normalization.difficulty(30, 10, 10), 0.001)
    }

    @Test
    fun `rotulo EASY abaixo de 45`() {
        assertEquals(DifficultyLabel.EASY, DifficultyLabel.of(59.9))
        assertEquals(DifficultyLabel.EASY, DifficultyLabel.of(10.0))
    }

    @Test
    fun `rotulo MEDIUM entre 45 e 70`() {
        assertEquals(DifficultyLabel.MEDIUM, DifficultyLabel.of(60.0))
        assertEquals(DifficultyLabel.MEDIUM, DifficultyLabel.of(99.9))
    }

    @Test
    fun `rotulo EPIC de 80 pra cima`() {
        assertEquals(DifficultyLabel.EPIC, DifficultyLabel.of(100.0))
        assertEquals(DifficultyLabel.EPIC, DifficultyLabel.of(100.0))
    }
}
