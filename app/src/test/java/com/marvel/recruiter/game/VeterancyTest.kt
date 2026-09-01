package com.marvel.recruiter.game

import org.junit.Assert.assertEquals
import org.junit.Test

/** RF-05 (normalização logarítmica de Veterania) + RF-10 (guarda de divisão por zero). */
class VeterancyTest {

    // limites do conjunto de teste (constantes-normalizacao.md)
    private val xMin = 337
    private val xMax = 16924

    @Test
    fun `exemplo Colossus do spec`() {
        // 100 * (ln(7918) - ln(338)) / (ln(16925) - ln(338)) ≈ 80,6
        assertEquals(80.6, Normalization.veterancy(7917, xMin, xMax), 0.1)
    }

    @Test
    fun `x igual ao minimo da 0`() {
        assertEquals(0.0, Normalization.veterancy(xMin, xMin, xMax), 0.001)
    }

    @Test
    fun `x igual ao maximo da 100`() {
        assertEquals(100.0, Normalization.veterancy(xMax, xMin, xMax), 0.001)
    }

    @Test
    fun `RF-10 — xMin igual xMax retorna 50`() {
        assertEquals(50.0, Normalization.veterancy(5000, 1000, 1000), 0.001)
    }

    @Test
    fun `curva log — meio geometrico fica proximo de 50`() {
        // ponto cujo ln está a meio caminho entre ln(1) e ln(10001): x = 100 (ln(101)≈4,6; ln(10001)≈9,2)
        assertEquals(50.0, Normalization.veterancy(100, 0, 10000), 1.0)
    }
}
