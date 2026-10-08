package com.marvel.recruiter.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.marvel.recruiter.R
import com.marvel.recruiter.data.repository.PokedexEntry
import com.marvel.recruiter.ui.components.HalftoneOverlay
import com.marvel.recruiter.ui.components.HeroImage
import com.marvel.recruiter.ui.components.MainTab
import com.marvel.recruiter.ui.components.RecruiterBottomBar
import com.marvel.recruiter.ui.components.TopBarDossier
import com.marvel.recruiter.ui.theme.Borders
import com.marvel.recruiter.ui.theme.Space
import com.marvel.recruiter.viewmodel.PokedexFilter
import com.marvel.recruiter.viewmodel.PokedexSort
import com.marvel.recruiter.game.Rarity
import com.marvel.recruiter.viewmodel.PokedexUiState
import com.marvel.recruiter.viewmodel.PokedexViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

/** Pokédex: grade de todos os personagens curados; só os recrutados abrem a wiki. */
@Composable
fun PokedexScreen(
    onHeroClick: (Long) -> Unit,
    onTabSelected: (MainTab) -> Unit,
    viewModel: PokedexViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    PokedexContent(
        state = state,
        onHeroClick = onHeroClick,
        onTabSelected = onTabSelected,
        onFilterChange = viewModel::setFilter,
        onSortChange = viewModel::setSort,
        onRarityChange = viewModel::setRarity,
    )
}

@Composable
fun PokedexContent(
    state: PokedexUiState,
    onHeroClick: (Long) -> Unit,
    onTabSelected: (MainTab) -> Unit,
    onFilterChange: (PokedexFilter) -> Unit,
    onSortChange: (PokedexSort) -> Unit,
    onRarityChange: (Rarity?) -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val lockedHint = stringResource(R.string.pokedex_locked_hint)
    val columns = if (LocalConfiguration.current.screenWidthDp >= 600) 3 else 2

    Scaffold(
        containerColor = colors.background,
        topBar = { TopBarDossier(title = stringResource(R.string.pokedex_title)) },
        bottomBar = { RecruiterBottomBar(selected = MainTab.POKEDEX, onSelect = onTabSelected) },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(Space.s4),
            horizontalArrangement = Arrangement.spacedBy(Space.s3),
            verticalArrangement = Arrangement.spacedBy(Space.s3),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(verticalArrangement = Arrangement.spacedBy(Space.s3)) {
                    Text(
                        text = stringResource(R.string.pokedex_counter, state.discovered, state.total),
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.onSurfaceVariant,
                    )
                    LinearProgressIndicator(
                        progress = { if (state.total == 0) 0f else state.discovered.toFloat() / state.total },
                        modifier = Modifier.fillMaxWidth().height(6.dp),
                        color = colors.primary,
                        trackColor = colors.surfaceVariant,
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Space.s2),
                    ) {
                        FilterOption(PokedexFilter.ALL, stringResource(R.string.pokedex_filter_all), state.filter, onFilterChange)
                        FilterOption(PokedexFilter.RECRUITED, stringResource(R.string.pokedex_filter_recruited), state.filter, onFilterChange)
                        FilterOption(PokedexFilter.LOCKED, stringResource(R.string.pokedex_filter_locked), state.filter, onFilterChange)
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Space.s2),
                    ) {
                        SortOption(PokedexSort.CATALOG, stringResource(R.string.pokedex_sort_catalog), state.sort, onSortChange)
                        SortOption(PokedexSort.ALPHABETICAL, stringResource(R.string.pokedex_sort_alphabetical), state.sort, onSortChange)
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Space.s2),
                    ) {
                        RarityOption(Rarity.COMMON, stringResource(R.string.pokedex_rarity_common), state.rarity, onRarityChange)
                        RarityOption(Rarity.RARE, stringResource(R.string.pokedex_rarity_rare), state.rarity, onRarityChange)
                        RarityOption(Rarity.LEGENDARY, stringResource(R.string.pokedex_rarity_legendary), state.rarity, onRarityChange)
                    }
                }
            }
            if (!state.loading && state.filter == PokedexFilter.RECRUITED && state.discovered == 0) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = stringResource(R.string.pokedex_empty_recruited),
                        style = MaterialTheme.typography.bodyLarge,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(vertical = Space.s4),
                    )
                }
            }
            items(state.entries, key = { it.cvId }) { entry ->
                PokedexTile(
                    entry = entry,
                    onClick = {
                        if (entry.recruited) {
                            onHeroClick(entry.cvId)
                        } else {
                            scope.launch { snackbar.showSnackbar(lockedHint) }
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun PokedexTile(entry: PokedexEntry, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(12.dp)
    val lockedDescription = stringResource(R.string.cd_pokedex_locked)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.surface)
            .border(Borders.thin, colors.outline, shape)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics(mergeDescendants = true) {
                if (!entry.recruited) contentDescription = lockedDescription
            },
    ) {
        Box(Modifier.fillMaxWidth().aspectRatio(1f)) {
            if (entry.recruited) {
                HeroImage(name = entry.name, imageUrl = entry.imageUrl, modifier = Modifier.fillMaxSize())
            } else {
                LockedSilhouette(Modifier.fillMaxSize())
            }
        }
        Column(Modifier.padding(Space.s3)) {
            Text(
                text = if (entry.recruited) entry.name else stringResource(R.string.pokedex_hidden_name),
                style = MaterialTheme.typography.titleMedium,
                color = if (entry.recruited) colors.onSurface else colors.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/** Textura escura com "?" para personagem não recrutado. Nunca mostra a imagem nem o nome. */
@Composable
private fun LockedSilhouette(modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val dark = colors.background.luminance() < 0.5f
    val base = if (dark) Color(0xFF07080A) else Color(0xFF2A2620)
    Box(modifier.background(base), contentAlignment = Alignment.Center) {
        HalftoneOverlay(Color.White.copy(alpha = 0.06f), Modifier.fillMaxSize())
        Text(
            text = "?",
            style = MaterialTheme.typography.displayLarge,
            color = colors.outline,
        )
    }
}

@Composable
private fun RarityOption(
    value: Rarity,
    label: String,
    current: Rarity?,
    onSelect: (Rarity?) -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    FilterChip(
        selected = current == value,
        onClick = { onSelect(value) },
        label = { Text(label, style = MaterialTheme.typography.labelMedium) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = colors.tertiaryContainer,
            selectedLabelColor = colors.onTertiaryContainer,
        ),
    )
}

@Composable
private fun SortOption(
    value: PokedexSort,
    label: String,
    current: PokedexSort,
    onSelect: (PokedexSort) -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    FilterChip(
        selected = current == value,
        onClick = { onSelect(value) },
        label = { Text(label, style = MaterialTheme.typography.labelMedium) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = colors.secondaryContainer,
            selectedLabelColor = colors.onSecondaryContainer,
        ),
    )
}

@Composable
private fun FilterOption(
    value: PokedexFilter,
    label: String,
    current: PokedexFilter,
    onSelect: (PokedexFilter) -> Unit,
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
