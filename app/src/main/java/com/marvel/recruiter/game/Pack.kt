package com.marvel.recruiter.game

import kotlin.random.Random

const val PACK_COST = 100
const val PACK_WEIGHT_BASE = 5.0

enum class Rarity(val weightMultiplier: Double) {
    COMMON(1.0),
    RARE(0.4),
    LEGENDARY(0.12),
}

const val RARE_STRENGTH_THRESHOLD = 45.0
const val LEGENDARY_STRENGTH_THRESHOLD = 65.0

fun rarityOf(strength: Double): Rarity = when {
    strength >= LEGENDARY_STRENGTH_THRESHOLD -> Rarity.LEGENDARY
    strength >= RARE_STRENGTH_THRESHOLD -> Rarity.RARE
    else -> Rarity.COMMON
}

data class PackCandidate(val cvId: Long, val veterancy: Double, val strength: Double = 0.0)

fun drawFromPack(pool: List<PackCandidate>, random: Random): Long {
    require(pool.isNotEmpty()) { "pool de pacote vazio" }

    val weights = pool.map { (it.veterancy + PACK_WEIGHT_BASE) * rarityOf(it.strength).weightMultiplier }
    var point = random.nextDouble() * weights.sum()
    for (i in pool.indices) {
        point -= weights[i]
        if (point < 0.0) return pool[i].cvId
    }
    return pool.last().cvId // imprecisão de ponto flutuante no último passo
}
