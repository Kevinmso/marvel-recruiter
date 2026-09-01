package com.marvel.recruiter.game

import kotlin.random.Random

/** k do RF-12 — valor inicial, calibrar em T-25. */
const val SUCCESS_CHANCE_K = 1.0

/**
 * RF-12: chance de sucesso de uma missão.
 *
 * chance = clamp((teamStrength - difficulty) / difficulty * k + 0.5, 0.05, 0.95)
 *
 * `difficulty` nunca é 0 (RF-11 garante o intervalo 10–100, ou 50 pela guarda), então
 * a divisão é segura.
 */
fun successChance(teamStrength: Double, difficulty: Double): Double {
    val raw = (teamStrength - difficulty) / difficulty * SUCCESS_CHANCE_K + 0.5
    return raw.coerceIn(0.05, 0.95)
}

/**
 * RF-13: resolve o resultado da missão por sorteio.
 *
 * `random` é injetado (constitution.md C-15) — nunca usar um gerador global aqui.
 * Nos testes, passe `Random(seed)` para resultado determinístico.
 *
 * Sorteia um inteiro em [0, 100) e compara com `chance * 100`.
 */
fun resolveMission(chance: Double, random: Random): Boolean {
    return random.nextInt(100) < chance * 100
}
