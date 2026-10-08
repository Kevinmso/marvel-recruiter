package com.marvel.recruiter.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SeedPlanTest {

    private fun character(
        cvId: Long,
        powers: Int,
        appearances: Int,
        teams: List<TeamRef> = emptyList(),
        friends: Set<Long> = emptySet(),
        issues: Set<Long> = emptySet(),
    ) = CharacterInput(
        cvId = cvId, name = "c$cvId", realName = null, deck = null, aliases = null, imageUrl = null,
        powerCount = powers, issueAppearances = appearances,
        teams = teams, friendCvIds = friends, issueIds = issues,
    )

    private fun arc(cvId: Long, issues: Set<Long>) = ArcInput(cvId, "a$cvId", null, null, issues)

    private val chars = listOf(
        character(1, powers = 6, appearances = 337, teams = listOf(TeamRef(10, "X-Men")), friends = setOf(2), issues = setOf(100)),
        character(2, powers = 30, appearances = 16924, teams = listOf(TeamRef(10, "X-Men")), issues = setOf(200)),
    )
    private val arcs = listOf(
        arc(500, setOf(100, 101, 102, 103, 104, 105)),
        arc(600, (200L..321L).toSet()),
    )

    private val plan = buildSeedPlan(chars, arcs, seededAt = 42L)

    @Test
    fun `limites congelados vem dos dados do seed`() {
        assertEquals("6", plan.meta["pMin"])
        assertEquals("30", plan.meta["pMax"])
        assertEquals("337", plan.meta["xMin"])
        assertEquals("16924", plan.meta["xMax"])
        assertEquals("6", plan.meta["yMin"])
        assertEquals("122", plan.meta["yMax"])
        assertEquals("42", plan.meta["seededAt"])
    }

    @Test
    fun `atributos de personagem seguem as formulas`() {
        val weak = plan.characters.first { it.cvId == 1L }
        assertEquals(0.0, weak.power, 0.001)
        assertEquals(0.0, weak.veterancy, 0.001)
        assertEquals(20.0, weak.adjustedPower, 0.001)  // 0 + 0.4*50, piso 15 não afeta
    }

    @Test
    fun `sinergia so entre curados e uma vez por par`() {
        assertEquals(setOf(SynergyPair(1, 2)), plan.synergies)
    }

    @Test
    fun `dificuldade do arco usa y do arco`() {
        val big = plan.arcs.first { it.cvId == 600L }
        assertEquals(122, big.numIssues)
        assertEquals(130.0, big.difficulty, 0.001)
    }

    @Test
    fun `unlock so quando ha issue em comum`() {
        assertEquals(setOf(Unlock(1, 500), Unlock(2, 600)), plan.unlocks.toSet())
    }

    @Test
    fun `times sao distintos`() {
        assertEquals(listOf(TeamRef(10, "X-Men")), plan.teams)
        assertTrue(plan.characters.all { 10L in it.teamCvIds })
    }
}
