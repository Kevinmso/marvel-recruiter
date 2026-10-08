package com.marvel.recruiter.game

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.random.Random

class PackRarityTest {

    @Test
    fun `faixas de raridade pela forca`() {
        assertEquals(Rarity.COMMON, rarityOf(44.9))
        assertEquals(Rarity.RARE, rarityOf(45.0))
        assertEquals(Rarity.RARE, rarityOf(64.9))
        assertEquals(Rarity.LEGENDARY, rarityOf(65.0))
    }

    @Test
    fun `herois fortes saem bem menos que fracos com mesma veterania`() {
        val pool = listOf(
            PackCandidate(1, veterancy = 50.0, strength = 30.0),
            PackCandidate(2, veterancy = 50.0, strength = 80.0),
        )
        val random = Random(42)
        val n = 100_000
        val counts = LongArray(3)
        repeat(n) { counts[drawFromPack(pool, random).toInt()]++ }

        val ratio = counts[2].toDouble() / counts[1]
        assertEquals(0.12, ratio, 0.01)
    }
}
