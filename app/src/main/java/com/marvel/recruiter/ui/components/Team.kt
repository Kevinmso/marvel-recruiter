package com.marvel.recruiter.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.marvel.recruiter.R
import com.marvel.recruiter.data.repository.RosterHero
import com.marvel.recruiter.ui.theme.Borders
import com.marvel.recruiter.ui.theme.EaseDecelerate
import com.marvel.recruiter.ui.theme.LocalRecruiterColors
import com.marvel.recruiter.ui.theme.Motion
import com.marvel.recruiter.ui.theme.Space
import com.marvel.recruiter.ui.theme.motionTween
import com.marvel.recruiter.ui.theme.rememberReducedMotion
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

/** Slot do time: vazio (tracejado com "+") ou com miniatura do herói. */
@Composable
fun TeamSlot(
    hero: RosterHero?,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(12.dp)
    if (hero == null) {
        Box(
            modifier = modifier
                .height(96.dp)
                .clip(shape)
                .drawDashedBorder(colors.outline),
            contentAlignment = Alignment.Center,
        ) {
            Text("+", style = MaterialTheme.typography.titleLarge, color = colors.outline)
        }
    } else {
        val removeDescription = stringResource(R.string.cd_remove_hero, hero.name)
        Column(
            modifier = modifier
                .height(96.dp)
                .clip(shape)
                .border(Borders.thin, colors.primary, shape)
                .padding(Space.s1),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(8.dp))) {
                HeroImage(name = hero.name, imageUrl = hero.imageUrl, modifier = Modifier.fillMaxSize())
            }
            Text(
                text = hero.name,
                style = MaterialTheme.typography.labelSmall,
                color = colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Box(
                Modifier
                    .size(48.dp)
                    .clickable(onClick = onRemove)
                    .semantics { contentDescription = removeDescription },
                contentAlignment = Alignment.Center,
            ) {
                CloseGlyph(colors.onSurfaceVariant, size = 14.dp)
            }
        }
    }
}

private fun Modifier.drawDashedBorder(color: androidx.compose.ui.graphics.Color): Modifier =
    this.then(
        Modifier.drawBehind {
            val effect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f))
            drawRoundRect(
                color = color,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx(), pathEffect = effect),
                cornerRadius = CornerRadius(12.dp.toPx()),
            )
        },
    )

/**
 * Barra de força: preenche até [strength] (escala de referência [maxReference], só para a barra);
 * a linha vertical marca a Dificuldade.
 */
@Composable
fun ForceBar(
    strength: Double,
    difficulty: Double,
    label: String,
    modifier: Modifier = Modifier,
    maxReference: Double = 200.0,
) {
    val colors = MaterialTheme.colorScheme
    val semantic = LocalRecruiterColors.current
    val reduced = rememberReducedMotion()
    val target = (strength / maxReference).toFloat().coerceIn(0f, 1f)
    val animated by animateFloatAsState(
        targetValue = target,
        animationSpec = motionTween(Motion.FILL, reduced, EaseDecelerate),
        label = "forceBar",
    )
    val winning = strength > difficulty
    val fillColor = if (winning) semantic.success else semantic.danger
    val marker = (difficulty / maxReference).toFloat().coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(12.dp)
            .semantics(mergeDescendants = true) {
                progressBarRangeInfo = ProgressBarRangeInfo(strength.toFloat(), 0f..maxReference.toFloat())
                contentDescription = label
            },
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val radius = CornerRadius(size.height / 2)
            drawRoundRect(colors.surfaceVariant, cornerRadius = radius)
            drawRoundRect(fillColor, size = Size(size.width * animated, size.height), cornerRadius = radius)
            val x = size.width * marker
            drawLine(
                color = colors.primary,
                start = Offset(x, -4.dp.toPx()),
                end = Offset(x, size.height + 4.dp.toPx()),
                strokeWidth = 2.dp.toPx(),
            )
        }
    }
}
