package com.marvel.recruiter.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.marvel.recruiter.R
import com.marvel.recruiter.ui.theme.EaseDice
import com.marvel.recruiter.ui.theme.LocalRecruiterColors
import com.marvel.recruiter.ui.theme.Motion
import com.marvel.recruiter.ui.theme.StatValue
import com.marvel.recruiter.ui.theme.Space
import com.marvel.recruiter.ui.theme.motionTween
import com.marvel.recruiter.ui.theme.rememberReducedMotion
import com.marvel.recruiter.ui.util.formatInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription

/**
 * Rolagem do sorteio (design.md 3.11). Número girando até [roll]; ponteiro e arco de chance.
 * [rolling] liga a animação; [onSettled] avisa quando o valor final está na tela.
 */
@Composable
fun DiceRoll(
    roll: Int,
    chance: Double,
    rolling: Boolean,
    label: String,
    onSettled: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val semantic = LocalRecruiterColors.current
    val reduced = rememberReducedMotion()
    var shown by remember { mutableIntStateOf(0) }
    var settled by remember { mutableStateOf(false) }
    val currentOnSettled by rememberUpdatedState(onSettled)

    LaunchedEffect(rolling) {
        if (!rolling) return@LaunchedEffect
        if (reduced) {
            shown = roll
            settled = true
            currentOnSettled()
            return@LaunchedEffect
        }
        val spin = launch {
            while (true) {
                shown = Random.nextInt(100)
                delay(60)
            }
        }
        delay(Motion.DICE.toLong())
        spin.cancel()
        shown = roll
        settled = true
        currentOnSettled()
    }

    val pointer by animateFloatAsState(
        targetValue = if (settled) roll / 100f else 0f,
        animationSpec = motionTween(Motion.DICE, reduced, EaseDice),
        label = "pointer",
    )
    Column(
        modifier = modifier.clearAndSetSemantics { contentDescription = label },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.size(160.dp), contentAlignment = Alignment.Center) {
            Canvas(Modifier.fillMaxSize()) {
                val stroke = 10.dp.toPx()
                val inset = stroke / 2
                val arcSize = Size(size.width - stroke, size.height - stroke)
                val topLeft = Offset(inset, inset)
                drawArc(
                    color = semantic.danger,
                    startAngle = 135f,
                    sweepAngle = 270f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Butt),
                )
                drawArc(
                    color = semantic.success,
                    startAngle = 135f,
                    sweepAngle = 270f * chance.toFloat(),
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Butt),
                )
                val angle = Math.toRadians((135.0 + 270.0 * pointer))
                val r = size.minDimension / 2 - stroke
                val center = Offset(size.width / 2, size.height / 2)
                val tip = Offset(center.x + (r * cos(angle)).toFloat(), center.y + (r * sin(angle)).toFloat())
                drawLine(colors.onSurface, center, tip, strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
            }
            Box(
                Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(50))
                    .background(colors.surface),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (rolling) formatInt(shown) else "?",
                    style = StatValue.copy(fontSize = 56.sp, lineHeight = 60.sp),
                    color = colors.onSurface,
                    textAlign = TextAlign.Center,
                )
            }
        }
        Spacer(Modifier.height(Space.s2))
        Text(label, style = MaterialTheme.typography.labelSmall, color = colors.onSurfaceVariant)
    }
}

/** Faixa de resultado com carimbo. Vitória: brilho radial; falha: treme 2x. Reduced motion: estático. */
@Composable
fun ResultBanner(
    success: Boolean,
    title: String,
    stamp: String,
    modifier: Modifier = Modifier,
) {
    val semantic = LocalRecruiterColors.current
    val reduced = rememberReducedMotion()
    val background = if (success) semantic.success else semantic.danger
    val content = if (success) semantic.onSuccess else semantic.onDanger
    val glow by animateFloatAsState(
        targetValue = if (success) 1.2f else 0f,
        animationSpec = motionTween(Motion.BURST, reduced),
        label = "glow",
    )
    val shake = remember { Animatable(0f) }
    LaunchedEffect(success) {
        if (!success && !reduced) {
            repeat(2) {
                shake.animateTo(4f, motionTween(75, false))
                shake.animateTo(-4f, motionTween(75, false))
            }
            shake.animateTo(0f, motionTween(75, false))
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(96.dp)
            .graphicsLayer { translationX = shake.value * density }
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .semantics { contentDescription = title },
        contentAlignment = Alignment.Center,
    ) {
        if (success && glow > 0f) {
            Box(
                Modifier
                    .fillMaxSize()
                    .graphicsLayer { scaleX = glow; scaleY = glow }
                    .background(Brush.radialGradient(listOf(semantic.coin.copy(alpha = 0.35f), Color.Transparent))),
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.displayMedium.copy(fontSize = 30.sp, lineHeight = 34.sp),
            color = content,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = Space.s4),
        )
        StampLabel(
            text = stamp,
            color = content,
            rotation = -6f,
            modifier = Modifier.align(Alignment.TopEnd).padding(Space.s2),
        )
    }
}

/** Recompensa em duas colunas: XP e Moeda (design.md 3.14). Contadores sobem em 600ms. */
@Composable
fun RewardRow(
    xp: Int,
    coins: Int,
    success: Boolean,
    streakPercent: Int = 0,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val semantic = LocalRecruiterColors.current
    val reduced = rememberReducedMotion()
    val shownXp by animateIntAsState(xp, motionTween(Motion.FILL, reduced), label = "xp")
    val shownCoins by animateIntAsState(coins, motionTween(Motion.FILL, reduced), label = "coins")

    Column(modifier = modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Space.s3)) {
            RewardColumn(
                modifier = Modifier.weight(1f),
                icon = { XpGlyph(semantic.xp, size = 24.dp) },
                value = formatInt(shownXp),
                caption = stringResource(R.string.result_xp),
                valueColor = semantic.xp,
            )
            RewardColumn(
                modifier = Modifier.weight(1f),
                icon = { CoinGlyph(semantic.coin, colors.surface, size = 24.dp) },
                value = formatInt(shownCoins),
                caption = stringResource(R.string.result_coins),
                valueColor = semantic.coin,
            )
        }
        if (success && streakPercent > 0) {
            Spacer(Modifier.height(Space.s1))
            Text(
                text = stringResource(R.string.streak_bonus, streakPercent),
                style = MaterialTheme.typography.labelMedium,
                color = semantic.coin,
            )
        }
        if (!success) {
            Spacer(Modifier.height(Space.s1))
            Text(
                text = stringResource(R.string.result_fail_multiplier),
                style = MaterialTheme.typography.labelSmall,
                color = colors.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun RewardColumn(
    modifier: Modifier,
    icon: @Composable () -> Unit,
    value: String,
    caption: String,
    valueColor: Color,
) {
    Column(
        modifier = modifier.background(MaterialTheme.colorScheme.surface).padding(Space.s3),
        horizontalAlignment = Alignment.Start,
    ) {
        icon()
        Spacer(Modifier.height(Space.s1))
        Text(value, style = StatValue, color = valueColor)
        Text(caption, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
