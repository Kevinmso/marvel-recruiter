package com.marvel.recruiter.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import com.marvel.recruiter.R
import com.marvel.recruiter.game.PACK_COST
import com.marvel.recruiter.ui.components.EmptyState
import com.marvel.recruiter.ui.components.HeroCard
import com.marvel.recruiter.ui.components.HeroSilhouette
import com.marvel.recruiter.ui.components.Meter
import com.marvel.recruiter.ui.components.MainTab
import com.marvel.recruiter.ui.components.PackRevealDialog
import com.marvel.recruiter.ui.components.PrimaryButton
import com.marvel.recruiter.ui.components.RecruiterBottomBar
import com.marvel.recruiter.ui.components.CoinPill
import com.marvel.recruiter.ui.components.TeamSlot
import com.marvel.recruiter.ui.components.TopBarDossier
import com.marvel.recruiter.ui.components.XpPill
import com.marvel.recruiter.ui.theme.Space
import com.marvel.recruiter.viewmodel.RosterMessage
import com.marvel.recruiter.viewmodel.RosterUiState
import com.marvel.recruiter.viewmodel.RosterViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.layout.size

/** Tela 1: recrutados, saldo, compra de pacote (RF-16/17). */
@Composable
fun RosterScreen(
    onHeroClick: (Long) -> Unit,
    onTabSelected: (MainTab) -> Unit,
    viewModel: RosterViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    val notEnough = stringResource(R.string.msg_not_enough_coins)
    val noHeroLeft = stringResource(R.string.msg_no_hero_left)
    val purchaseFailed = stringResource(R.string.msg_purchase_failed)

    LaunchedEffect(Unit) {
        viewModel.messages.collect { message ->
            snackbar.showSnackbar(
                when (message) {
                    RosterMessage.NOT_ENOUGH_COINS -> notEnough
                    RosterMessage.NO_HERO_LEFT -> noHeroLeft
                    RosterMessage.PURCHASE_FAILED -> purchaseFailed
                },
            )
        }
    }

    RosterContent(
        state = state,
        snackbar = snackbar,
        onHeroClick = onHeroClick,
        onTabSelected = onTabSelected,
        onBuyPack = viewModel::buyPack,
        onOpenLevelPack = viewModel::openLevelPack,
        onOnlyAvailableChange = viewModel::setOnlyAvailable,
        onDismissReveal = viewModel::dismissReveal,
    )
}

@Composable
fun RosterContent(
    state: RosterUiState,
    snackbar: SnackbarHostState,
    onHeroClick: (Long) -> Unit,
    onTabSelected: (MainTab) -> Unit,
    onBuyPack: () -> Unit,
    onOpenLevelPack: () -> Unit,
    onOnlyAvailableChange: (Boolean) -> Unit,
    onDismissReveal: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val columns = if (LocalConfiguration.current.screenWidthDp >= 600) 3 else 2
    val revealed = state.revealedCvId?.let { id -> state.heroes.firstOrNull { it.cvId == id } }
    val allRecruited = state.totalHeroes > 0 && state.recruitedCount >= state.totalHeroes

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopBarDossier(
                title = stringResource(R.string.roster_title),
                actions = {
                    CoinPill(coins = state.coinBalance)
                    XpPill(xp = state.xpTotal)
                },
            )
        },
        bottomBar = { RecruiterBottomBar(selected = MainTab.ROSTER, onSelect = onTabSelected) },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = Space.s4, top = Space.s4, end = Space.s4, bottom = 152.dp),
            horizontalArrangement = Arrangement.spacedBy(Space.s3),
            verticalArrangement = Arrangement.spacedBy(Space.s3),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                PackCard(
                    buying = state.buying,
                    coins = state.coinBalance,
                    allRecruited = allRecruited,
                    level = state.level,
                    pendingLevelPacks = state.pendingLevelPacks,
                    onBuy = onBuyPack,
                    onOpenLevelPack = onOpenLevelPack,
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                RecruitedStrip(recruited = state.recruitedCount, total = state.totalHeroes)
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.roster_heroes_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = colors.onSurface,
                        modifier = Modifier.weight(1f),
                    )
                    FilterChip(
                        selected = state.onlyAvailable,
                        onClick = { onOnlyAvailableChange(!state.onlyAvailable) },
                        label = { Text(stringResource(R.string.roster_filter_available), style = MaterialTheme.typography.labelMedium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = colors.primaryContainer,
                            selectedLabelColor = colors.onPrimaryContainer,
                        ),
                    )
                }
            }
            if (state.recruitedCount == 0 && !state.loading) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    EmptyState(
                        title = stringResource(R.string.roster_empty_title),
                        body = stringResource(R.string.roster_empty_body),
                    )
                }
            } else if (state.recruitedCount in 1..2) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = stringResource(R.string.roster_partial, 3 - state.recruitedCount),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colors.surfaceVariant, RoundedCornerShape(8.dp))
                            .padding(Space.s3),
                    )
                }
            }
            items(state.heroes, key = { it.cvId }) { hero ->
                HeroCard(
                    hero = hero,
                    now = state.now,
                    onClick = { onHeroClick(hero.cvId) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }

    if (state.revealedCvId != null) {
        PackRevealDialog(
            hero = revealed,
            cost = PACK_COST,
            onDismiss = onDismissReveal,
        )
    }
}

@Composable
private fun PackCard(
    buying: Boolean,
    coins: Int,
    allRecruited: Boolean,
    level: Int,
    pendingLevelPacks: Int,
    onBuy: () -> Unit,
    onOpenLevelPack: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val canBuy = !allRecruited && coins >= PACK_COST && !buying
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surface, RoundedCornerShape(12.dp))
            .border(1.dp, colors.outline, RoundedCornerShape(12.dp))
            .padding(Space.s4),
        verticalArrangement = Arrangement.spacedBy(Space.s2),
    ) {
        Text(stringResource(R.string.pack_title), style = MaterialTheme.typography.titleLarge, color = colors.onSurface)
        Text(stringResource(R.string.pack_body), style = MaterialTheme.typography.bodyMedium, color = colors.onSurfaceVariant)
        Spacer(Modifier.height(Space.s1))
        PrimaryButton(
            text = if (allRecruited) stringResource(R.string.pack_all_recruited) else stringResource(R.string.pack_buy, PACK_COST),
            onClick = onBuy,
            enabled = canBuy,
            loading = buying,
            supportingText = if (!allRecruited && coins < PACK_COST) stringResource(R.string.pack_insufficient) else null,
        )
        if (pendingLevelPacks > 0) {
            Spacer(Modifier.height(Space.s1))
            PrimaryButton(
                text = stringResource(R.string.level_pack_open, pendingLevelPacks),
                onClick = onOpenLevelPack,
                enabled = !allRecruited && !buying,
                loading = false,
                supportingText = stringResource(R.string.level_label, level),
            )
        } else {
            Text(stringResource(R.string.level_label, level), style = MaterialTheme.typography.labelMedium, color = colors.onSurfaceVariant)
        }
    }
}

@Composable
private fun RecruitedStrip(recruited: Int, total: Int) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surfaceVariant, RoundedCornerShape(8.dp))
            .padding(Space.s3),
        verticalArrangement = Arrangement.spacedBy(Space.s2),
    ) {
        Text(
            text = stringResource(R.string.roster_recruited, recruited, total),
            style = MaterialTheme.typography.labelMedium,
            color = colors.onSurfaceVariant,
        )
        Meter(
            fraction = if (total == 0) 0f else recruited.toFloat() / total,
            color = colors.primary,
            track = colors.surface,
            height = 4.dp,
        )
    }
}
