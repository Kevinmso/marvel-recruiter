package com.marvel.recruiter.game

const val XP_PER_LEVEL = 200
const val LEVELS_PER_PACK = 5

fun levelOf(xpTotal: Int): Int = 1 + xpTotal / XP_PER_LEVEL

fun packsEarnedAt(level: Int): Int = level / LEVELS_PER_PACK

fun pendingLevelPacks(xpTotal: Int, packsOpened: Int): Int =
    (packsEarnedAt(levelOf(xpTotal)) - packsOpened).coerceAtLeast(0)
