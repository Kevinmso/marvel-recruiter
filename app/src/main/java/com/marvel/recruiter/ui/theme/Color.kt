package com.marvel.recruiter.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** Papéis de jogo que o Material3 não cobre (design.md 2.1). */
@Immutable
data class RecruiterColors(
    val success: Color,
    val onSuccess: Color,
    val danger: Color,
    val onDanger: Color,
    val coin: Color,
    val xp: Color,
    val difficultyEasy: Color,
    val difficultyMedium: Color,
    val difficultyEpic: Color,
    val synergy: Color,
    val cooldown: Color,
    val ink: Color,
    val factions: List<Color>,
)

val LocalRecruiterColors = staticCompositionLocalOf<RecruiterColors> {
    error("RecruiterTheme não está aplicado")
}

val LightColorScheme: ColorScheme = lightColorScheme(
    primary = Color(0xFFB3121F),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFF5D5D2),
    onPrimaryContainer = Color(0xFF5C0A12),
    secondary = Color(0xFF1C2B4A),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD9E2F5),
    onSecondaryContainer = Color(0xFF0F1C33),
    tertiary = Color(0xFF0B6E78),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFC9EEF2),
    onTertiaryContainer = Color(0xFF04363C),
    background = Color(0xFFF4F1EA),
    onBackground = Color(0xFF16130F),
    surface = Color(0xFFFBF9F4),
    onSurface = Color(0xFF16130F),
    surfaceVariant = Color(0xFFE8E2D3),
    onSurfaceVariant = Color(0xFF4D463C),
    surfaceTint = Color(0xFFB3121F),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF8F5EE),
    surfaceContainer = Color(0xFFF2EDE2),
    surfaceContainerHigh = Color(0xFFECE6D8),
    surfaceContainerHighest = Color(0xFFE5DECE),
    outline = Color(0xFF6B6358),
    outlineVariant = Color(0xFFCFC7B5),
    inverseSurface = Color(0xFF16130F),
    inverseOnSurface = Color(0xFFF3EFE6),
    error = Color(0xFFB00020),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
)

val DarkColorScheme: ColorScheme = darkColorScheme(
    primary = Color(0xFFFF5A4E),
    onPrimary = Color(0xFF0E0F12),
    primaryContainer = Color(0xFF3A1412),
    onPrimaryContainer = Color(0xFFFFD9D5),
    secondary = Color(0xFF8EB4FF),
    onSecondary = Color(0xFF0E0F12),
    secondaryContainer = Color(0xFF17263F),
    onSecondaryContainer = Color(0xFFD9E6FF),
    tertiary = Color(0xFF3FD0DC),
    onTertiary = Color(0xFF0E0F12),
    tertiaryContainer = Color(0xFF0E3A3F),
    onTertiaryContainer = Color(0xFFBDF3F8),
    background = Color(0xFF0E0F12),
    onBackground = Color(0xFFF3EFE6),
    surface = Color(0xFF17191E),
    onSurface = Color(0xFFF3EFE6),
    surfaceVariant = Color(0xFF22252C),
    onSurfaceVariant = Color(0xFFB9BEC9),
    surfaceTint = Color(0xFFFF5A4E),
    surfaceContainerLowest = Color(0xFF0B0C0F),
    surfaceContainerLow = Color(0xFF13151A),
    surfaceContainer = Color(0xFF1B1E24),
    surfaceContainerHigh = Color(0xFF262A32),
    surfaceContainerHighest = Color(0xFF2E323B),
    outline = Color(0xFF8A8F9C),
    outlineVariant = Color(0xFF2E323B),
    inverseSurface = Color(0xFFF3EFE6),
    inverseOnSurface = Color(0xFF16130F),
    error = Color(0xFFFF6B6B),
    onError = Color(0xFF0E0F12),
    errorContainer = Color(0xFF5C1A1A),
    onErrorContainer = Color(0xFFFFDAD6),
)

val LightRecruiterColors = RecruiterColors(
    success = Color(0xFF1E7B4A),
    onSuccess = Color(0xFFFFFFFF),
    danger = Color(0xFFB00020),
    onDanger = Color(0xFFFFFFFF),
    coin = Color(0xFF8A5A00),
    xp = Color(0xFF1C2B4A),
    difficultyEasy = Color(0xFF1E7B4A),
    difficultyMedium = Color(0xFF8A5A00),
    difficultyEpic = Color(0xFF6B2FA0),
    synergy = Color(0xFF0B6E78),
    cooldown = Color(0xFF6B6358),
    ink = Color(0xFF16130F),
    factions = listOf(
        Color(0xFF1F4E9A), Color(0xFF7A1F5C), Color(0xFF2E6B3A),
        Color(0xFF8A3B12), Color(0xFF3D3D8F), Color(0xFF5E5A1F),
    ),
)

val DarkRecruiterColors = RecruiterColors(
    success = Color(0xFF4ADE80),
    onSuccess = Color(0xFF0E0F12),
    danger = Color(0xFFFF6B6B),
    onDanger = Color(0xFF0E0F12),
    coin = Color(0xFFF5C451),
    xp = Color(0xFF8EB4FF),
    difficultyEasy = Color(0xFF4ADE80),
    difficultyMedium = Color(0xFFF2B33D),
    difficultyEpic = Color(0xFFB98CFF),
    synergy = Color(0xFF3FD0DC),
    cooldown = Color(0xFF8A8F9C),
    ink = Color(0xFFFFFFFF),
    factions = listOf(
        Color(0xFF6FA0FF), Color(0xFFE07CC0), Color(0xFF7BD389),
        Color(0xFFFFA271), Color(0xFFA3A3FF), Color(0xFFE6DC6E),
    ),
)
