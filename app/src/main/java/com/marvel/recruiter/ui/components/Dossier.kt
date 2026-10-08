package com.marvel.recruiter.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.marvel.recruiter.R
import com.marvel.recruiter.ui.theme.Borders
import com.marvel.recruiter.ui.theme.LocalRecruiterColors
import com.marvel.recruiter.ui.theme.Sizes
import com.marvel.recruiter.ui.theme.Space
import androidx.compose.foundation.layout.statusBarsPadding

/** Ticks de canto de "pasta de dossiê" (decorativo, design.md 2.4). */
fun Modifier.dossierTicks(color: Color): Modifier = drawWithContent {
    drawContent()
    val len = Borders.tick.toPx()
    val s = Borders.medium.toPx()
    val w = size.width
    val h = size.height
    drawLine(color, Offset(0f, 0f), Offset(len, 0f), s)
    drawLine(color, Offset(0f, 0f), Offset(0f, len), s)
    drawLine(color, Offset(w, 0f), Offset(w - len, 0f), s)
    drawLine(color, Offset(w, 0f), Offset(w, len), s)
    drawLine(color, Offset(0f, h), Offset(len, h), s)
    drawLine(color, Offset(0f, h), Offset(0f, h - len), s)
    drawLine(color, Offset(w, h), Offset(w - len, h), s)
    drawLine(color, Offset(w, h), Offset(w, h - len), s)
}

/** Meios-tons: pontos em grade (decorativo). */
@Composable
fun HalftoneOverlay(color: Color, modifier: Modifier = Modifier) {
    val density = LocalDensity.current
    Canvas(modifier.fillMaxSize()) {
        val step = with(density) { 8.dp.toPx() }
        val radius = with(density) { 1.dp.toPx() }
        var row = 0
        var y = step / 2
        while (y < size.height) {
            var x = if (row % 2 == 0) step / 2 else step
            while (x < size.width) {
                drawCircle(color, radius = radius, center = Offset(x, y))
                x += step * 2
            }
            y += step
            row++
        }
    }
}

/** Linhas de varredura para o painel de missão no escuro (decorativo). */
@Composable
fun ScanlineOverlay(modifier: Modifier = Modifier) {
    val density = LocalDensity.current
    Canvas(modifier.fillMaxSize()) {
        val step = with(density) { 4.dp.toPx() }
        var y = 0f
        while (y < size.height) {
            drawLine(Color.White.copy(alpha = 0.02f), Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
            y += step
        }
    }
}

@Composable
fun TopBarDossier(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    actions: @Composable () -> Unit = {},
) {
    val colors = MaterialTheme.colorScheme
    Column(modifier.fillMaxWidth().background(colors.surface).statusBarsPadding()) {
        Row(
            modifier = Modifier.fillMaxWidth().height(56.dp).padding(horizontal = Space.s1),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (onBack != null) {
                Box(
                    Modifier
                        .size(Sizes.touchMin)
                        .clickable(role = Role.Button, onClickLabel = stringResource(R.string.action_back), onClick = onBack),
                    contentAlignment = Alignment.Center,
                ) {
                    BackGlyph(colors.onSurface)
                }
            } else {
                Spacer(Modifier.width(Space.s4))
            }
            Text(
                text = title,
                style = MaterialTheme.typography.displayMedium,
                color = colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f).padding(start = Space.s2),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(Space.s2), verticalAlignment = Alignment.CenterVertically) {
                actions()
            }
            Spacer(Modifier.width(Space.s2))
        }
        val ink = LocalRecruiterColors.current.ink
        Canvas(Modifier.fillMaxWidth().height(4.dp)) {
            val stroke = 2.dp.toPx()
            val y = size.height / 2
            drawLine(ink.copy(alpha = 0.15f), Offset(0f, y), Offset(size.width, y), strokeWidth = stroke)
            drawLine(colors.outline, Offset(0f, y - 4.dp.toPx()), Offset(0f, y + 4.dp.toPx()), strokeWidth = stroke)
            drawLine(colors.outline, Offset(size.width, y - 4.dp.toPx()), Offset(size.width, y + 4.dp.toPx()), strokeWidth = stroke)
        }
    }
}

enum class MainTab { ROSTER, POKEDEX, MISSIONS, SHOP }

@Composable
fun RecruiterBottomBar(selected: MainTab, onSelect: (MainTab) -> Unit) {
    val colors = MaterialTheme.colorScheme
    NavigationBar(containerColor = colors.surface) {
        NavigationBarItem(
            selected = selected == MainTab.ROSTER,
            onClick = { onSelect(MainTab.ROSTER) },
            icon = { RosterGlyph(if (selected == MainTab.ROSTER) colors.onPrimaryContainer else colors.onSurfaceVariant) },
            label = { Text(stringResource(R.string.tab_roster), style = MaterialTheme.typography.labelMedium) },
            colors = tabColors(colors),
        )
        NavigationBarItem(
            selected = selected == MainTab.POKEDEX,
            onClick = { onSelect(MainTab.POKEDEX) },
            icon = { PokedexGlyph(if (selected == MainTab.POKEDEX) colors.onPrimaryContainer else colors.onSurfaceVariant) },
            label = { Text(stringResource(R.string.tab_pokedex), style = MaterialTheme.typography.labelMedium) },
            colors = tabColors(colors),
        )
        NavigationBarItem(
            selected = selected == MainTab.MISSIONS,
            onClick = { onSelect(MainTab.MISSIONS) },
            icon = { MissionGlyph(if (selected == MainTab.MISSIONS) colors.onPrimaryContainer else colors.onSurfaceVariant) },
            label = { Text(stringResource(R.string.tab_missions), style = MaterialTheme.typography.labelMedium) },
            colors = tabColors(colors),
        )
        NavigationBarItem(
            selected = selected == MainTab.SHOP,
            onClick = { onSelect(MainTab.SHOP) },
            icon = { ShopGlyph(if (selected == MainTab.SHOP) colors.onPrimaryContainer else colors.onSurfaceVariant) },
            label = { Text(stringResource(R.string.tab_shop), style = MaterialTheme.typography.labelMedium) },
            colors = tabColors(colors),
        )
    }
}

@Composable
private fun tabColors(colors: androidx.compose.material3.ColorScheme) =
    NavigationBarItemDefaults.colors(
        selectedIconColor = colors.onPrimaryContainer,
        selectedTextColor = colors.onSurface,
        indicatorColor = colors.primaryContainer,
        unselectedIconColor = colors.onSurfaceVariant,
        unselectedTextColor = colors.onSurfaceVariant,
    )
