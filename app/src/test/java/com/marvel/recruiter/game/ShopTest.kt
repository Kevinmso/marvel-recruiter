package com.marvel.recruiter.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class ShopTest {

    private fun hero(id: Long, strength: Double) = PackCandidate(id, veterancy = 50.0, strength = strength)

    private val pool = (1L..10L).map { hero(it, strength = 30.0) } +
        (11L..16L).map { hero(it, strength = 50.0) } +
        (17L..19L).map { hero(it, strength = 70.0) }

    @Test
    fun `vitrine tem 3 comuns, 3 raras e 1 lendaria`() {
        val picks = drawShop(pool, Random(1))
        assertEquals(3, picks.count { it.rarity == Rarity.COMMON })
        assertEquals(3, picks.count { it.rarity == Rarity.RARE })
        assertEquals(1, picks.count { it.rarity == Rarity.LEGENDARY })
    }

    @Test
    fun `cada oferta sai do pool da sua raridade`() {
        val picks = drawShop(pool, Random(7))
        picks.forEach { pick ->
            assertEquals(pick.rarity, rarityOf(pool.first { it.cvId == pick.cvId }.strength))
        }
    }

    @Test
    fun `preco segue a raridade`() {
        drawShop(pool, Random(3)).forEach { assertEquals(shopPrice(it.rarity), it.price) }
        assertEquals(150, shopPrice(Rarity.COMMON))
        assertEquals(300, shopPrice(Rarity.RARE))
        assertEquals(750, shopPrice(Rarity.LEGENDARY))
    }

    @Test
    fun `nao repete heroi na mesma vitrine`() {
        val picks = drawShop(pool, Random(11))
        assertEquals(picks.size, picks.map { it.cvId }.distinct().size)
    }

    @Test
    fun `secao sem herois mostra menos ofertas`() {
        val noLegendary = pool.filter { rarityOf(it.strength) != Rarity.LEGENDARY }
        val picks = drawShop(noLegendary, Random(5))
        assertEquals(0, picks.count { it.rarity == Rarity.LEGENDARY })
        assertEquals(6, picks.size)
    }

    @Test
    fun `mesma semente gera a mesma vitrine`() {
        assertEquals(drawShop(pool, Random(42)), drawShop(pool, Random(42)))
        assertTrue(drawShop(pool, Random(42)).isNotEmpty())
    }
}
