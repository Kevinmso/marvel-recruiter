package com.marvel.recruiter.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import kotlin.random.Random

class PackTest {

    @Test
    fun `pool de um sempre retorna aquele heroi`() {
        val pool = listOf(PackCandidate(cvId = 42, veterancy = 0.0))
        repeat(100) { assertEquals(42L, drawFromPack(pool, Random(it))) }
    }

    @Test
    fun `mesma seed produz o mesmo sorteio`() {
        val pool = listOf(
            PackCandidate(1, 10.0),
            PackCandidate(2, 50.0),
            PackCandidate(3, 90.0),
        )
        assertEquals(drawFromPack(pool, Random(7)), drawFromPack(pool, Random(7)))
    }

    @Test
    fun `frequencia converge para os pesos (veterancy + 5)`() {
        val pool = listOf(
            PackCandidate(1, veterancy = 0.0),   // peso 5  → 0.05
            PackCandidate(2, veterancy = 15.0),  // peso 20 → 0.20
            PackCandidate(3, veterancy = 70.0),  // peso 75 → 0.75
        )
        val random = Random(123)
        val n = 200_000
        val counts = LongArray(4) // índice = cvId
        repeat(n) { counts[drawFromPack(pool, random).toInt()]++ }

        assertEquals(0.05, counts[1].toDouble() / n, 0.01)
        assertEquals(0.20, counts[2].toDouble() / n, 0.01)
        assertEquals(0.75, counts[3].toDouble() / n, 0.01)
    }

    @Test
    fun `veterancy igual para todos vira sorteio uniforme`() {
        val pool = (1L..4L).map { PackCandidate(it, veterancy = 30.0) }
        val random = Random(99)
        val n = 100_000
        val counts = LongArray(5)
        repeat(n) { counts[drawFromPack(pool, random).toInt()]++ }
        for (id in 1..4) {
            assertEquals(0.25, counts[id].toDouble() / n, 0.01)
        }
    }

    @Test
    fun `pool vazio lanca erro`() {
        assertThrows(IllegalArgumentException::class.java) {
            drawFromPack(emptyList(), Random(1))
        }
    }
}
