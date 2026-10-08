package com.marvel.recruiter.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.marvel.recruiter.R
import com.marvel.recruiter.data.repository.RosterHero
import com.marvel.recruiter.ui.theme.Borders
import com.marvel.recruiter.ui.theme.LocalRecruiterColors
import com.marvel.recruiter.ui.theme.Space
import com.marvel.recruiter.ui.util.formatInt
import kotlin.math.roundToInt

/** Iniciais sobre meios-tons; usado enquanto a imagem não chega ou falha. Nunca inventa imagem. */
@Composable
fun HeroSilhouette(name: String, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = modifier.background(colors.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        HalftoneOverlay(colors.onSurfaceVariant.copy(alpha = 0.12f), Modifier.fillMaxSize())
        Text(
            text = initialsOf(name),
            style = MaterialTheme.typography.titleLarge,
            color = colors.onSurfaceVariant,
        )
    }
}

internal fun initialsOf(name: String): String =
    name.split(" ")
        .map { word -> word.filter { it.isLetterOrDigit() } }
        .filter { it.isNotEmpty() }
        .take(2)
        .joinToString("") { it.first().uppercase() }

/** Imagem do herói com silhueta por baixo (aparece se a imagem não carregar). */
@Composable
fun HeroImage(
    name: String,
    imageUrl: String?,
    modifier: Modifier = Modifier,
    grayscale: Boolean = false,
) {
    val filter = if (grayscale) ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }) else null
    Box(modifier) {
        HeroSilhouette(name, Modifier.fillMaxSize())
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = stringResource(R.string.cd_hero_image, name),
                contentScale = ContentScale.Crop,
                colorFilter = filter,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

/**
 * Card de herói (design.md 3.1). [enabled] false = desabilitado para seleção (cooldown);
 * [selected] marca o herói escalado no time.
 */
@Composable
fun HeroCard(
    hero: RosterHero,
    now: Long,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    enabled: Boolean = true,
    onProfileClick: (() -> Unit)? = null,
) {
    val colors = MaterialTheme.colorScheme
    val semantic = LocalRecruiterColors.current
    val resting = hero.availableAt > now
    val status = if (resting) stringResource(R.string.hero_status_resting) else stringResource(R.string.hero_status_available)
    val borderColor = if (selected) colors.primary else colors.outline
    val profileDescription = stringResource(R.string.cd_hero_profile, hero.name)
    val borderWidth = if (selected) Borders.thick else Borders.thin
    val shape = RoundedCornerShape(12.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.surface)
            .border(borderWidth, borderColor, shape)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .semantics(mergeDescendants = true) {
                contentDescription = "${hero.name}. ${status}."
                stateDescription = status
            },
    ) {
        Box(Modifier.fillMaxWidth().aspectRatio(1f)) {
            HeroImage(
                name = hero.name,
                imageUrl = hero.imageUrl,
                modifier = Modifier.fillMaxSize(),
                grayscale = !enabled,
            )
            val stamp = when {
                resting -> stringResource(R.string.stamp_resting) to colors.secondary
                else -> stringResource(R.string.stamp_recruited) to semantic.success
            }
            StampLabel(
                text = stamp.first,
                color = stamp.second,
                modifier = Modifier.align(Alignment.TopEnd).padding(Space.s2),
            )
            if (selected) {
                Box(
                    Modifier
                        .align(Alignment.TopStart)
                        .padding(Space.s2)
                        .size(24.dp)
                        .clip(RoundedCornerShape(50))
                        .background(colors.primary),
                    contentAlignment = Alignment.Center,
                ) {
                    CheckGlyph(colors.onPrimary, size = 14.dp)
                }
            }
        }
        Column(Modifier.padding(Space.s3)) {
            Text(
                text = hero.name,
                style = MaterialTheme.typography.headlineSmall,
                color = colors.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(Space.s2))
            Column(verticalArrangement = Arrangement.spacedBy(Space.s1)) {
                StatChip(
                    label = stringResource(R.string.hero_stat_power),
                    value = formatInt(hero.adjustedPower.roundToInt()),
                )
                StatChip(
                    label = stringResource(R.string.hero_stat_veterancy),
                    value = formatInt(hero.veterancy.roundToInt()),
                )
            }
            if (resting) {
                Spacer(Modifier.height(Space.s2))
                CooldownBadge(availableAt = hero.availableAt, now = now)
            }
            if (onProfileClick != null) {
                GhostButton(
                    text = stringResource(R.string.action_profile),
                    onClick = onProfileClick,
                    modifier = Modifier.semantics(mergeDescendants = true) {
                        contentDescription = profileDescription
                    },
                )
            }
        }
    }
}

/** Chip de equipe. A cor vem do nome (a Comic Vine não entrega cvId aqui); só cor de UI. */
@Composable
fun FactionChip(teamName: String, modifier: Modifier = Modifier) {
    val colors = LocalRecruiterColors.current
    val slot = Math.floorMod(teamName.hashCode(), colors.factions.size)
    val tone = colors.factions[slot]
    Box(
        modifier = modifier
            .height(24.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(tone.copy(alpha = 0.14f))
            .border(Borders.thin, tone, RoundedCornerShape(4.dp))
            .padding(horizontal = Space.s2),
        contentAlignment = Alignment.Center,
    ) {
        Text(teamName, style = MaterialTheme.typography.labelMedium, color = tone, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}
