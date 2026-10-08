package com.marvel.recruiter.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.marvel.recruiter.R
import com.marvel.recruiter.ui.components.ArcRounds
import com.marvel.recruiter.ui.components.CooldownBadge
import com.marvel.recruiter.ui.components.DiceRoll
import com.marvel.recruiter.ui.components.DifficultyTag
import com.marvel.recruiter.ui.components.ErrorState
import com.marvel.recruiter.ui.components.ROUNDS
import com.marvel.recruiter.ui.components.GhostButton
import com.marvel.recruiter.ui.components.HeroImage
import com.marvel.recruiter.ui.components.PrimaryButton
import com.marvel.recruiter.ui.components.ResultBanner
import com.marvel.recruiter.ui.components.RewardRow
import com.marvel.recruiter.ui.components.SecondaryButton
import com.marvel.recruiter.ui.components.SeedProgress
import com.marvel.recruiter.ui.components.StampLabel
import com.marvel.recruiter.ui.components.TopBarDossier
import com.marvel.recruiter.ui.theme.LocalRecruiterColors
import com.marvel.recruiter.ui.theme.Space
import com.marvel.recruiter.ui.theme.StatValue
import com.marvel.recruiter.ui.util.formatDecimal
import com.marvel.recruiter.ui.util.formatPercent
import com.marvel.recruiter.viewmodel.MissionResultUiState
import com.marvel.recruiter.viewmodel.MissionResultViewModel
import com.marvel.recruiter.game.streakMultiplier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.math.roundToInt

/** Tela 4: calcula e grava a missão ao abrir; a rolagem só revela o resultado já salvo (RF-12/13/14). */
@Composable
fun MissionResultScreen(
    arcCvId: Long,
    heroesCsv: String,
    coordinationPosition: Double,
    onNewMission: () -> Unit,
    onSameTeam: (arcCvId: Long) -> Unit,
    onBack: () -> Unit,
    onHeroClick: (Long) -> Unit,
    viewModel: MissionResultViewModel = koinViewModel(
        parameters = { parametersOf(arcCvId, heroesCsv, coordinationPosition) },
    ),
) {
    val state by viewModel.state.collectAsState()
    MissionResultContent(
        state = state,
        arcCvId = arcCvId,
        onNewMission = onNewMission,
        onSameTeam = onSameTeam,
        onBack = onBack,
        onHeroClick = onHeroClick,
        onRetry = viewModel::retry,
        onRollSettled = viewModel::onRollSettled,
    )
}

@Composable
fun MissionResultContent(
    state: MissionResultUiState,
    arcCvId: Long,
    onNewMission: () -> Unit,
    onSameTeam: (arcCvId: Long) -> Unit,
    onBack: () -> Unit,
    onHeroClick: (Long) -> Unit,
    onRetry: () -> Unit,
    onRollSettled: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val snackbar = remember { SnackbarHostState() }
    val waitMessage = stringResource(R.string.msg_wait_roll)

    val ready = state as? MissionResultUiState.Ready
    val busy = state is MissionResultUiState.Calculating || (ready != null && !ready.revealed)

    val scope = rememberCoroutineScope()
    BackHandler(enabled = busy) {
        // Não interrompe a animação (design.md Tela 4).
        scope.launch { snackbar.showSnackbar(waitMessage) }
    }

    var rollStarted by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopBarDossier(
                title = stringResource(R.string.result_title),
                onBack = if (busy) null else onBack,
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Space.s4, vertical = Space.s3),
            verticalArrangement = Arrangement.spacedBy(Space.s4),
        ) {
            when (val s = state) {
                MissionResultUiState.Calculating -> SeedProgress(stringResource(R.string.result_calculating))
                MissionResultUiState.SaveFailed -> ErrorState(
                    title = stringResource(R.string.result_save_failed_title),
                    body = stringResource(R.string.result_save_failed_body),
                    actionLabel = stringResource(R.string.action_save_retry),
                    onAction = onRetry,
                )
                is MissionResultUiState.Ready -> {
                    val outcome = s.outcome
                    val rollLabel = stringResource(R.string.result_roll_label, outcome.roll, formatPercent(outcome.chance))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = outcome.arcName.keepLastWordsTogether(),
                            style = MaterialTheme.typography.headlineSmall,
                            color = colors.onSurface,
                            modifier = Modifier.weight(1f),
                        )
                        Spacer(Modifier.width(Space.s2))
                        DifficultyTag(outcome.difficulty)
                    }
                    if (s.revealed) {
                        ResultBanner(
                            success = outcome.success,
                            title = stringResource(if (outcome.success) R.string.result_win else R.string.result_lose),
                            stamp = stringResource(if (outcome.success) R.string.stamp_success else R.string.stamp_failure),
                        )
                    }
                    ArcRounds(
                        heroes = s.heroes,
                        teamStrength = outcome.teamStrength,
                        difficulty = outcome.difficulty,
                        arcName = outcome.arcName,
                        arcImageUrl = s.arcImageUrl,
                        onFinished = { rollStarted = true },
                        frozenStep = if (s.revealed) ROUNDS * s.heroes.size - 1 else null,
                    )
                    CalcPanel(
                        strength = outcome.teamStrength,
                        difficulty = outcome.difficulty,
                        chance = outcome.chance,
                    )
                    DiceRoll(
                        roll = outcome.roll,
                        chance = outcome.chance,
                        rolling = rollStarted,
                        label = rollLabel,
                        onSettled = onRollSettled,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    if (s.revealed) {
                        if (outcome.success && outcome.streak >= 2) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                StampLabel(
                                    text = stringResource(R.string.streak_stamp, outcome.streak),
                                    color = LocalRecruiterColors.current.coin,
                                    rotation = -3f,
                                )
                            }
                        }
                        RewardRow(
                            xp = outcome.xpEarned,
                            coins = outcome.coinsEarned,
                            success = outcome.success,
                            streakPercent = if (outcome.success) {
                                ((streakMultiplier(outcome.streak - 1) - 1) * 100).roundToInt()
                            } else {
                                0
                            },
                        )
                        Text(
                            text = stringResource(R.string.result_rest_title),
                            style = MaterialTheme.typography.titleLarge,
                            color = colors.onSurface,
                        )
                        val now = System.currentTimeMillis()
                        s.heroes.forEach { hero ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(colors.surface)
                                    .border(1.dp, colors.outline, RoundedCornerShape(12.dp))
                                    .clickable { onHeroClick(hero.cvId) }
                                    .padding(Space.s2),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(Modifier.size(48.dp).clip(RoundedCornerShape(8.dp))) {
                                    HeroImage(name = hero.name, imageUrl = hero.imageUrl, modifier = Modifier.fillMaxSize())
                                }
                                Spacer(Modifier.width(Space.s3))
                                Text(
                                    text = hero.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = colors.onSurface,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1,
                                )
                                CooldownBadge(availableAt = hero.availableAt, now = now)
                            }
                        }
                        Spacer(Modifier.height(Space.s2))
                        PrimaryButton(text = stringResource(R.string.action_new_mission), onClick = onNewMission)
                        SecondaryButton(
                            text = stringResource(R.string.action_same_team),
                            onClick = { onSameTeam(arcCvId) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalcPanel(strength: Double, difficulty: Double, chance: Double) {
    val colors = MaterialTheme.colorScheme
    val stroke = colors.outline
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surface, RoundedCornerShape(12.dp))
            .border(1.dp, stroke, RoundedCornerShape(12.dp))
            .padding(vertical = Space.s3, horizontal = Space.s2),
    ) {
        CalcTile(stringResource(R.string.result_strength), formatDecimal(strength), Modifier.weight(1f))
        CalcTile(stringResource(R.string.result_difficulty), formatDecimal(difficulty), Modifier.weight(1f))
        CalcTile(stringResource(R.string.result_chance), "${formatPercent(chance)}%", Modifier.weight(1f))
    }
}

@Composable
private fun CalcTile(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(Space.s1))
        Text(text = value, style = StatValue, color = MaterialTheme.colorScheme.onSurface)
    }
}

private fun String.keepLastWordsTogether(): String {
    val i = lastIndexOf(' ')
    return if (i > 0) substring(0, i) + " " + substring(i + 1) else this
}
