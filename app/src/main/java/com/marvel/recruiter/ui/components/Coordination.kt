package com.marvel.recruiter.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import com.marvel.recruiter.R
import com.marvel.recruiter.game.coordinationBonus
import com.marvel.recruiter.ui.theme.LocalRecruiterColors
import com.marvel.recruiter.ui.theme.Space
import kotlin.math.roundToInt

/**
 * Barra do minijogo de coordenação. [position] em 0..1 (marcador); a faixa central dá bônus máximo,
 * as faixas laterais bônus menor. [frozen] indica que a jogada já foi travada.
 */
@Composable
fun CoordinationMeter(
    position: Double,
    frozen: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val semantic = LocalRecruiterColors.current
    val bonusPercent = (coordinationBonus(position) * 100).roundToInt()
    val bonusLabel = if (bonusPercent > 0) {
        stringResource(R.string.coordination_bonus, bonusPercent)
    } else {
        stringResource(R.string.coordination_none)
    }
    val title = stringResource(if (frozen) R.string.coordination_locked else R.string.coordination_title)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clearAndSetSemantics { contentDescription = "$title. $bonusLabel" },
    ) {
        Row {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = colors.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = bonusLabel,
                style = MaterialTheme.typography.labelMedium,
                color = if (bonusPercent > 0) semantic.success else colors.onSurfaceVariant,
            )
        }
        Spacer(Modifier.height(Space.s2))
        Canvas(Modifier.fillMaxWidth().height(20.dp)) {
            val h = size.height
            val w = size.width
            val radius = CornerRadius(h / 2, h / 2)
            drawRoundRect(color = colors.surfaceVariant, size = size, cornerRadius = radius)
            drawRoundRect(
                color = colors.outline.copy(alpha = 0.35f),
                topLeft = Offset(w * 0.30f, 0f),
                size = Size(w * 0.40f, h),
                cornerRadius = radius,
            )
            drawRoundRect(
                color = semantic.success.copy(alpha = 0.55f),
                topLeft = Offset(w * 0.42f, 0f),
                size = Size(w * 0.16f, h),
                cornerRadius = radius,
            )
            val x = (position.coerceIn(0.0, 1.0) * w).toFloat()
            drawLine(
                color = colors.onSurface,
                start = Offset(x, -2.dp.toPx()),
                end = Offset(x, h + 2.dp.toPx()),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }
    }
}
