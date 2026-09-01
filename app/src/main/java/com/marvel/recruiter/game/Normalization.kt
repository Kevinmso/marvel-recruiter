package com.marvel.recruiter.game

import kotlin.math.ln

object Normalization {
    fun power(p: Int, pMin: Int, pMax: Int): Double { // p = number of powers a character have
        if (pMin == pMax) return 50.0
        return 100.0 * (p - pMin) / (pMax - pMin)
    }

    fun veterancy(x: Int, xMin: Int, xMax: Int): Double { // x = number of issues a character appears
        if (xMin == xMax) return 50.0
        return 100.0 * (ln(x + 1.0) - ln(xMin + 1.0)) / (ln(xMax + 1.0) - ln(xMin + 1.0))
    }
}