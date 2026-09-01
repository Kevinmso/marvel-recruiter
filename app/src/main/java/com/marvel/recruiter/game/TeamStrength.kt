package com.marvel.recruiter.game

data class HeroStats(
    val cvId: Long,
    val adjustedPower: Double,
    val veterancy: Double,
    val teamCvIds: Set<Long>
)

data class SynergyPair(
    val lowCvId: Long,
    val highCvId: Long
)

fun synergyBonus(team: List<HeroStats>, synergies: Set<SynergyPair>): Int {
    var count = 0
    for (i in team.indices) {
        for (j in i + 1 until team.size) {
            val a = team[i].cvId
            val b = team[j].cvId
            val pair = SynergyPair(minOf(a, b), maxOf(a, b))
            if (pair in synergies) count++
        }
    }
    return count * 10
}

fun factionBonus(team: List<HeroStats>): Int {
    val counts = mutableMapOf<Long, Int>()
    for (hero in team) for (t in hero.teamCvIds) {
        counts[t] = (counts[t] ?: 0) + 1
    }
    val max = counts.values.maxOrNull() ?: 0
    if (max <= 1) return 0
    return max * 3
}

fun teamStrength(team: List<HeroStats>, synergies: Set<SynergyPair>): Double {
    val base = team.sumOf { 0.6 * it.adjustedPower + 0.4 * it.veterancy }
    val synergy = minOf(synergyBonus(team, synergies), 40)
    val faction = factionBonus(team)
    return base + synergy + faction
}