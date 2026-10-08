package com.marvel.recruiter.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.marvel.recruiter.R

val AntonFamily = FontFamily(Font(R.font.anton_regular, FontWeight.Normal))

val BarlowCondensedFamily = FontFamily(
    Font(R.font.barlow_condensed_medium, FontWeight.Medium),
    Font(R.font.barlow_condensed_semibold, FontWeight.SemiBold),
    Font(R.font.barlow_condensed_bold, FontWeight.Bold),
)

// Arquivo variável: o peso é aplicado pelo eixo wght.
val InterFamily = FontFamily(
    Font(R.font.inter_variable, FontWeight.Normal),
    Font(R.font.inter_variable, FontWeight.Medium),
    Font(R.font.inter_variable, FontWeight.SemiBold),
)

val JetBrainsMonoFamily = FontFamily(
    Font(R.font.jetbrains_mono_variable, FontWeight.Medium),
)

val RecruiterTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = AntonFamily, fontWeight = FontWeight.Normal,
        fontSize = 48.sp, lineHeight = 52.sp, letterSpacing = 0.02.em,
    ),
    displayMedium = TextStyle(
        fontFamily = AntonFamily, fontWeight = FontWeight.Normal,
        fontSize = 36.sp, lineHeight = 40.sp, letterSpacing = 0.02.em,
    ),
    headlineSmall = TextStyle(
        fontFamily = BarlowCondensedFamily, fontWeight = FontWeight.Bold,
        fontSize = 26.sp, lineHeight = 30.sp, letterSpacing = 0.04.em,
    ),
    titleLarge = TextStyle(
        fontFamily = BarlowCondensedFamily, fontWeight = FontWeight.Bold,
        fontSize = 22.sp, lineHeight = 26.sp, letterSpacing = 0.03.em,
    ),
    titleMedium = TextStyle(
        fontFamily = BarlowCondensedFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp, lineHeight = 22.sp, letterSpacing = 0.04.em,
    ),
    bodyLarge = TextStyle(
        fontFamily = InterFamily, fontWeight = FontWeight.Normal,
        fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = InterFamily, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = BarlowCondensedFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp, lineHeight = 20.sp, letterSpacing = 0.08.em,
    ),
    labelMedium = TextStyle(
        fontFamily = BarlowCondensedFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp, lineHeight = 16.sp, letterSpacing = 0.08.em,
    ),
    labelSmall = TextStyle(
        fontFamily = InterFamily, fontWeight = FontWeight.Medium,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.02.em,
    ),
)

/** Valores numéricos: sempre mono (design.md 2.2). */
val StatValue = TextStyle(
    fontFamily = JetBrainsMonoFamily, fontWeight = FontWeight.Medium,
    fontSize = 22.sp, lineHeight = 26.sp,
)

val StatValueSmall = TextStyle(
    fontFamily = JetBrainsMonoFamily, fontWeight = FontWeight.Medium,
    fontSize = 15.sp, lineHeight = 20.sp,
)
