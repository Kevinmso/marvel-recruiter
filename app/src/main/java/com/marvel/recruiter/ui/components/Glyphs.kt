package com.marvel.recruiter.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Ícones desenhados à mão: o projeto não depende de material-icons.

@Composable
fun BackGlyph(color: Color, size: Dp = 24.dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        drawLine(color, Offset(w * 0.8f, h / 2), Offset(w * 0.2f, h / 2), strokeWidth = stroke.width, cap = StrokeCap.Round)
        drawLine(color, Offset(w * 0.2f, h / 2), Offset(w * 0.5f, h * 0.2f), strokeWidth = stroke.width, cap = StrokeCap.Round)
        drawLine(color, Offset(w * 0.2f, h / 2), Offset(w * 0.5f, h * 0.8f), strokeWidth = stroke.width, cap = StrokeCap.Round)
    }
}

@Composable
fun CheckGlyph(color: Color, size: Dp = 16.dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val width = 2.dp.toPx()
        drawLine(color, Offset(w * 0.2f, h * 0.55f), Offset(w * 0.42f, h * 0.75f), strokeWidth = width, cap = StrokeCap.Round)
        drawLine(color, Offset(w * 0.42f, h * 0.75f), Offset(w * 0.8f, h * 0.28f), strokeWidth = width, cap = StrokeCap.Round)
    }
}

@Composable
fun CloseGlyph(color: Color, size: Dp = 16.dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val width = 2.dp.toPx()
        drawLine(color, Offset(w * 0.2f, h * 0.2f), Offset(w * 0.8f, h * 0.8f), strokeWidth = width, cap = StrokeCap.Round)
        drawLine(color, Offset(w * 0.8f, h * 0.2f), Offset(w * 0.2f, h * 0.8f), strokeWidth = width, cap = StrokeCap.Round)
    }
}

@Composable
fun AlertGlyph(color: Color, size: Dp = 24.dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val path = Path().apply {
            moveTo(w / 2, h * 0.1f)
            lineTo(w * 0.95f, h * 0.9f)
            lineTo(w * 0.05f, h * 0.9f)
            close()
        }
        drawPath(path, color, style = Stroke(width = 2.dp.toPx()))
        drawLine(color, Offset(w / 2, h * 0.4f), Offset(w / 2, h * 0.65f), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
        drawCircle(color, radius = 1.4.dp.toPx(), center = Offset(w / 2, h * 0.78f))
    }
}

@Composable
fun LockGlyph(color: Color, size: Dp = 48.dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        drawArc(
            color,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.28f, h * 0.12f),
            size = androidx.compose.ui.geometry.Size(w * 0.44f, h * 0.44f),
            style = Stroke(width = 3.dp.toPx()),
        )
        drawRectCompat(color, w * 0.18f, h * 0.45f, w * 0.64f, h * 0.45f)
    }
}

@Composable
fun HourglassGlyph(color: Color, size: Dp = 14.dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val top = Path().apply { moveTo(w * 0.15f, h * 0.05f); lineTo(w * 0.85f, h * 0.05f); lineTo(w * 0.5f, h * 0.5f); close() }
        val bottom = Path().apply { moveTo(w * 0.15f, h * 0.95f); lineTo(w * 0.85f, h * 0.95f); lineTo(w * 0.5f, h * 0.5f); close() }
        drawPath(top, color, style = Stroke(width = 1.4.dp.toPx()))
        drawPath(bottom, color, style = Stroke(width = 1.4.dp.toPx()))
    }
}

@Composable
fun RosterGlyph(color: Color, size: Dp = 24.dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        drawCircle(color, radius = w * 0.18f, center = Offset(w / 2, h * 0.32f))
        drawArc(
            color,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.18f, h * 0.55f),
            size = androidx.compose.ui.geometry.Size(w * 0.64f, h * 0.6f),
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round),
        )
    }
}

@Composable
fun MissionGlyph(color: Color, size: Dp = 24.dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val diamond = Path().apply {
            moveTo(w / 2, h * 0.08f)
            lineTo(w * 0.92f, h / 2)
            lineTo(w / 2, h * 0.92f)
            lineTo(w * 0.08f, h / 2)
            close()
        }
        drawPath(diamond, color, style = Stroke(width = 2.dp.toPx()))
        drawCircle(color, radius = w * 0.1f, center = Offset(w / 2, h / 2))
    }
}

/** Ícone de moeda: círculo cheio com marca de cunhagem. */
@Composable
fun CoinGlyph(color: Color, markColor: Color, size: Dp = 24.dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) {
        val w = this.size.width
        drawCircle(color, radius = w / 2)
        drawCircle(markColor, radius = w * 0.28f, style = Stroke(width = 1.5.dp.toPx()))
    }
}

/** Losango com barra: símbolo do placar de XP. */
@Composable
fun XpGlyph(color: Color, size: Dp = 24.dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val diamond = Path().apply {
            moveTo(w / 2, 0f)
            lineTo(w, h / 2)
            lineTo(w / 2, h)
            lineTo(0f, h / 2)
            close()
        }
        drawPath(diamond, color)
        drawLine(Color.White.copy(alpha = 0.9f), Offset(w * 0.3f, h / 2), Offset(w * 0.7f, h / 2), strokeWidth = 2.dp.toPx())
    }
}

/** Grade 2×2 de cartas: símbolo da Pokédex. Uma carta preenchida marca o "descoberto". */
@Composable
fun PokedexGlyph(color: Color, size: Dp = 24.dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cell = w * 0.38f
        val stroke = 2.dp.toPx()
        val xs = listOf(w * 0.08f, w * 0.54f)
        val ys = listOf(h * 0.08f, h * 0.54f)
        xs.forEachIndexed { col, x ->
            ys.forEachIndexed { row, y ->
                if (col == 1 && row == 1) {
                    drawRect(color, topLeft = Offset(x, y), size = androidx.compose.ui.geometry.Size(cell, cell))
                } else {
                    drawRect(
                        color,
                        topLeft = Offset(x + stroke / 2, y + stroke / 2),
                        size = androidx.compose.ui.geometry.Size(cell - stroke, cell - stroke),
                        style = Stroke(width = stroke),
                    )
                }
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawRectCompat(
    color: Color,
    left: Float,
    top: Float,
    width: Float,
    height: Float,
) {
    drawRect(color, topLeft = Offset(left, top), size = androidx.compose.ui.geometry.Size(width, height))
}

/** Ícone da loja: sacola com alça. */
@Composable
fun ShopGlyph(color: Color, size: Dp = 24.dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        drawRoundRect(
            color,
            topLeft = Offset(w * 0.12f, h * 0.36f),
            size = Size(w * 0.76f, h * 0.56f),
            cornerRadius = CornerRadius(w * 0.08f),
            style = stroke,
        )
        drawArc(
            color,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.32f, h * 0.12f),
            size = Size(w * 0.36f, h * 0.4f),
            style = stroke,
        )
    }
}
