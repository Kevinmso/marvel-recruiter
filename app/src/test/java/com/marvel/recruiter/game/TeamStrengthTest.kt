package com.marvel.recruiter.game

import org.junit.Assert.assertEquals
import org.junit.Test

class TeamStrengthTest {

    private fun hero(
        cvId: Long,
        adjustedPower: Double = 0.0,
        veterancy: Double = 0.0,
        teams: Set<Long> = emptySet(),
    ) = HeroStats(cvId, adjustedPower, veterancy, teams)

    private fun pair(a: Long, b: Long) = SynergyPair(minOf(a, b), maxOf(a, b))

    @Test
    fun `sem pares de amizade da 0`() {
        val team = listOf(hero(1), hero(2), hero(3))
        assertEquals(0, synergyBonus(team, emptySet()))
    }

    @Test
    fun `um par de amizade da 10`() {
        val team = listOf(hero(1), hero(2), hero(3))
        assertEquals(4, synergyBonus(team, setOf(pair(1, 2))))
    }

    @Test
    fun `tres pares no time de 3 da 30`() {
        val team = listOf(hero(1), hero(2), hero(3))
        val synergies = setOf(pair(1, 2), pair(1, 3), pair(2, 3))
        assertEquals(12, synergyBonus(team, synergies))
    }

    @Test
    fun `par e reconhecido mesmo com herois em ordem invertida`() {
        val team = listOf(hero(3), hero(1), hero(2)) // fora de ordem
        assertEquals(4, synergyBonus(team, setOf(pair(1, 3))))
    }

    @Test
    fun `synergyBonus nao aplica o cap de 40 (isso e no teamStrength)`() {
        val team = (1L..5L).map { hero(it) }
        val allPairs = buildSet {
            for (a in 1L..5L) for (b in (a + 1)..5L) add(pair(a, b))
        }
        assertEquals(40, synergyBonus(team, allPairs))
    }

    @Test
    fun `ninguem compartilha equipe da 0`() {
        val team = listOf(
            hero(1, teams = setOf(10)),
            hero(2, teams = setOf(20)),
            hero(3, teams = setOf(30)),
        )
        assertEquals(0, factionBonus(team))
    }

    @Test
    fun `dois herois na mesma equipe da 6`() {
        val team = listOf(
            hero(1, teams = setOf(10, 11)),
            hero(2, teams = setOf(10)),
            hero(3, teams = setOf(30)),
        )
        assertEquals(2, factionBonus(team)) // equipe 10 tem 2 -> 2*3
    }

    @Test
    fun `tres herois na mesma equipe da 9`() {
        val team = listOf(
            hero(1, teams = setOf(10)),
            hero(2, teams = setOf(10)),
            hero(3, teams = setOf(10)),
        )
        assertEquals(3, factionBonus(team))
    }

    @Test
    fun `media base sem bonus`() {
        val team = listOf(
            hero(1, adjustedPower = 100.0, veterancy = 0.0),
            hero(2, adjustedPower = 0.0, veterancy = 100.0),
            hero(3, adjustedPower = 50.0, veterancy = 50.0),
        )
        // média de (60, 40, 50)
        assertEquals(50.0, teamStrength(team, emptySet()), 0.001)
    }

    @Test
    fun `media base mais sinergia mais faccao`() {
        val team = listOf(
            hero(1, adjustedPower = 100.0, teams = setOf(10)),
            hero(2, adjustedPower = 100.0, teams = setOf(10)),
            hero(3, adjustedPower = 100.0, teams = setOf(10)),
        )
        // base = média de 3 × 60 = 60 ; synergy = 10 (1 par) ; faction = 9 (3 na equipe 10)
        assertEquals(67.0, teamStrength(team, setOf(pair(1, 2))), 0.001)
    }

    @Test
    fun `sinergia e limitada a 40`() {
        val team = (1L..5L).map { hero(it) } // base 0, sem equipe
        val allPairs = buildSet {
            for (a in 1L..5L) for (b in (a + 1)..5L) add(pair(a, b))
        }
        // synergyBonus = 100, mas min(100, 40) = 40
        assertEquals(12.0, teamStrength(team, allPairs), 0.001)
    }
}
