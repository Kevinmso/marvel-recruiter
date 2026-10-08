package com.marvel.recruiter.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.marvel.recruiter.R
import com.marvel.recruiter.ui.components.EmptyState
import com.marvel.recruiter.ui.components.GhostButton
import com.marvel.recruiter.ui.components.MainTab
import com.marvel.recruiter.ui.components.MissionCard
import com.marvel.recruiter.ui.components.RecruiterBottomBar
import com.marvel.recruiter.ui.components.TopBarDossier
import com.marvel.recruiter.ui.theme.Space
import com.marvel.recruiter.viewmodel.DifficultyFilter
import com.marvel.recruiter.viewmodel.MissionsUiState
import com.marvel.recruiter.viewmodel.MissionsViewModel
import org.koin.androidx.compose.koinViewModel

/** Tela 3: arcos desbloqueados por heróis recrutados, com dificuldade (RF-11/19). */
@Composable
fun MissionsScreen(
    onArcClick: (Long) -> Unit,
    onGoRoster: () -> Unit,
    onTabSelected: (MainTab) -> Unit,
    viewModel: MissionsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    MissionsContent(
        state = state,
        onArcClick = onArcClick,
        onGoRoster = onGoRoster,
        onTabSelected = onTabSelected,
        onFilterChange = viewModel::setFilter,
    )
}

@Composable
fun MissionsContent(
    state: MissionsUiState,
    onArcClick: (Long) -> Unit,
    onGoRoster: () -> Unit,
    onTabSelected: (MainTab) -> Unit,
    onFilterChange: (DifficultyFilter) -> Unit,
) {
    val colors = MaterialTheme.colorScheme

    Scaffold(
        containerColor = colors.background,
        topBar = { TopBarDossier(title = stringResource(R.string.missions_title)) },
        bottomBar = { RecruiterBottomBar(selected = MainTab.MISSIONS, onSelect = onTabSelected) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = Space.s4, top = Space.s3, end = Space.s4, bottom = Space.s4),
            verticalArrangement = Arrangement.spacedBy(Space.s3),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(Space.s2),
                ) {
                    FilterOption(DifficultyFilter.ALL, stringResource(R.string.filter_all), state.filter, onFilterChange)
                    FilterOption(DifficultyFilter.EASY, stringResource(R.string.difficulty_easy), state.filter, onFilterChange)
                    FilterOption(DifficultyFilter.MEDIUM, stringResource(R.string.difficulty_medium), state.filter, onFilterChange)
                    FilterOption(DifficultyFilter.EPIC, stringResource(R.string.difficulty_epic), state.filter, onFilterChange)
                }
            }
            if (!state.loading && state.unlockedCount > 0) {
                item {
                    Text(
                        text = stringResource(R.string.missions_summary, state.unlockedCount, state.restingHeroes),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colors.surfaceVariant, RoundedCornerShape(8.dp))
                            .padding(Space.s3),
                    )
                }
            }
            when {
                state.loading -> items(3) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(colors.surfaceVariant, RoundedCornerShape(12.dp)),
                    )
                }
                state.unlockedCount == 0 -> item {
                    EmptyState(
                        title = stringResource(R.string.missions_empty_title),
                        body = stringResource(R.string.missions_empty_body),
                        actionLabel = stringResource(R.string.action_go_roster),
                        onAction = onGoRoster,
                    )
                }
                state.arcs.isEmpty() -> item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = Space.s4),
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = stringResource(R.string.missions_filter_empty),
                            style = MaterialTheme.typography.bodyLarge,
                            color = colors.onSurfaceVariant,
                        )
                        GhostButton(
                            text = stringResource(R.string.action_clear_filter),
                            onClick = { onFilterChange(DifficultyFilter.ALL) },
                        )
                    }
                }
                else -> items(state.arcs, key = { it.cvId }) { arc ->
                    MissionCard(arc = arc, onClick = { onArcClick(arc.cvId) })
                }
            }
            item {
                Spacer(Modifier.height(Space.s1))
                Text(
                    text = stringResource(R.string.attribution),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun FilterOption(
    value: DifficultyFilter,
    label: String,
    current: DifficultyFilter,
    onSelect: (DifficultyFilter) -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    FilterChip(
        selected = current == value,
        onClick = { onSelect(value) },
        label = { Text(label, style = MaterialTheme.typography.labelMedium) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = colors.primaryContainer,
            selectedLabelColor = colors.onPrimaryContainer,
        ),
    )
}
