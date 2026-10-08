package com.marvel.recruiter.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.marvel.recruiter.R
import com.marvel.recruiter.data.repository.RosterHero
import com.marvel.recruiter.ui.theme.Borders
import com.marvel.recruiter.ui.theme.EaseStandard
import com.marvel.recruiter.ui.theme.LocalRecruiterColors
import com.marvel.recruiter.ui.theme.Motion
import com.marvel.recruiter.ui.theme.Space
import com.marvel.recruiter.ui.theme.motionTween
import com.marvel.recruiter.ui.theme.rememberReducedMotion
import com.marvel.recruiter.ui.util.formatInt
import kotlinx.coroutines.delay

private const val PHASE_CLOSED = 0
private const val PHASE_SHAKING = 1
private const val PHASE_BURST = 2
private const val PHASE_FLIPPING = 3
private const val PHASE_REVEALED = 4

/**
 * Revelação do pacote (design.md 3.10 e Tela 1). A compra já foi debitada; fechar só após revelar.
 * [hero] null enquanto o roster ainda não trouxe o herói sorteado.
 */
@Composable
fun PackRevealDialog(
    hero: RosterHero?,
    cost: Int,
    onDismiss: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val semantic = LocalRecruiterColors.current
    val reduced = rememberReducedMotion()

    var skipped by remember { mutableStateOf(reduced) }
    var phase by remember { mutableIntStateOf(if (reduced) PHASE_REVEALED else PHASE_CLOSED) }
    val flip = remember { Animatable(if (reduced) 1f else 0f) }
    val stamp = remember { Animatable(if (reduced) 1f else 1.15f) }

    LaunchedEffect(skipped) {
        if (skipped) {
            phase = PHASE_REVEALED
            flip.snapTo(1f)
            stamp.snapTo(1f)
            return@LaunchedEffect
        }
        phase = PHASE_SHAKING
        delay(Motion.PACK_SHAKE.toLong())
        phase = PHASE_BURST
        delay(500)
        phase = PHASE_FLIPPING
        flip.animateTo(1f, motionTween(Motion.FLIP, false, EaseStandard))
        phase = PHASE_REVEALED
        stamp.animateTo(1f, motionTween(300, false))
    }

    val shake by rememberInfiniteTransition(label = "shake").animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(tween(150), RepeatMode.Reverse),
        label = "shakeAngle",
    )
    val revealed = phase == PHASE_REVEALED
    val angle = flip.value * 180f

    Dialog(
        onDismissRequest = { if (revealed) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = colors.surface,
            modifier = Modifier.fillMaxWidth().padding(horizontal = Space.s4),
        ) {
            Column(
                modifier = Modifier.padding(Space.s4),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.pack_title),
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.primary,
                )
                Spacer(Modifier.height(Space.s3))
                Box(
                    modifier = Modifier
                        .size(width = 260.dp, height = 340.dp)
                        .graphicsLayer {
                            rotationZ = if (phase == PHASE_SHAKING && !reduced) shake else 0f
                            rotationY = angle
                            cameraDistance = 16f * density
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    if (angle < 90f || hero == null) {
                        Cover(modifier = Modifier.fillMaxSize())
                    } else {
                        Box(Modifier.fillMaxSize().graphicsLayer { rotationY = 180f }) {
                            Face(hero = hero, stampScale = stamp.value, semanticColor = semantic.success)
                        }
                    }
                }
                Spacer(Modifier.height(Space.s3))
                Text(
                    text = stringResource(R.string.pack_cost, cost),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.onSurfaceVariant,
                )
                Spacer(Modifier.height(Space.s3))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (!revealed) {
                        GhostButton(
                            text = stringResource(R.string.action_skip),
                            onClick = { skipped = true },
                        )
                    } else {
                        PrimaryButton(
                            text = stringResource(R.string.action_continue),
                            onClick = onDismiss,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Cover(modifier: Modifier) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colors.primary)
            .border(Borders.medium, colors.onPrimary.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            .padding(Space.s4),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.pack_cover),
            style = MaterialTheme.typography.displayMedium,
            color = colors.onPrimary,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun Face(hero: RosterHero, stampScale: Float, semanticColor: androidx.compose.ui.graphics.Color) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier.fillMaxSize().background(colors.surfaceVariant).padding(Space.s3),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.size(200.dp).clip(RoundedCornerShape(12.dp))) {
            HeroImage(name = hero.name, imageUrl = hero.imageUrl, modifier = Modifier.fillMaxSize())
            StampLabel(
                text = stringResource(R.string.stamp_recruited),
                color = semanticColor,
                rotation = -8f,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(Space.s2)
                    .graphicsLayer { scaleX = stampScale; scaleY = stampScale },
            )
        }
        Spacer(Modifier.height(Space.s3))
        Text(
            text = hero.name,
            style = MaterialTheme.typography.headlineSmall,
            color = colors.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 2,
        )
        Text(
            text = stringResource(R.string.pack_new_recruit),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onSurfaceVariant,
        )
        Text(
            text = stringResource(R.string.hero_stat_veterancy) + " " + formatInt(hero.veterancy.toInt()),
            style = MaterialTheme.typography.labelMedium,
            color = colors.onSurfaceVariant,
        )
        Spacer(Modifier.height(Space.s2))
        RarityTag(strength = 0.6 * hero.adjustedPower + 0.4 * hero.veterancy)
    }
}
