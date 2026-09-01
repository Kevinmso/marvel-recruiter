package com.marvel.recruiter.game

object Normalization {
    fun power(p: Int, pMin: Int, pMax: Int): Double {
        if (pMin == pMax) return 50.0
        return 100.0 * (p - pMin) / (pMax - pMin)
    }
}