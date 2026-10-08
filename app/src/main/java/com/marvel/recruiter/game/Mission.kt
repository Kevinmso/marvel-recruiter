package com.marvel.recruiter.game

import kotlin.math.exp
import kotlin.math.roundToInt
import kotlin.random.Random

// constantes de calibração (T-25)
const val SUCCESS_CHANCE_SCALE = 30.0
const val REWARD_XP_ALPHA = 10.0
const val REWARD_COINS_BETA = 5.0
const val COOLDOWN_MINUTES_PER_10_DIFFICULTY = 3.0

fun successChance(teamStrength: Double, difficulty: Double): Double {
    val raw = 1.0 / (1.0 + exp(-(teamStrength - difficulty) / SUCCESS_CHANCE_SCALE))
    return raw.coerceIn(0.05, 0.95)
}

data class MissionRoll(val roll: Int, val success: Boolean)

fun rollMission(chance: Double, random: Random): MissionRoll {
    val roll = random.nextInt(100)
    return MissionRoll(roll, roll < chance * 100)
}

fun resolveMission(chance: Double, random: Random): Boolean = rollMission(chance, random).success

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
