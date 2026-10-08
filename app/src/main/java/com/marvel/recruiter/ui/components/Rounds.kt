package com.marvel.recruiter.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marvel.recruiter.R
import com.marvel.recruiter.data.repository.RosterHero
import com.marvel.recruiter.ui.theme.AntonFamily
import com.marvel.recruiter.ui.theme.EaseAccelerate
import com.marvel.recruiter.ui.theme.EaseDecelerate
import com.marvel.recruiter.ui.theme.LocalRecruiterColors
import com.marvel.recruiter.ui.theme.Motion
import com.marvel.recruiter.ui.theme.Space
import com.marvel.recruiter.ui.theme.StatValueSmall
import com.marvel.recruiter.ui.theme.motionTween
import com.marvel.recruiter.ui.theme.rememberReducedMotion
import com.marvel.recruiter.ui.util.formatPercent
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

const val ROUNDS = 3

private const val START_PRESSURE = 1f
private const val SHAKE_DP = 6f
private const val SHAKE_FREQUENCY = 20f
private val SceneHeight = 260.dp
private val ScenePadding = 8.dp
private val FighterSize = 44.dp
private val ArcFrameWidth = 120.dp
private val LungeDistance = 80.dp

private fun heroForce(hero: RosterHero): Double = 0.6 * hero.adjustedPower + 0.4 * hero.veterancy

/**
 * Três rodadas de confronto (RF-27): em cada golpe, um herói investe contra o arco na ordem de
 * força e a pressão restante cai na proporção da contribuição dele. [frozenStep] fixa o estado
 * parado (último passo ao reabrir, ou um passo específico em teste) sem animar.
 */
@Composable
fun ArcRounds(
    heroes: List<RosterHero>,
    teamStrength: Double,
    difficulty: Double,
    arcName: String,
    arcImageUrl: String?,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
    frozenStep: Int? = null,
) {
    val colors = MaterialTheme.colorScheme
    val semantic = LocalRecruiterColors.current
    val reduced = rememberReducedMotion()
    val ordered = remember(heroes) { heroes.sortedByDescending(::heroForce) }
    val count = ordered.size
    val total = ROUNDS * count

    val forces = ordered.map(::heroForce)
    val sum = forces.sum()
    val shares = forces.map { if (sum > 0) it / sum else 1.0 / count }
    val cumulative = shares.runningReduce { a, b -> a + b }
    val endPressure = (1.0 - teamStrength / difficulty).coerceIn(0.0, 1.0).toFloat()

    fun pressureAfter(step: Int): Float {
        if (step < 0) return START_PRESSURE
        val resolved = (step / count + cumulative[step % count]) / ROUNDS
        return START_PRESSURE - (START_PRESSURE - endPressure) * resolved.toFloat()
    }

    var liveStep by remember { mutableIntStateOf(-1) }
    val step = frozenStep ?: liveStep
    val animated = !reduced && frozenStep == null
    val live = animated && step >= 0
    val currentOnFinished by rememberUpdatedState(onFinished)

    LaunchedEffect(ordered) {
        if (frozenStep != null) return@LaunchedEffect
        if (reduced) {
            liveStep = total - 1
        } else {
            for (s in 0 until total) {
                liveStep = s
                delay(Motion.ROUND_STEP.toLong())
            }
            delay(Motion.ROUND_HOLD)
        }
        currentOnFinished()
    }

    val shake = remember { Animatable(0f) }
    val flash = remember { Animatable(0f) }
    val impact = remember { Animatable(0f) }
    val lunge = remember { Animatable(0f) }
    LaunchedEffect(step) {
        if (!live) return@LaunchedEffect
        lunge.animateTo(1f, tween(Motion.ROUND_LUNGE, easing = EaseDecelerate))
        coroutineScope {
            launch { lunge.animateTo(0f, tween(Motion.ROUND_IMPACT, easing = EaseAccelerate)) }
            launch {
                shake.snapTo(1f)
                shake.animateTo(0f, tween(Motion.ROUND_IMPACT))
            }
            launch {
                flash.snapTo(1f)
                flash.animateTo(0f, tween(Motion.ROUND_IMPACT))
            }
            launch {
                impact.snapTo(0f)
                impact.animateTo(1f, tween(Motion.ROUND_IMPACT))
            }
        }
    }

    val pressure by animateFloatAsState(
        targetValue = pressureAfter(step),
        animationSpec = motionTween(Motion.ROUND_STEP, reduced, EaseDecelerate),
        label = "pressure",
    )

    val roundNow = if (step < 0) 1 else step / count + 1
    val strikerIndex = if (step < 0) -1 else step % count
    val striker = ordered.getOrNull(strikerIndex)
    val hitValue = if (striker != null) (shares[strikerIndex] * 100).roundToInt() else 0
    val pressureLabel = stringResource(R.string.round_pressure_cd, formatPercent(pressure.toDouble()))
    val barColor = difficultyColor(difficulty)
    val textMeasurer = rememberTextMeasurer()
    val damageText = stringResource(R.string.round_damage, hitValue)
    val shape = RoundedCornerShape(12.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface, shape)
            .border(1.dp, colors.outline, shape)
            .padding(Space.s4),
        verticalArrangement = Arrangement.spacedBy(Space.s3),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.round_title, roundNow, ROUNDS),
                style = MaterialTheme.typography.titleLarge.copy(fontFamily = AntonFamily),
                color = colors.primary,
                modifier = Modifier
                    .border(2.dp, semantic.ink, RoundedCornerShape(4.dp))
                    .padding(horizontal = Space.s2),
            )
        }
        Text(
            text = striker?.let { stringResource(R.string.round_attacker, it.name) }.orEmpty(),
            style = MaterialTheme.typography.labelMedium,
            color = colors.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.round_pressure),
                style = MaterialTheme.typography.labelSmall,
                color = colors.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(R.string.round_pressure_value, formatPercent(pressure.toDouble())),
                style = StatValueSmall,
                color = colors.onSurface,
            )
        }
        Canvas(
            Modifier
                .fillMaxWidth()
                .height(14.dp)
                .semantics(mergeDescendants = true) {
                    progressBarRangeInfo = ProgressBarRangeInfo(pressure, 0f..1f)
                    contentDescription = pressureLabel
                },
        ) {
            val radius = CornerRadius(size.height / 2)
            val fillWidth = size.width * pressure
            drawRoundRect(colors.surfaceVariant, cornerRadius = radius)
            drawRoundRect(barColor, size = Size(fillWidth, size.height), cornerRadius = radius)
            if (flash.value > 0f) {
                drawRoundRect(
                    Color.White.copy(alpha = 0.7f * flash.value),
                    size = Size(fillWidth, size.height),
                    cornerRadius = radius,
                )
            }
        }
        Box(
            Modifier
                .fillMaxWidth()
                .height(SceneHeight)
                .graphicsLayer {
                    translationX = shake.value * SHAKE_DP * density * sin(shake.value * SHAKE_FREQUENCY)
                }
                .clip(RoundedCornerShape(8.dp))
                .border(2.dp, semantic.ink, RoundedCornerShape(8.dp))
                .clearAndSetSemantics {},
        ) {
            BattleBackdrop(arcName, arcImageUrl, Modifier.matchParentSize())
            Row(Modifier.fillMaxSize().padding(ScenePadding)) {
                Column(Modifier.weight(1f).fillMaxHeight()) {
                    ordered.forEachIndexed { index, hero ->
                        Fighter(
                            hero = hero,
                            present = step >= 0,
                            striking = index == strikerIndex,
                            dimmed = live && index != strikerIndex,
                            lunge = lunge.value,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                ArcTarget(
                    arcName = arcName,
                    arcImageUrl = arcImageUrl,
                    flash = flash.value,
                    modifier = Modifier.width(ArcFrameWidth).fillMaxHeight(),
                )
            }
            if (live && striker != null) {
                Canvas(Modifier.matchParentSize()) {
                    val p = impact.value
                    if (p >= 1f) return@Canvas
                    val fade = 1f - p
                    val pad = ScenePadding.toPx()
                    val rowHeight = (size.height - 2 * pad) / count
                    val center = Offset(
                        size.width - pad - ArcFrameWidth.toPx() + 4.dp.toPx(),
                        pad + (strikerIndex + 0.5f) * rowHeight,
                    )
                    drawCircle(
                        color = semantic.coin.copy(alpha = fade),
                        radius = 10.dp.toPx() + 70.dp.toPx() * p,
                        center = center,
                        style = Stroke(width = 4.dp.toPx() * fade + 1.dp.toPx()),
                    )
                    for (i in 0 until 12) {
                        val angle = Math.toRadians(i * 30.0)
                        val dx = cos(angle).toFloat()
                        val dy = sin(angle).toFloat()
                        drawLine(
                            color = semantic.coin.copy(alpha = fade),
                            start = Offset(center.x + dx * (6.dp.toPx() + 20.dp.toPx() * p), center.y + dy * (6.dp.toPx() + 20.dp.toPx() * p)),
                            end = Offset(center.x + dx * (26.dp.toPx() + 30.dp.toPx() * p), center.y + dy * (26.dp.toPx() + 30.dp.toPx() * p)),
                            strokeWidth = 3.dp.toPx(),
                            cap = StrokeCap.Round,
                        )
                    }
                    drawCircle(
                        color = colors.primary.copy(alpha = fade),
                        radius = 14.dp.toPx() * fade,
                        center = center,
                    )
                    drawText(
                        textMeasurer = textMeasurer,
                        text = damageText,
                        topLeft = Offset(center.x + 10.dp.toPx(), center.y - 56.dp.toPx() - 22.dp.toPx() * p),
                        style = TextStyle(
                            fontFamily = AntonFamily,
                            fontSize = 26.sp,
                            color = semantic.coin.copy(alpha = fade),
                            shadow = Shadow(color = Color.Black, offset = Offset(2f, 2f), blurRadius = 4f),
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun BattleBackdrop(arcName: String, arcImageUrl: String?, modifier: Modifier) {
    Box(modifier.background(Color.Black)) {
        ArcImage(name = arcName, imageUrl = arcImageUrl, modifier = Modifier.matchParentSize().blur(12.dp))
        Box(Modifier.matchParentSize().background(Color.Black.copy(alpha = 0.55f)))
        Canvas(Modifier.matchParentSize()) {
            val gap = 14.dp.toPx()
            var x = -size.height
            while (x < size.width) {
                drawLine(
                    color = Color.White.copy(alpha = 0.10f),
                    start = Offset(x, size.height),
                    end = Offset(x + size.height, 0f),
                    strokeWidth = 1.dp.toPx(),
                )
                x += gap
            }
            val frontX = size.width - ScenePadding.toPx() - ArcFrameWidth.toPx() - 6.dp.toPx()
            drawLine(
                color = Color.White.copy(alpha = 0.35f),
                start = Offset(frontX, 0f),
                end = Offset(frontX, size.height),
                strokeWidth = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f)),
            )
        }
    }
}

@Composable
private fun Fighter(
    hero: RosterHero,
    present: Boolean,
    striking: Boolean,
    dimmed: Boolean,
    lunge: Float,
    modifier: Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val semantic = LocalRecruiterColors.current
    val reduced = rememberReducedMotion()
    val enter by animateFloatAsState(
        targetValue = if (present) 1f else 0f,
        animationSpec = motionTween(Motion.ROUND_ENTRY, reduced, EaseDecelerate),
        label = "enter",
    )
    val lungePx = with(LocalDensity.current) { LungeDistance.toPx() }
    Box(modifier, contentAlignment = Alignment.CenterStart) {
        Box(
            Modifier
                .size(FighterSize)
                .graphicsLayer {
                    val k = if (striking) lunge else 0f
                    translationX = k * lungePx
                    val scale = (0.8f + 0.2f * enter) * (1f + 0.12f * k)
                    scaleX = scale
                    scaleY = scale
                    alpha = enter * (if (dimmed) 0.55f else 1f)
                }
                .border(2.dp, semantic.ink)
                .background(colors.surface)
                .padding(2.dp),
        ) {
            HeroImage(name = hero.name, imageUrl = hero.imageUrl, modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun ArcTarget(arcName: String, arcImageUrl: String?, flash: Float, modifier: Modifier) {
    val semantic = LocalRecruiterColors.current
    Box(modifier.border(2.dp, semantic.ink)) {
        ArcImage(name = arcName, imageUrl = arcImageUrl, modifier = Modifier.fillMaxSize())
        if (flash > 0f) {
            Box(Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.45f * flash)))
        }
    }
}
