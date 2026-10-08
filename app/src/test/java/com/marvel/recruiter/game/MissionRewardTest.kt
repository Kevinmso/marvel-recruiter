package com.marvel.recruiter.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MissionRewardTest {

    @Test
    fun `sucesso na dificuldade 50 rende 500 xp e 250 coins`() {
        assertEquals(MissionReward(xp = 500, coins = 250), missionReward(difficulty = 50.0, success = true))
    }

    @Test
    fun `falha na mesma missao rende 20 por cento`() {
        assertEquals(MissionReward(xp = 100, coins = 50), missionReward(difficulty = 50.0, success = false))
    }

    @Test
    fun `arredonda para o inteiro mais proximo`() {
        // 5 * 12,7 = 63,5 -> 64
        val reward = missionReward(difficulty = 12.7, success = true)
        assertEquals(127, reward.xp)
        assertEquals(64, reward.coins)
    }

    @Test
    fun `sucesso rende mais que falha`() {
        val win = missionReward(difficulty = 86.9, success = true)
        val loss = missionReward(difficulty = 86.9, success = false)
        assertTrue(win.xp > loss.xp)
        assertTrue(win.coins > loss.coins)
    }
}
