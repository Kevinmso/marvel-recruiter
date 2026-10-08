package com.marvel.recruiter.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.marvel.recruiter.ui.components.MainTab
import com.marvel.recruiter.ui.screens.HeroProfileScreen
import com.marvel.recruiter.ui.screens.ArcStoryScreen
import com.marvel.recruiter.ui.screens.MissionResultScreen
import com.marvel.recruiter.ui.screens.MissionsScreen
import com.marvel.recruiter.ui.screens.PokedexScreen
import com.marvel.recruiter.ui.screens.RosterScreen
import com.marvel.recruiter.ui.screens.SeedScreen
import com.marvel.recruiter.ui.screens.ShopScreen
import com.marvel.recruiter.ui.screens.SquadScreen
import com.marvel.recruiter.viewmodel.NO_ARC_ID
import com.marvel.recruiter.viewmodel.SeedViewModel
import org.koin.androidx.compose.koinViewModel

object Routes {
    const val SEED = "seed"
    const val ROSTER = "roster"
    const val MISSIONS = "missions"
    const val POKEDEX = "pokedex"
    const val SHOP = "shop"
    const val ARC = "arc/{arcId}"
    const val TEAM = "team?arcId={arcId}"
    const val RESULT = "result/{arcId}/{heroes}/{position}"
    const val HERO = "hero/{cvId}"

    fun team(arcId: Long = NO_ARC_ID) = "team?arcId=$arcId"
    fun result(arcId: Long, heroIds: List<Long>, coordinationPosition: Double) =
        "result/$arcId/${heroIds.joinToString(",")}/$coordinationPosition"
    fun hero(cvId: Long) = "hero/$cvId"
    fun arc(cvId: Long) = "arc/$cvId"
}

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    seedViewModel: SeedViewModel = koinViewModel(),
) {
    NavHost(navController = navController, startDestination = Routes.SEED) {
        composable(Routes.SEED) {
            SeedScreen(
                viewModel = seedViewModel,
                onReady = {
                    navController.navigate(Routes.ROSTER) {
                        popUpTo(Routes.SEED) { inclusive = true }
                    }
                },
            )
        }
        composable(Routes.ROSTER) {
            RosterScreen(
                onHeroClick = { navController.navigate(Routes.hero(it)) },
                onTabSelected = { navigateTab(navController, it) },
            )
        }
        composable(Routes.POKEDEX) {
            PokedexScreen(
                onHeroClick = { navController.navigate(Routes.hero(it)) },
                onTabSelected = { navigateTab(navController, it) },
            )
        }
        composable(Routes.SHOP) {
            ShopScreen(onTabSelected = { navigateTab(navController, it) })
        }
        composable(Routes.MISSIONS) {
            MissionsScreen(
                onArcClick = { navController.navigate(Routes.arc(it)) },
                onGoRoster = { navigateTab(navController, MainTab.ROSTER) },
                onTabSelected = { navigateTab(navController, it) },
            )
        }
        composable(
            Routes.TEAM,
            arguments = listOf(navArgument("arcId") {
                type = NavType.LongType
                defaultValue = NO_ARC_ID
            }),
        ) { entry ->
            SquadScreen(
                arcCvId = entry.arguments?.getLong("arcId") ?: NO_ARC_ID,
                onStart = { arcId, heroIds, position ->
                    navController.navigate(Routes.result(arcId, heroIds, position))
                },
                onBack = { navController.popBackStack() },
                onHeroClick = { navController.navigate(Routes.hero(it)) },
                onGoMissions = { navigateTab(navController, MainTab.MISSIONS) },
                onGoRoster = { navigateTab(navController, MainTab.ROSTER) },
            )
        }
        composable(
            Routes.RESULT,
            arguments = listOf(
                navArgument("arcId") { type = NavType.LongType },
                navArgument("heroes") { type = NavType.StringType },
                navArgument("position") { type = NavType.StringType },
            ),
        ) { entry ->
            MissionResultScreen(
                arcCvId = entry.arguments?.getLong("arcId") ?: NO_ARC_ID,
                heroesCsv = entry.arguments?.getString("heroes").orEmpty(),
                coordinationPosition = entry.arguments?.getString("position")?.toDoubleOrNull() ?: 0.5,
                onNewMission = {
                    navController.navigate(Routes.MISSIONS) {
                        popUpTo(Routes.ROSTER) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onSameTeam = { arcId ->
                    navController.navigate(Routes.team(arcId)) {
                        popUpTo(Routes.RESULT) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() },
                onHeroClick = { navController.navigate(Routes.hero(it)) },
            )
        }
        composable(
            Routes.ARC,
            arguments = listOf(navArgument("arcId") { type = NavType.LongType }),
        ) { entry ->
            ArcStoryScreen(
                arcCvId = entry.arguments?.getLong("arcId") ?: NO_ARC_ID,
                onBack = { navController.popBackStack() },
                onBuildTeam = { navController.navigate(Routes.team(it)) },
            )
        }
        composable(
            Routes.HERO,
            arguments = listOf(navArgument("cvId") { type = NavType.LongType }),
        ) { entry ->
            HeroProfileScreen(
                cvId = entry.arguments?.getLong("cvId") ?: NO_ARC_ID,
                onBack = { navController.popBackStack() },
                onGoRoster = { navigateTab(navController, MainTab.ROSTER) },
                onArcClick = { navController.navigate(Routes.arc(it)) },
            )
        }
    }
}

private fun navigateTab(navController: NavHostController, tab: MainTab) {
    val route = when (tab) {
        MainTab.ROSTER -> Routes.ROSTER
        MainTab.POKEDEX -> Routes.POKEDEX
        MainTab.MISSIONS -> Routes.MISSIONS
        MainTab.SHOP -> Routes.SHOP
    }
    navController.navigate(route) {
        popUpTo(Routes.ROSTER) { inclusive = false }
        launchSingleTop = true
    }
}
