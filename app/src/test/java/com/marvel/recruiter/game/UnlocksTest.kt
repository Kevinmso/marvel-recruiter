package com.marvel.recruiter.game

import org.junit.Assert.assertEquals
import org.junit.Test

class UnlocksTest {

    @Test
    fun `personagem desbloqueia arco quando compartilha ao menos uma issue`() {
        val characters = mapOf(1L to setOf(10L, 11L), 2L to setOf(99L))
        val arcs = mapOf(100L to setOf(11L, 12L))

        assertEquals(listOf(Unlock(1L, 100L)), resolveUnlocks(characters, arcs))
    }

    @Test
    fun `sem issue em comum nao desbloqueia`() {
        assertEquals(emptyList<Unlock>(), resolveUnlocks(mapOf(1L to setOf(1L)), mapOf(2L to setOf(2L))))
    }

    @Test
    fun `um personagem pode desbloquear varios arcos`() {
        val characters = mapOf(1L to setOf(10L, 20L))
        val arcs = mapOf(100L to setOf(10L), 200L to setOf(20L), 300L to setOf(30L))

        assertEquals(
            setOf(Unlock(1L, 100L), Unlock(1L, 200L)),
            resolveUnlocks(characters, arcs).toSet(),
        )
    }
}
