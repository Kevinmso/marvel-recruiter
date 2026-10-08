package com.marvel.recruiter.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.marvel.recruiter.R
import com.marvel.recruiter.ui.components.CooldownBadge
import com.marvel.recruiter.ui.components.EmptyState
import com.marvel.recruiter.ui.components.FactionChip
import com.marvel.recruiter.ui.components.RarityTag
import com.marvel.recruiter.ui.components.HeroImage
import com.marvel.recruiter.ui.components.LockGlyph
import com.marvel.recruiter.ui.components.MissionCard
import com.marvel.recruiter.ui.components.PowerBar
import com.marvel.recruiter.ui.components.SeedProgress
import com.marvel.recruiter.ui.components.StampLabel
import com.marvel.recruiter.ui.components.TopBarDossier
import com.marvel.recruiter.ui.theme.Space
import com.marvel.recruiter.ui.util.formatDecimal
import com.marvel.recruiter.ui.util.formatInt
import com.marvel.recruiter.viewmodel.HeroProfileUiState
import com.marvel.recruiter.viewmodel.HeroProfileViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.math.roundToInt

/** Tela 5: dossiê do herói. Bloqueado (não recrutado) mostra só atributos (design.md 8.1). */
@Composable
fun HeroProfileScreen(
    cvId: Long,
    onBack: () -> Unit,
    onGoRoster: () -> Unit,
    onArcClick: (Long) -> Unit,
    viewModel: HeroProfileViewModel = koinViewModel(parameters = { parametersOf(cvId) }),
) {
    val state by viewModel.state.collectAsState()
    HeroProfileContent(
        state = state,
        onBack = onBack,
        onGoRoster = onGoRoster,
        onArcClick = onArcClick,
    )
}

@Composable
fun HeroProfileContent(
    state: HeroProfileUiState,
    onBack: () -> Unit,
    onGoRoster: () -> Unit,
    onArcClick: (Long) -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val detail = state.detail

    Scaffold(
        containerColor = colors.background,
        topBar = { TopBarDossier(title = stringResource(R.string.profile_title), onBack = onBack) },
    ) { padding ->
        when {
            state.loading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                SeedProgress(stringResource(R.string.result_calculating))
            }
            detail == null -> EmptyState(
                title = stringResource(R.string.profile_not_found),
                body = "",
                modifier = Modifier.padding(padding),
                actionLabel = stringResource(R.string.action_go_roster),
                onAction = onGoRoster,
            )
            else -> {
                val hero = detail.hero
                val recruited = detail.recruited
                val resting = detail.availableAt != null
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    verticalArrangement = Arrangement.spacedBy(Space.s4),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = Space.s6),
                ) {
                    item {
                        Box(Modifier.fillMaxWidth().height(360.dp)) {
                            HeroImage(
                                name = hero.name,
                                imageUrl = hero.imageUrl,
                                modifier = Modifier.fillMaxSize(),
                                grayscale = !recruited,
                            )
                            Box(
                                Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .background(Brush.verticalGradient(0f to Color.Transparent, 1f to colors.background)),
                            )
                            StampLabel(
                                text = when {
                                    !recruited -> stringResource(R.string.stamp_locked)
                                    resting -> stringResource(R.string.stamp_resting)
                                    else -> stringResource(R.string.stamp_recruited)
                                },
                                color = if (recruited) colors.primary else colors.secondary,
                                rotation = -4f,
                                modifier = Modifier.align(Alignment.TopEnd).padding(Space.s4),
                            )
                            Column(
                                modifier = Modifier.align(Alignment.BottomStart).padding(Space.s4),
                            ) {
                                Text(
                                    text = hero.name,
                                    style = MaterialTheme.typography.displayLarge,
                                    color = colors.onBackground,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                hero.realName?.takeIf { it.isNotBlank() }?.let {
                                    Text(
                                        text = stringResource(R.string.profile_real_name, it),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = colors.onSurfaceVariant,
                                    )
                                }
                                hero.aliases?.takeIf { it.isNotBlank() }?.let {
                                    Text(
                                        text = stringResource(R.string.profile_aliases, it.replace("\n", ", ")),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = colors.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                    item {
                        Column(Modifier.padding(horizontal = Space.s4), verticalArrangement = Arrangement.spacedBy(Space.s3)) {
                            RarityTag(strength = 0.6 * hero.adjustedPower + 0.4 * hero.veterancy)
                            SectionTitle(stringResource(R.string.profile_attributes))
                            PowerBar(
                                label = stringResource(R.string.hero_stat_power),
                                value = hero.adjustedPower,
                                valueText = formatInt(hero.adjustedPower.roundToInt()),
                                disabled = !recruited,
                            )
                            PowerBar(
                                label = stringResource(R.string.hero_stat_veterancy),
                                value = hero.veterancy,
                                valueText = formatDecimal(hero.veterancy, 0),
                                disabled = !recruited,
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(Space.s2)) {
                                InfoChip(stringResource(R.string.hero_stat_powers_count, hero.numPowers))
                                InfoChip(stringResource(R.string.hero_stat_appearances, hero.numAppearances))
                            }
                        }
                    }
                    if (recruited) {
                        item {
                            Column(Modifier.padding(horizontal = Space.s4), verticalArrangement = Arrangement.spacedBy(Space.s2)) {
                                SectionTitle(stringResource(R.string.profile_dossier))
                                val deck = hero.deck?.takeIf { it.isNotBlank() }
                                Text(
                                    text = deck ?: stringResource(R.string.profile_no_summary),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = colors.onSurface,
                                )
                                if (deck != null) {
                                    Text(
                                        text = stringResource(R.string.original_text_caption),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = colors.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                        item {
                            Column(Modifier.padding(horizontal = Space.s4), verticalArrangement = Arrangement.spacedBy(Space.s2)) {
                                SectionTitle(stringResource(R.string.profile_teams))
                                Row(
                                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(Space.s2),
                                ) {
                                    detail.teamNames.forEach { FactionChip(it) }
                                }
                            }
                        }
                        item {
                            Column(Modifier.padding(horizontal = Space.s4), verticalArrangement = Arrangement.spacedBy(Space.s2)) {
                                SectionTitle(stringResource(R.string.profile_arcs))
                                if (detail.arcs.isEmpty()) {
                                    Text(
                                        text = stringResource(R.string.profile_no_arcs),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = colors.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                        items(detail.arcs, key = { it.cvId }) { arc ->
                            Box(Modifier.padding(horizontal = Space.s4)) {
                                MissionCard(arc = arc, onClick = { onArcClick(arc.cvId) }, compact = true)
                            }
                        }
                    } else {
                        item {
                            EmptyState(
                                title = stringResource(R.string.stamp_locked),
                                body = stringResource(R.string.profile_locked_body),
                                actionLabel = stringResource(R.string.action_go_roster),
                                onAction = onGoRoster,
                                icon = { LockGlyph(colors.secondary, size = 48.dp) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text = text, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
}

@Composable
private fun InfoChip(text: String) {
    Box(
        Modifier
            .height(32.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
            .padding(horizontal = Space.s2),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}
