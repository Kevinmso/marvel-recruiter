package com.marvel.recruiter.game

import org.junit.Assert.assertEquals
import org.junit.Test

/** RF-11: normalização logarítmica de Dificuldade (escala 10–100) + rótulo. */
class DifficultyTest {

    // limites do conjunto de teste (constantes-normalizacao.md)
    private val yMin = 6
    private val yMax = 122

    @Test
    fun `exemplo House of M do spec`() {
        // 10 + 90 * (ln(81) - ln(7)) / (ln(123) - ln(7)) ≈ 86,9
        assertEquals(86.9, Normalization.difficulty(80, yMin, yMax), 0.1)
    }

    @Test
    fun `y igual ao minimo da o piso 10`() {
        assertEquals(10.0, Normalization.difficulty(yMin, yMin, yMax), 0.001)
    }

    @Test
    fun `y igual ao maximo da o teto 100`() {
        assertEquals(100.0, Normalization.difficulty(yMax, yMin, yMax), 0.001)
    }

    @Test
    fun `RF-10 — yMin igual yMax retorna 50`() {
        assertEquals(50.0, Normalization.difficulty(30, 10, 10), 0.001)
    }

    // ---- rótulo (RF-11) ----

    @Test
    fun `rotulo EASY abaixo de 40`() {
        assertEquals(DifficultyLabel.EASY, DifficultyLabel.of(39.9))
        assertEquals(DifficultyLabel.EASY, DifficultyLabel.of(10.0))
    }

    @Test
    fun `rotulo MEDIUM entre 40 e 70`() {
        assertEquals(DifficultyLabel.MEDIUM, DifficultyLabel.of(40.0))
        assertEquals(DifficultyLabel.MEDIUM, DifficultyLabel.of(69.9))
    }

    @Test
    fun `rotulo EPIC de 70 pra cima`() {
        assertEquals(DifficultyLabel.EPIC, DifficultyLabel.of(70.0))
        assertEquals(DifficultyLabel.EPIC, DifficultyLabel.of(100.0))
    }
}
