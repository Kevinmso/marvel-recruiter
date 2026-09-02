package com.marvel.recruiter.game

import kotlin.random.Random

const val PACK_COST = 100
const val PACK_WEIGHT_BASE = 5.0

data class PackCandidate(val cvId: Long, val veterancy: Double)

fun drawFromPack(pool: List<PackCandidate>, random: Random): Long {
    require(pool.isNotEmpty()) { "pool de pacote vazio" }

    val weights = pool.map { it.veterancy + PACK_WEIGHT_BASE }
    var point = random.nextDouble() * weights.sum()
    for (i in pool.indices) {
        point -= weights[i]
        if (point < 0.0) return pool[i].cvId
    }
    return pool.last().cvId // imprecisão de ponto flutuante no último passo
}
