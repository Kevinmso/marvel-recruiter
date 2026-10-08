package com.marvel.recruiter.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.marvel.recruiter.R
import com.marvel.recruiter.ui.components.ErrorState
import com.marvel.recruiter.ui.theme.Space
import com.marvel.recruiter.viewmodel.SeedUiState
import com.marvel.recruiter.viewmodel.SeedViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

/** Tempo mínimo da marca na abertura, contado a partir de quando a tela aparece. */
private const val SPLASH_MIN_MS = 3_000L

/** Tela 0: seed inicial. Mostra a marca enquanto carrega; erro com "Tentar novamente" (RF-20). */
@Composable
fun SeedScreen(
    onReady: () -> Unit,
    viewModel: SeedViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val shownAt = remember { System.currentTimeMillis() }

    LaunchedEffect(state) {
        if (state == SeedUiState.Ready) {
            val remaining = SPLASH_MIN_MS - (System.currentTimeMillis() - shownAt)
            if (remaining > 0) delay(remaining)
            onReady()
        }
    }

    SeedContent(state = state, onRetry = viewModel::retry)
}

@Composable
fun SeedContent(state: SeedUiState, onRetry: () -> Unit) {
    val colors = MaterialTheme.colorScheme

    when (state) {
        SeedUiState.Loading, SeedUiState.Ready -> Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.marvel_red)),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.splash_logo),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.fillMaxWidth(0.9f),
                contentScale = ContentScale.Fit,
            )
        }
        SeedUiState.Error -> Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
                .systemBarsPadding(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Space.s4),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
            ) {
                ErrorState(
                    title = stringResource(R.string.seed_error_title),
                    body = stringResource(R.string.seed_error_body),
                    actionLabel = stringResource(R.string.action_retry),
                    onAction = onRetry,
                    detail = stringResource(R.string.seed_error_code),
                )
                Spacer(Modifier.height(Space.s4))
                Text(
                    text = stringResource(R.string.attribution),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(bottom = Space.s4),
                )
            }
        }
    }
}
