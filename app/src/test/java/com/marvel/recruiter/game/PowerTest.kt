package com.marvel.recruiter.game

import org.junit.Assert.assertEquals
import org.junit.Test

/** RF-04 (normalização linear de Poder) + RF-10 (guarda de divisão por zero). */
class PowerTest {

    @Test
    fun `p no meio do intervalo — exemplo Colossus do spec`() {
        // 100 * (8 - 6) / (30 - 6) = 200 / 24 ≈ 8,333
        assertEquals(8.333, Normalization.power(p = 8, pMin = 6, pMax = 30), 0.01)
    }

    @Test
    fun `p igual ao minimo da 0`() {
        assertEquals(0.0, Normalization.power(p = 6, pMin = 6, pMax = 30), 0.001)
    }

    @Test
    fun `p igual ao maximo da 100`() {
        assertEquals(100.0, Normalization.power(p = 30, pMin = 6, pMax = 30), 0.001)
    }

    @Test
    fun `RF-10 — pMin igual pMax retorna 50 sem dividir por zero`() {
        assertEquals(50.0, Normalization.power(p = 15, pMin = 10, pMax = 10), 0.001)
    }

    @Test
    fun `resultado e proporcional — ponto a um quarto do intervalo`() {
        // pMin=0, pMax=40, p=10 → 25% do caminho → 25.0
        assertEquals(25.0, Normalization.power(p = 10, pMin = 0, pMax = 40), 0.001)
    }
}
