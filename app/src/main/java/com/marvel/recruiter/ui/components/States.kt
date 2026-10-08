package com.marvel.recruiter.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.marvel.recruiter.ui.theme.LocalRecruiterColors
import com.marvel.recruiter.ui.theme.Space

/** Estado vazio: silhueta em meios-tons, título, texto e ação opcional (design.md 3.16). */
@Composable
fun EmptyState(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    icon: @Composable () -> Unit = {},
) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = modifier.fillMaxWidth().padding(Space.s4),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Space.s2),
    ) {
        Box(Modifier.height(96.dp), contentAlignment = Alignment.Center) { icon() }
        Text(title, style = MaterialTheme.typography.titleLarge, color = colors.onSurface, textAlign = TextAlign.Center)
        Text(body, style = MaterialTheme.typography.bodyLarge, color = colors.onSurfaceVariant, textAlign = TextAlign.Center)
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(Space.s2))
            PrimaryButton(text = actionLabel, onClick = onAction)
        }
    }
}

/** Erro com botão de nova tentativa manual (RF-20: sem retry automático). */
@Composable
fun ErrorState(
    title: String,
    body: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    detail: String? = null,
) {
    val colors = MaterialTheme.colorScheme
    val semantic = LocalRecruiterColors.current
    Column(
        modifier = modifier.fillMaxWidth().padding(Space.s4),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Space.s3),
    ) {
        AlertGlyph(semantic.danger, size = 48.dp)
        Text(title, style = MaterialTheme.typography.titleLarge, color = colors.onSurface, textAlign = TextAlign.Center)
        Text(body, style = MaterialTheme.typography.bodyLarge, color = colors.onSurfaceVariant, textAlign = TextAlign.Center)
        PrimaryButton(text = actionLabel, onClick = onAction)
        if (detail != null) {
            Text(detail, style = MaterialTheme.typography.labelSmall, color = colors.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}

/** Progresso indeterminado com texto da etapa (nunca finge progresso). */
@Composable
fun SeedProgress(
    stepLabel: String,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = Space.s4)) {
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth().height(4.dp),
            color = colors.primary,
            trackColor = colors.surfaceVariant,
        )
        Spacer(Modifier.height(Space.s2))
        Text(stepLabel, style = MaterialTheme.typography.labelMedium, color = colors.onSurfaceVariant)
    }
}
