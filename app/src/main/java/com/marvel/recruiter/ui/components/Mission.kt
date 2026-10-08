package com.marvel.recruiter.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.marvel.recruiter.R
import com.marvel.recruiter.data.repository.ArcSummary
import com.marvel.recruiter.game.DifficultyLabel
import com.marvel.recruiter.game.Rarity
import com.marvel.recruiter.game.rarityOf
import com.marvel.recruiter.ui.theme.Borders
import com.marvel.recruiter.ui.theme.LocalRecruiterColors
import com.marvel.recruiter.ui.theme.Space
import com.marvel.recruiter.ui.theme.StatValueSmall
import com.marvel.recruiter.ui.util.formatDecimal
import com.marvel.recruiter.ui.util.formatInt

/** Cor e rótulo de uma dificuldade (design.md 2.1 e 3.5). */
@Composable
fun difficultyColor(difficulty: Double): Color {
    val c = LocalRecruiterColors.current
    return when (DifficultyLabel.of(difficulty)) {
        DifficultyLabel.EASY -> c.difficultyEasy
        DifficultyLabel.MEDIUM -> c.difficultyMedium
        DifficultyLabel.EPIC -> c.difficultyEpic
    }
}

@Composable
fun DifficultyTag(difficulty: Double, modifier: Modifier = Modifier) {
    val label = DifficultyLabel.of(difficulty)
    val color = difficultyColor(difficulty)
    val (text, pips) = when (label) {
        DifficultyLabel.EASY -> stringResource(R.string.difficulty_easy) to 1
        DifficultyLabel.MEDIUM -> stringResource(R.string.difficulty_medium) to 2
        DifficultyLabel.EPIC -> stringResource(R.string.difficulty_epic) to 3
    }
    Row(
        modifier = modifier
            .height(24.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.12f))
            .border(Borders.thin, color, RoundedCornerShape(4.dp))
            .padding(horizontal = Space.s2),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Canvas(Modifier.size(width = (pips * 9).dp, height = 8.dp)) {
            for (i in 0 until pips) {
                val cx = 4.dp.toPx() + i * 9.dp.toPx()
                val cy = size.height / 2
                val r = 3.dp.toPx()
                drawLine(color, Offset(cx - r, cy), Offset(cx, cy - r), strokeWidth = 1.5.dp.toPx())
                drawLine(color, Offset(cx, cy - r), Offset(cx + r, cy), strokeWidth = 1.5.dp.toPx())
                drawLine(color, Offset(cx + r, cy), Offset(cx, cy + r), strokeWidth = 1.5.dp.toPx())
                drawLine(color, Offset(cx, cy + r), Offset(cx - r, cy), strokeWidth = 1.5.dp.toPx())
            }
        }
        Spacer(Modifier.width(Space.s2))
        Text(text, style = MaterialTheme.typography.labelMedium, color = color)
    }
}

/** Imagem do arco com silhueta por baixo (mesmo padrão do HeroImage). */
@Composable
fun ArcImage(name: String, imageUrl: String?, modifier: Modifier = Modifier) {
    Box(modifier) {
        HeroSilhouette(name, Modifier.fillMaxSize())
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = stringResource(R.string.cd_arc_image, name),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

/** Card de arco (design.md 3.6). Faixa de imagem no topo com título sobre gradiente; borda esquerda na cor da dificuldade. */
@Composable
fun MissionCard(
    arc: ArcSummary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(12.dp)
    val accent = difficultyColor(arc.difficulty)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.surface)
            .border(Borders.thin, colors.outline, shape)
            .clickable(role = Role.Button, onClick = onClick),
    ) {
        Box(Modifier.fillMaxWidth().height(if (compact) 96.dp else 150.dp)) {
            ArcImage(name = arc.name, imageUrl = arc.imageUrl, modifier = Modifier.fillMaxSize())
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f)))),
            )
            Text(
                text = arc.name,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = Space.s3, vertical = Space.s2),
            )
        }
        Row(Modifier.height(IntrinsicSize.Min)) {
            Box(Modifier.width(4.dp).fillMaxHeight().background(accent))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(if (compact) Space.s3 else Space.s4),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DifficultyTag(arc.difficulty)
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = stringResource(R.string.issues_count, arc.numIssues),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.onSurfaceVariant,
                    )
                }
                if (!compact && !arc.deck.isNullOrBlank()) {
                    Spacer(Modifier.height(Space.s2))
                    Text(
                        text = arc.deck,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = stringResource(R.string.original_text_caption),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.onSurfaceVariant,
                    )
                }
                Spacer(Modifier.height(Space.s3))
                Text(
                    text = stringResource(R.string.difficulty_value, formatDecimal(arc.difficulty)),
                    style = StatValueSmall,
                    color = colors.onSurface,
                )
            }
        }
    }
}

@Composable
fun RarityTag(strength: Double, modifier: Modifier = Modifier) {
    val rarity = rarityOf(strength)
    val color = when (rarity) {
        Rarity.COMMON -> Color(0xFF9AA5B1)
        Rarity.RARE -> Color(0xFF4DA3FF)
        Rarity.LEGENDARY -> Color(0xFFF2C94C)
    }
    val text = when (rarity) {
        Rarity.COMMON -> stringResource(R.string.rarity_common)
        Rarity.RARE -> stringResource(R.string.rarity_rare)
        Rarity.LEGENDARY -> stringResource(R.string.rarity_legendary)
    }
    Row(
        modifier = modifier
            .height(24.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.12f))
            .border(Borders.thin, color, RoundedCornerShape(4.dp))
            .padding(horizontal = Space.s2),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = text.uppercase(), style = MaterialTheme.typography.labelMedium, color = color)
    }
}
