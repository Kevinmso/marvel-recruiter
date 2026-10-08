package com.marvel.recruiter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.marvel.recruiter.ui.nav.AppNavHost
import com.marvel.recruiter.ui.theme.RecruiterTheme
import com.marvel.recruiter.viewmodel.SeedUiState
import com.marvel.recruiter.viewmodel.SeedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val seedViewModel: SeedViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        // A splash nativa segura só o fundo vermelho até o seed terminar; a marca é desenhada pela SeedScreen
        installSplashScreen().setKeepOnScreenCondition { seedViewModel.state.value == SeedUiState.Loading }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecruiterTheme {
                AppNavHost(seedViewModel = seedViewModel)
            }
        }
    }
}
