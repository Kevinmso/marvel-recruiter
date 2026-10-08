package com.marvel.recruiter.data.sync

import com.marvel.recruiter.data.remote.CuratedRoster
import com.marvel.recruiter.game.Unlock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** Valida o snapshot empacotado: contagens e nenhum arco sem desbloqueio. */
class LiveRosterTest {

    @Test
    fun `snapshot empacotado gera plano valido`() {
        val json = File("src/main/assets/roster_snapshot.json").readText()
        val plan = RosterFetcher(readSnapshot = { json }).fetchPlan()

        assertEquals(CuratedRoster.characterCvIds.size, plan.characters.size)
        assertEquals(CuratedRoster.arcCvIds.size, plan.arcs.size)
        val unlockedArcs: Set<Long> = plan.unlocks.map(Unlock::arcCvId).toSet()
        val orphans = plan.arcs.map { it.cvId }.filterNot { it in unlockedArcs }
        assertTrue("arcos sem nenhum unlock: $orphans", orphans.isEmpty())
    }
}
