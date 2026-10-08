package com.marvel.recruiter.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marvel.recruiter.R
import com.marvel.recruiter.ui.theme.Borders
import com.marvel.recruiter.ui.theme.EaseDecelerate
import com.marvel.recruiter.ui.theme.LocalRecruiterColors
import com.marvel.recruiter.ui.theme.Motion
import com.marvel.recruiter.ui.theme.Space
import com.marvel.recruiter.ui.theme.StatValue
import com.marvel.recruiter.ui.theme.StatValueSmall
import com.marvel.recruiter.ui.theme.motionTween
import com.marvel.recruiter.ui.theme.rememberReducedMotion
import com.marvel.recruiter.ui.util.formatInt
import kotlin.math.ceil
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

/** Barra linear 0..1 com trilho; anima o preenchimento (design.md 3.2 / 3.12). */
@Composable
fun Meter(
    fraction: Float,
    color: Color,
    track: Color,
    height: Dp,
    modifier: Modifier = Modifier,
) {
    val reduced = rememberReducedMotion()
    val animated by animateFloatAsState(
        targetValue = fraction.coerceIn(0f, 1f),
        animationSpec = motionTween(Motion.FILL, reduced, EaseDecelerate),
        label = "meter",
    )
    Canvas(modifier.fillMaxWidth().height(height)) {
        val radius = CornerRadius(size.height / 2)
        drawRoundRect(track, cornerRadius = radius)
        if (animated > 0f) {
            drawRoundRect(color, size = Size(size.width * animated, size.height), cornerRadius = radius)
        }
    }
}

@Composable
fun PowerBar(
    label: String,
    value: Double,
    valueText: String,
    modifier: Modifier = Modifier,
    disabled: Boolean = false,
) {
    val colors = MaterialTheme.colorScheme
    val description = "$label $valueText de 100"
    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                progressBarRangeInfo = ProgressBarRangeInfo(value.toFloat(), 0f..100f)
                contentDescription = description
            },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = colors.onSurfaceVariant)
            Spacer(Modifier.weight(1f))
            Text(valueText, style = StatValueSmall, color = colors.onSurface)
        }
        Spacer(Modifier.height(Space.s1))
        Meter(
            fraction = (value / 100.0).toFloat(),
            color = if (disabled) colors.outline else colors.primary,
            track = colors.surfaceVariant,
            height = 8.dp,
        )
    }
}

@Composable
fun StatChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    highlight: Boolean = false,
) {
    val colors = MaterialTheme.colorScheme
    val bg = if (highlight) colors.primaryContainer else colors.surfaceVariant
    val valueColor = if (highlight) colors.onPrimaryContainer else colors.onSurface
    Row(
        modifier = modifier
            .height(32.dp)
            .background(bg, RoundedCornerShape(8.dp))
            .padding(horizontal = Space.s2),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = colors.onSurfaceVariant)
        Spacer(Modifier.width(Space.s1))
        Text(value, style = StatValueSmall, color = valueColor)
    }
}

/** Contagem regressiva de descanso; texto sempre visível. Vira "DISPONÍVEL" quando passa. */
@Composable
fun CooldownBadge(
    availableAt: Long,
    now: Long,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val semantics = LocalRecruiterColors.current
    val remaining = availableAt - now
    if (remaining <= 0L) {
        Row(
            modifier = modifier
                .height(28.dp)
                .background(semantics.success.copy(alpha = 0.16f), RoundedCornerShape(50))
                .border(Borders.thin, semantics.success, RoundedCornerShape(50))
                .padding(horizontal = Space.s2),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CheckGlyph(semantics.success, size = 14.dp)
            Spacer(Modifier.width(Space.s1))
            Text(stringResource(R.string.cooldown_ready), style = StatValueSmall.copy(fontSize = 12.sp), color = colors.onSurface)
        }
        return
    }
    val minutes = ceil(remaining / 60_000.0).toInt()
    val text = if (minutes >= 60) {
        stringResource(R.string.cooldown_hours_minutes, minutes / 60, minutes % 60)
    } else {
        stringResource(R.string.cooldown_minutes, minutes)
    }
    Row(
        modifier = modifier
            .height(28.dp)
            .background(semantics.cooldown.copy(alpha = 0.16f), RoundedCornerShape(50))
            .border(Borders.thin, semantics.cooldown, RoundedCornerShape(50))
            .padding(horizontal = Space.s2),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HourglassGlyph(semantics.cooldown, size = 14.dp)
        Spacer(Modifier.width(Space.s1))
        Text(text, style = StatValueSmall.copy(fontSize = 12.sp), color = colors.onSurface)
    }
}

@Composable
fun CoinPill(coins: Int, modifier: Modifier = Modifier) {
    val semantics = LocalRecruiterColors.current
    val shown by animateIntAsState(coins, animationSpec = motionTween(300, rememberReducedMotion()), label = "coins")
    val valueText = formatInt(shown)
    val coinDescription = stringResource(R.string.coin_label, valueText)
    Row(
        modifier = modifier
            .height(36.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(50))
            .padding(start = Space.s1, end = Space.s3)
            .semantics { contentDescription = coinDescription },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CoinGlyph(semantics.coin, MaterialTheme.colorScheme.surface, size = 28.dp)
        Spacer(Modifier.width(Space.s2))
        Text(valueText, style = StatValueSmall, color = semantics.coin)
    }
}

@Composable
fun XpPill(xp: Int, modifier: Modifier = Modifier) {
    val semantics = LocalRecruiterColors.current
    val shown by animateIntAsState(xp, animationSpec = motionTween(300, rememberReducedMotion()), label = "xp")
    val valueText = formatInt(shown)
    val xpDescription = stringResource(R.string.xp_label, valueText)
    Row(
        modifier = modifier
            .height(36.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(50))
            .padding(start = Space.s2, end = Space.s3)
            .semantics { contentDescription = xpDescription },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        XpGlyph(semantics.xp, size = 20.dp)
        Spacer(Modifier.width(Space.s2))
        Text(stringResource(R.string.xp_label, valueText), style = StatValueSmall, color = semantics.xp)
    }
}

/** Carimbo de quadrinho: texto caixa alta com borda e leve rotação fixa. */
@Composable
fun StampLabel(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    rotation: Float = -4f,
) {
    Box(
        modifier = modifier
            .graphicsLayer { rotationZ = rotation; alpha = 0.92f }
            .border(Borders.medium, color, RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
            .padding(horizontal = Space.s2, vertical = 2.dp),
    ) {
        Text(text, style = MaterialTheme.typography.labelMedium, color = color)
    }
}
