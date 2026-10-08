package com.marvel.recruiter.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.marvel.recruiter.R
import com.marvel.recruiter.ui.components.CoordinationMeter
import com.marvel.recruiter.ui.components.EmptyState
import com.marvel.recruiter.ui.components.ForceBar
import com.marvel.recruiter.ui.components.GhostButton
import com.marvel.recruiter.ui.components.HeroCard
import com.marvel.recruiter.ui.components.MissionCard
import com.marvel.recruiter.ui.components.PrimaryButton
import com.marvel.recruiter.ui.components.StatChip
import com.marvel.recruiter.ui.components.TeamSlot
import com.marvel.recruiter.ui.components.TopBarDossier
import com.marvel.recruiter.ui.theme.Space
import com.marvel.recruiter.ui.util.formatDecimal
import com.marvel.recruiter.viewmodel.MAX_TEAM
import com.marvel.recruiter.viewmodel.SquadBlock
import com.marvel.recruiter.viewmodel.SquadUiState
import com.marvel.recruiter.viewmodel.SquadViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.math.roundToInt

/** Tela 2: escolha de 3 a 5 heróis disponíveis para um arco (RF-08/09). */
@Composable
fun SquadScreen(
    arcCvId: Long,
    onStart: (arcCvId: Long, heroIds: List<Long>, coordinationPosition: Double) -> Unit,
    onBack: () -> Unit,
    onHeroClick: (Long) -> Unit,
    onGoMissions: () -> Unit,
    onGoRoster: () -> Unit,
    viewModel: SquadViewModel = koinViewModel(parameters = { parametersOf(arcCvId) }),
) {
    val state by viewModel.state.collectAsState()
    SquadContent(
        state = state,
        onToggle = viewModel::toggle,
        onStart = onStart,
        onBack = onBack,
        onHeroClick = onHeroClick,
        onGoMissions = onGoMissions,
        onGoRoster = onGoRoster,
    )
}

@Composable
fun SquadContent(
    state: SquadUiState,
    onToggle: (Long) -> Unit,
    onStart: (arcCvId: Long, heroIds: List<Long>, coordinationPosition: Double) -> Unit,
    onBack: () -> Unit,
    onHeroClick: (Long) -> Unit,
    onGoMissions: () -> Unit,
    onGoRoster: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val columns = if (LocalConfiguration.current.screenWidthDp >= 600) 3 else 2
    val selectedHeroes = state.selected.mapNotNull { id -> state.heroes.firstOrNull { it.cvId == id } }
    val reason = when (state.block) {
        SquadBlock.NO_ARC -> stringResource(R.string.squad_need_arc)
        SquadBlock.NOT_ENOUGH_AVAILABLE -> stringResource(R.string.squad_not_enough_available)
        SquadBlock.NEED_MORE -> stringResource(R.string.squad_need_three)
        null -> null
    }

    Scaffold(
        containerColor = colors.background,
        topBar = { TopBarDossier(title = stringResource(R.string.squad_title), onBack = onBack) },
        bottomBar = {
            SquadFooter(
                selectedCount = state.selected.size,
                canStart = state.canStart,
                reason = reason,
                onStart = { position -> state.arc?.let { onStart(it.cvId, state.selected, position) } },
            )
        },
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = Space.s4, top = Space.s4, end = Space.s4, bottom = Space.s4),
            horizontalArrangement = Arrangement.spacedBy(Space.s3),
            verticalArrangement = Arrangement.spacedBy(Space.s3),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                val arc = state.arc
                if (arc == null) {
                    Column(verticalArrangement = Arrangement.spacedBy(Space.s2)) {
                        Text(
                            text = stringResource(R.string.squad_no_arc_title),
                            style = MaterialTheme.typography.bodyLarge,
                            color = colors.onSurfaceVariant,
                        )
                        GhostButton(text = stringResource(R.string.action_view_missions), onClick = onGoMissions)
                    }
                } else {
                    MissionCard(arc = arc, onClick = {}, compact = true)
                }
            }
            if (state.arc != null) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    StrengthPanel(
                        strength = state.estimate?.total ?: 0.0,
                        difficulty = state.arc?.difficulty ?: 0.0,
                        base = state.estimate?.base ?: 0.0,
                        synergy = state.estimate?.synergy ?: 0,
                        faction = state.estimate?.faction ?: 0,
                    )
                }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(horizontalArrangement = Arrangement.spacedBy(Space.s2)) {
                    repeat(MAX_TEAM) { index ->
                        val hero = selectedHeroes.getOrNull(index)
                        TeamSlot(
                            hero = hero,
                            onRemove = { hero?.let { onToggle(it.cvId) } },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    Text(
                        text = stringResource(R.string.squad_available_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = colors.onSurface,
                    )
                    Text(
                        text = stringResource(R.string.squad_hint),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.onSurfaceVariant,
                    )
                }
            }
            if (!state.loading && state.heroes.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    EmptyState(
                        title = stringResource(R.string.roster_empty_title),
                        body = stringResource(R.string.msg_recruit_first),
                        actionLabel = stringResource(R.string.action_go_roster),
                        onAction = onGoRoster,
                    )
                }
            }
            items(state.heroes, key = { it.cvId }) { hero ->
                val available = hero.availableAt <= state.now
                HeroCard(
                    hero = hero,
                    now = state.now,
                    selected = hero.cvId in state.selected,
                    enabled = available,
                    onClick = { onToggle(hero.cvId) },
                    onProfileClick = { onHeroClick(hero.cvId) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

/** Rodapé fixo: contagem, minijogo de coordenação e INICIAR MISSÃO. O marcador trava ao iniciar. */
@Composable
private fun SquadFooter(
    selectedCount: Int,
    canStart: Boolean,
    reason: String?,
    onStart: (coordinationPosition: Double) -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val transition = rememberInfiniteTransition(label = "coordination")
    val sweep by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "sweep",
    )
    var frozen by remember { mutableStateOf<Double?>(null) }

    Surface(color = colors.surface, tonalElevation = 0.dp) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(Space.s4),
        ) {
            Text(
                text = stringResource(R.string.squad_selected_count, selectedCount),
                style = MaterialTheme.typography.labelMedium,
                color = colors.onSurfaceVariant,
            )
            Spacer(Modifier.height(Space.s2))
            CoordinationMeter(position = frozen ?: sweep.toDouble(), frozen = frozen != null)
            Spacer(Modifier.height(Space.s3))
            PrimaryButton(
                text = stringResource(R.string.squad_start),
                enabled = canStart && frozen == null,
                supportingText = reason,
                onClick = {
                    val position = sweep.toDouble()
                    frozen = position
                    onStart(position)
                },
            )
        }
    }
}

@Composable
private fun StrengthPanel(
    strength: Double,
    difficulty: Double,
    base: Double,
    synergy: Int,
    faction: Int,
) {
    val colors = MaterialTheme.colorScheme
    val label = stringResource(R.string.result_force_label, formatDecimal(strength))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surface, RoundedCornerShape(12.dp))
            .padding(Space.s4),
        verticalArrangement = Arrangement.spacedBy(Space.s2),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.squad_strength),
                style = MaterialTheme.typography.titleMedium,
                color = colors.onSurface,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = formatDecimal(strength),
                style = com.marvel.recruiter.ui.theme.StatValue,
                color = colors.onSurface,
            )
        }
        ForceBar(strength = strength, difficulty = difficulty, label = label)
        Text(
            text = stringResource(R.string.squad_vs, formatDecimal(difficulty)),
            style = MaterialTheme.typography.labelMedium,
            color = colors.onSurfaceVariant,
        )
        if (synergy > 0 || faction > 0) {
            Text(
                text = stringResource(R.string.squad_team_bonus_active),
                style = MaterialTheme.typography.labelMedium,
                color = colors.primary,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(Space.s1)) {
            StatChip(label = stringResource(R.string.squad_base), value = formatDecimal(base, 0))
            if (synergy > 0) StatChip(label = stringResource(R.string.squad_synergy, synergy), value = "", highlight = true)
            if (faction > 0) StatChip(label = stringResource(R.string.squad_faction, faction), value = "", highlight = true)
        }
    }
}
