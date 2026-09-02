package com.marvel.recruiter.game

import kotlin.math.roundToInt
import kotlin.random.Random

// constantes de calibração (T-25)
const val SUCCESS_CHANCE_K = 1.0
const val REWARD_XP_ALPHA = 10.0
const val REWARD_COINS_BETA = 5.0
const val COOLDOWN_MINUTES_PER_10_DIFFICULTY = 15.0

fun successChance(teamStrength: Double, difficulty: Double): Double {
    val raw = (teamStrength - difficulty) / difficulty * SUCCESS_CHANCE_K + 0.5
    return raw.coerceIn(0.05, 0.95)
}

fun resolveMission(chance: Double, random: Random): Boolean {
    return random.nextInt(100) < chance * 100
}

data class MissionReward(val xp: Int, val coins: Int)

fun missionReward(difficulty: Double, success: Boolean): MissionReward {
    val multiplier = if (success) 1.0 else 0.2
    return MissionReward(
        xp = (REWARD_XP_ALPHA * difficulty * multiplier).roundToInt(),
        coins = (REWARD_COINS_BETA * difficulty * multiplier).roundToInt(),
    )
}

fun cooldownMinutes(difficulty: Double): Double {
    return COOLDOWN_MINUTES_PER_10_DIFFICULTY * (difficulty / 10.0)
}

fun availableAtAfterMission(difficulty: Double, now: Long): Long {
    return now + (cooldownMinutes(difficulty) * 60_000).toLong()
}
