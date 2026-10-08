package com.marvel.recruiter.ui.util

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

private val ptBr = Locale("pt", "BR")

fun formatInt(value: Int): String = NumberFormat.getIntegerInstance(ptBr).format(value)

fun formatDecimal(value: Double, digits: Int = 1): String = String.format(ptBr, "%.${digits}f", value)

fun formatPercent(chance: Double): Int = (chance * 100).roundToInt()
