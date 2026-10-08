package com.marvel.recruiter.game

const val MAX_STREAK_BONUS_STEPS = 5
const val STREAK_STEP = 0.05

/** Bônus de força do minijogo de ataque coordenado. `position` em [0, 1]. */
fun coordinationBonus(position: Double): Double = when {
    position in 0.42..0.58 -> 0.10
    position in 0.30..0.70 -> 0.05
    else -> 0.0
}

/** Multiplicador de recompensa por sequência de vitórias (máximo +25%). */
fun streakMultiplier(streak: Int): Double = 1.0 + STREAK_STEP * streak.coerceIn(0, MAX_STREAK_BONUS_STEPS)
