package com.marvel.recruiter.game

import org.junit.Assert.assertEquals
import org.junit.Test

/** RF-06 (compensação de raridade) + RF-07 (piso de segurança). */
class AdjustedPowerTest {

    // ---- RF-06: compensatedPower ----

    @Test
    fun `RF-06 — heroi sem veterania ganha o bonus cheio (Black Goliath)`() {
        // min(100, 0 + 0.4 * (50 - 0)) = 20
        assertEquals(20.0, Normalization.compensatedPower(power = 0.0, veterancy = 0.0), 0.001)
    }

    @Test
    fun `RF-06 — veterano (veterancy maior ou igual a 50) nao ganha nada (Colossus)`() {
        // max(0, 50 - 80.6) = 0 → compensado == power
        assertEquals(8.33, Normalization.compensatedPower(power = 8.33, veterancy = 80.6), 0.001)
    }

    @Test
    fun `RF-06 — exatamente na fronteira veterancy 50 nao ganha bonus`() {
        assertEquals(40.0, Normalization.compensatedPower(power = 40.0, veterancy = 50.0), 0.001)
    }

    @Test
    fun `RF-06 — bonus parcial para veterancy entre 0 e 50`() {
        // 30 + 0.4 * (50 - 30) = 30 + 8 = 38
        assertEquals(38.0, Normalization.compensatedPower(power = 30.0, veterancy = 30.0), 0.001)
    }

    @Test
    fun `RF-06 — teto de 100`() {
        // 90 + 0.4 * 50 = 110 → min(100, 110) = 100
        assertEquals(100.0, Normalization.compensatedPower(power = 90.0, veterancy = 0.0), 0.001)
    }

    // ---- RF-07: adjustedPower ----

    @Test
    fun `RF-07 — piso de 15 quando o compensado e menor (Colossus)`() {
        assertEquals(15.0, Normalization.adjustedPower(compensated = 8.33), 0.001)
    }

    @Test
    fun `RF-07 — compensado acima de 15 passa direto (Black Goliath)`() {
        assertEquals(20.0, Normalization.adjustedPower(compensated = 20.0), 0.001)
    }

    @Test
    fun `RF-07 — exatamente 15 fica 15`() {
        assertEquals(15.0, Normalization.adjustedPower(compensated = 15.0), 0.001)
    }
}
