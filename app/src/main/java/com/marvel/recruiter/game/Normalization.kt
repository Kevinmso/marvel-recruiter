package com.marvel.recruiter.game

import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min

object Normalization {

    const val GAMMA = 0.4

    fun power(p: Int, pMin: Int, pMax: Int): Double { // p = number of powers a character have
        if (pMin == pMax) return 50.0
        return 100.0 * (p - pMin) / (pMax - pMin)
    }

    fun veterancy(x: Int, xMin: Int, xMax: Int): Double { // x = number of issues a character appears
        if (xMin == xMax) return 50.0
        return 100.0 * (ln(x + 1.0) - ln(xMin + 1.0)) / (ln(xMax + 1.0) - ln(xMin + 1.0))
    }

    fun compensatedPower(power: Double, veterancy: Double): Double {
        val compensated: Double = power + GAMMA * max(0.0, 50 - veterancy)
        return min(100.0, compensated)
    }

    fun adjustedPower(compensated: Double): Double{
        return max(15.0, compensated)
    }
}