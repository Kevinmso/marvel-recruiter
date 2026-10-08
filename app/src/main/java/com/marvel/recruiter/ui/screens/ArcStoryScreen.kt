package com.marvel.recruiter.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.marvel.recruiter.R
import com.marvel.recruiter.ui.components.ArcImage
import com.marvel.recruiter.ui.components.DifficultyTag
import com.marvel.recruiter.ui.components.EmptyState
import com.marvel.recruiter.ui.components.PrimaryButton
import com.marvel.recruiter.ui.components.SeedProgress
import com.marvel.recruiter.ui.components.TopBarDossier
import com.marvel.recruiter.ui.theme.Space
import com.marvel.recruiter.ui.theme.StatValueSmall
import com.marvel.recruiter.ui.util.formatDecimal
import com.marvel.recruiter.viewmodel.ArcStoryUiState
import com.marvel.recruiter.viewmodel.ArcStoryViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

/** História do arco: texto original da Comic Vine e ponte para a montagem de time. */
@Composable
fun ArcStoryScreen(
    arcCvId: Long,
    onBack: () -> Unit,
    onBuildTeam: (Long) -> Unit,
    viewModel: ArcStoryViewModel = koinViewModel(parameters = { parametersOf(arcCvId) }),
) {
    val state by viewModel.state.collectAsState()
    ArcStoryContent(
        state = state,
        onBack = onBack,
        onBuildTeam = { onBuildTeam(arcCvId) },
    )
}

@Composable
fun ArcStoryContent(
    state: ArcStoryUiState,
    onBack: () -> Unit,
    onBuildTeam: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val arc = state.arc

    Scaffold(
        containerColor = colors.background,
        topBar = { TopBarDossier(title = stringResource(R.string.arc_story_title), onBack = onBack) },
        bottomBar = {
            if (arc != null) {
                Box(Modifier.fillMaxWidth().background(colors.surface).padding(Space.s4)) {
                    PrimaryButton(text = stringResource(R.string.arc_build_team), onClick = onBuildTeam)
                }
            }
        },
    ) { padding ->
        when {
            state.loading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                SeedProgress(stringResource(R.string.result_calculating))
            }
            arc == null -> EmptyState(
                title = stringResource(R.string.arc_not_found),
                body = "",
                modifier = Modifier.padding(padding),
            )
            else -> {
                val story = arc.story?.takeIf { it.isNotBlank() }
                val deck = arc.deck?.takeIf { it.isNotBlank() }
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(bottom = Space.s4),
                    verticalArrangement = Arrangement.spacedBy(Space.s4),
                ) {
                    item {
                        ArcImage(
                            name = arc.name,
                            imageUrl = arc.imageUrl,
                            modifier = Modifier.fillMaxWidth().height(220.dp),
                        )
                    }
                    item {
                        Column(Modifier.padding(horizontal = Space.s4), verticalArrangement = Arrangement.spacedBy(Space.s2)) {
                            Text(
                                text = arc.name,
                                style = MaterialTheme.typography.headlineSmall,
                                color = colors.onSurface,
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                DifficultyTag(arc.difficulty)
                                Spacer(Modifier.weight(1f))
                                Text(
                                    text = stringResource(R.string.issues_count, arc.numIssues),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colors.onSurfaceVariant,
                                )
                            }
                            Text(
                                text = stringResource(R.string.difficulty_value, formatDecimal(arc.difficulty)),
                                style = StatValueSmall,
                                color = colors.onSurface,
                            )
                        }
                    }
                    item {
                        Column(Modifier.padding(horizontal = Space.s4), verticalArrangement = Arrangement.spacedBy(Space.s2)) {
                            Text(
                                text = stringResource(R.string.arc_story_title),
                                style = MaterialTheme.typography.titleLarge,
                                color = colors.onSurface,
                            )
                            if (story != null) {
                                Text(story, style = MaterialTheme.typography.bodyLarge, color = colors.onSurface)
                            } else {
                                Text(
                                    text = stringResource(R.string.profile_no_summary),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = colors.onSurface,
                                )
                                deck?.let {
                                    Text(it, style = MaterialTheme.typography.bodyLarge, color = colors.onSurfaceVariant)
                                }
                            }
                            if (story != null || deck != null) {
                                Text(
                                    text = stringResource(R.string.original_text_caption),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colors.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
