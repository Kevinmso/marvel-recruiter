package com.marvel.recruiter.ui

import android.app.Application
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.captureRoboImage
import com.marvel.recruiter.data.local.entity.CharacterEntity
import com.marvel.recruiter.data.repository.ArcSummary
import com.marvel.recruiter.data.repository.HeroDetail
import com.marvel.recruiter.data.repository.MissionOutcome
import com.marvel.recruiter.data.repository.PokedexEntry
import com.marvel.recruiter.data.repository.RosterHero
import com.marvel.recruiter.data.repository.StrengthEstimate
import com.marvel.recruiter.ui.screens.ArcStoryContent
import com.marvel.recruiter.ui.screens.PokedexContent
import com.marvel.recruiter.viewmodel.ArcStoryUiState
import com.marvel.recruiter.viewmodel.PokedexUiState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.marvel.recruiter.ui.components.ArcRounds
import com.marvel.recruiter.ui.components.CoordinationMeter
import com.marvel.recruiter.ui.screens.HeroProfileContent
import com.marvel.recruiter.ui.screens.MissionResultContent
import com.marvel.recruiter.ui.screens.MissionsContent
import com.marvel.recruiter.ui.screens.RosterContent
import com.marvel.recruiter.ui.screens.SeedContent
import com.marvel.recruiter.ui.screens.SquadContent
import com.marvel.recruiter.ui.theme.Motion
import com.marvel.recruiter.ui.theme.RecruiterTheme
import com.marvel.recruiter.viewmodel.DifficultyFilter
import com.marvel.recruiter.viewmodel.HeroProfileUiState
import com.marvel.recruiter.viewmodel.MissionResultUiState
import com.marvel.recruiter.viewmodel.MissionsUiState
import com.marvel.recruiter.viewmodel.PokedexFilter
import com.marvel.recruiter.viewmodel.RosterUiState
import com.marvel.recruiter.viewmodel.SeedUiState
import com.marvel.recruiter.viewmodel.SquadBlock
import com.marvel.recruiter.viewmodel.SquadUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(application = Application::class, qualifiers = "w411dp-h891dp-xxhdpi")
class ScreenshotTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val now = 1_800_000_000_000L
    private val hour = 3_600_000L

    private val heroes = listOf(
        RosterHero(4005_1440, "Wolverine", null, 100.0, 100.0, 0),
        RosterHero(4005_1442, "Capitão América", null, 80.6, 92.0, now + 90 * 60_000L),
        RosterHero(4005_1460, "Colossus", null, 80.6, 15.0, 0),
        RosterHero(4005_21561, "Carol Danvers", null, 60.0, 84.5, now + 4 * hour),
        RosterHero(4005_3470, "Black Goliath", null, 0.0, 20.0, 0),
    )

    private val arcs = listOf(
        ArcSummary(40615, "\"Avengers\" Civil War", "Heróis se dividem após a Lei de Registro de Super-Humanos.", null, 122, 100.0),
        ArcSummary(40991, "\"X-Men/Avengers\" House of M", "Wanda altera a realidade e cria o mundo de House of M.", null, 80, 86.9),
        ArcSummary(42233, "\"Infinity Trilogy\" Infinity Gauntlet", "Thanos reúne as Joias do Infinito.", null, 32, 58.7),
        ArcSummary(57031, "\"Avengers\" Age of Ultron", null, null, 23, 48.7),
        ArcSummary(44425, "\"The Uncanny X-Men\" Days of Future Past", "Um futuro sombrio se abate sobre os mutantes.", null, 6, 10.0),
    )

    private fun shot(name: String, dark: Boolean, content: @Composable () -> Unit) {
        composeRule.setContent {
            RecruiterTheme(darkTheme = dark) { content() }
        }
        composeRule.onRoot().captureRoboImage("screenshots/$name-${if (dark) "dark" else "light"}.png")
    }

    @Test fun seedLoadingLight() = shot("seed-loading", false) {
        SeedContent(state = SeedUiState.Loading, onRetry = {})
    }

    @Test fun seedLoadingDark() = shot("seed-loading", true) {
        SeedContent(state = SeedUiState.Loading, onRetry = {})
    }

    @Test fun seedErrorLight() = shot("seed-error", false) {
        SeedContent(state = SeedUiState.Error, onRetry = {})
    }

    @Test fun seedErrorDark() = shot("seed-error", true) {
        SeedContent(state = SeedUiState.Error, onRetry = {})
    }

    @Test fun rosterEmptyLight() = shot("roster-empty", false) { rosterEmpty() }

    @Test fun rosterEmptyDark() = shot("roster-empty", true) { rosterEmpty() }

    @Test fun rosterWithHeroesLight() = shot("roster-heroes", false) { rosterFull() }

    @Test fun rosterWithHeroesDark() = shot("roster-heroes", true) { rosterFull() }

    @Test fun squadLight() = shot("squad", false) { squad() }

    @Test fun squadDark() = shot("squad", true) { squad() }

    @Test fun missionsLight() = shot("missions", false) { missions() }

    @Test fun missionsDark() = shot("missions", true) { missions() }

    @Test fun missionRoundTwoLight() = shot("mission-round-two", false) { rounds() }

    @Test fun missionRoundTwoDark() = shot("mission-round-two", true) { rounds() }

    @Test fun missionResultLight() = shot("mission-result", false) { result(success = true) }

    @Test fun missionResultDark() = shot("mission-result", true) { result(success = true) }

    @Test fun missionResultLossLight() = shot("mission-result-loss", false) { result(success = false) }

    @Test fun missionResultLossDark() = shot("mission-result-loss", true) { result(success = false) }

    @Test fun missionResultStreakLight() = shot("mission-result-streak", false) { result(success = true, streak = 3) }

    @Test fun missionResultStreakDark() = shot("mission-result-streak", true) { result(success = true, streak = 3) }

    @Test fun coordinationIdleLight() = shot("coordination-idle", false) { meter(position = 0.2, frozen = false) }

    @Test fun coordinationIdleDark() = shot("coordination-idle", true) { meter(position = 0.2, frozen = false) }

    @Test fun coordinationBonusLight() = shot("coordination-bonus", false) { meter(position = 0.5, frozen = true) }

    @Test fun coordinationBonusDark() = shot("coordination-bonus", true) { meter(position = 0.5, frozen = true) }

    @Test fun heroProfileLight() = shot("hero-profile", false) { profile(recruited = true) }

    @Test fun heroProfileDark() = shot("hero-profile", true) { profile(recruited = true) }

    @Test fun heroLockedLight() = shot("hero-locked", false) { profile(recruited = false) }

    @Test fun heroLockedDark() = shot("hero-locked", true) { profile(recruited = false) }

    @Test fun pokedexLight() = shot("pokedex", false) { pokedex() }

    @Test fun pokedexDark() = shot("pokedex", true) { pokedex() }

    @Test fun arcStoryLight() = shot("arc-story", false) { arcStory(story = arcStoryText) }

    @Test fun arcStoryDark() = shot("arc-story", true) { arcStory(story = arcStoryText) }

    @Test fun arcStoryNoSummaryLight() = shot("arc-story-no-summary", false) { arcStory(story = null) }

    @Test fun arcStoryNoSummaryDark() = shot("arc-story-no-summary", true) { arcStory(story = null) }

    private val arcStoryText =
        "Após a tragédia de Wundagore, Wanda Maximoff altera a realidade ao redor dos mutantes " +
            "e cria o mundo de House of M. Enquanto Magneto governa uma nação mutante, os heróis " +
            "precisam descobrir quem está por trás da distorção e restaurar a ordem antes que " +
            "a memória de todos seja apagada para sempre."

    private val pokedexEntries = listOf(
        PokedexEntry(4005_1440, "Wolverine", null, recruited = true, veterancy = 100.0, adjustedPower = 100.0),
        PokedexEntry(4005_1442, "Capitão América", null, recruited = true, veterancy = 80.6, adjustedPower = 92.0),
        PokedexEntry(4005_1460, "Colossus", null, recruited = true, veterancy = 80.6, adjustedPower = 15.0),
        PokedexEntry(4005_21561, "Carol Danvers", null, recruited = false, veterancy = null, adjustedPower = null),
        PokedexEntry(4005_3470, "Black Goliath", null, recruited = false, veterancy = null, adjustedPower = null),
        PokedexEntry(4005_9001, "Thor", null, recruited = false, veterancy = null, adjustedPower = null),
    )

    @Composable
    private fun rosterEmpty() {
        RosterContent(
            state = RosterUiState(loading = false, coinBalance = 300, xpTotal = 0, totalHeroes = 24, now = now),
            snackbar = SnackbarHostState(),
            onHeroClick = {},
            onTabSelected = {},
            onBuyPack = {},
            onOpenLevelPack = {},
            onOnlyAvailableChange = {},
            onDismissReveal = {},
        )
    }

    @Composable
    private fun rosterFull() {
        RosterContent(
            state = RosterUiState(
                loading = false,
                coinBalance = 40,
                xpTotal = 1250,
                heroes = heroes,
                recruitedCount = heroes.size,
                totalHeroes = 24,
                now = now,
            ),
            snackbar = SnackbarHostState(),
            onHeroClick = {},
            onTabSelected = {},
            onBuyPack = {},
            onOpenLevelPack = {},
            onOnlyAvailableChange = {},
            onDismissReveal = {},
        )
    }

    @Composable
    private fun squad() {
        SquadContent(
            state = SquadUiState(
                loading = false,
                arc = arcs[1],
                heroes = heroes,
                selected = listOf(heroes[0].cvId, heroes[2].cvId, heroes[4].cvId),
                estimate = StrengthEstimate(total = 58.0, base = 45.0, synergy = 10, faction = 3),
                availableCount = 4,
                canStart = true,
                block = null,
                now = now,
            ),
            onToggle = {},
            onStart = { _, _, _ -> },
            onBack = {},
            onHeroClick = {},
            onGoMissions = {},
            onGoRoster = {},
        )
    }

    @Composable
    private fun missions() {
        MissionsContent(
            state = MissionsUiState(
                loading = false,
                arcs = arcs,
                unlockedCount = arcs.size,
                restingHeroes = 2,
                hasRecruits = true,
                filter = DifficultyFilter.ALL,
            ),
            onArcClick = {},
            onGoRoster = {},
            onTabSelected = {},
            onFilterChange = {},
        )
    }

    @Composable
    private fun meter(position: Double, frozen: Boolean) {
        Column(
            Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
        ) {
            CoordinationMeter(position = position, frozen = frozen)
        }
    }

    @Composable
    private fun rounds() {
        ArcRounds(
            heroes = heroes,
            teamStrength = 58.0,
            difficulty = 86.9,
            arcName = "\"X-Men/Avengers\" House of M",
            arcImageUrl = null,
            onFinished = {},
            frozenStep = heroes.size + 1,
        )
    }

    private fun battleHit(dark: Boolean) {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            RecruiterTheme(darkTheme = dark) {
                ArcRounds(
                    heroes = heroes,
                    teamStrength = 58.0,
                    difficulty = 86.9,
                    arcName = "\"X-Men/Avengers\" House of M",
                    arcImageUrl = null,
                    onFinished = {},
                )
            }
        }
        composeRule.mainClock.advanceTimeBy(Motion.ROUND_STEP.toLong() * 2 + 120)
        composeRule.onRoot().captureRoboImage("screenshots/mission-battle-hit-${if (dark) "dark" else "light"}.png")
    }

    @Test fun missionBattleHitLight() = battleHit(dark = false)

    @Test fun missionBattleHitDark() = battleHit(dark = true)

    @Composable
    private fun result(success: Boolean, streak: Int = 0) {
        MissionResultContent(
            state = MissionResultUiState.Ready(
                outcome = MissionOutcome(
                    resultId = 1,
                    arcName = "\"X-Men/Avengers\" House of M",
                    teamStrength = 58.0,
                    difficulty = 86.9,
                    chance = 0.17,
                    roll = 12,
                    success = success,
                    xpEarned = if (success) 869 else 174,
                    coinsEarned = if (success) 435 else 87,
                    streak = if (success) streak else 0,
                ),
                heroes = heroes.take(3),
                cooldownMinutes = 130.35,
                revealed = true,
            ),
            arcCvId = 40991,
            onNewMission = {},
            onSameTeam = {},
            onBack = {},
            onHeroClick = {},
            onRetry = {},
            onRollSettled = {},
        )
    }

    @Composable
    private fun pokedex() {
        PokedexContent(
            state = PokedexUiState(
                loading = false,
                entries = pokedexEntries,
                discovered = 3,
                total = pokedexEntries.size,
                filter = PokedexFilter.ALL,
            ),
            onHeroClick = {},
            onTabSelected = {},
            onFilterChange = {},
            onSortChange = {},
            onRarityChange = {},
        )
    }

    @Composable
    private fun arcStory(story: String?) {
        ArcStoryContent(
            state = ArcStoryUiState(
                loading = false,
                arc = ArcSummary(
                    cvId = 40991,
                    name = "\"X-Men/Avengers\" House of M",
                    deck = "Wanda altera a realidade e cria o mundo de House of M.",
                    imageUrl = null,
                    numIssues = 80,
                    difficulty = 86.9,
                    story = story,
                ),
            ),
            onBack = {},
            onBuildTeam = {},
        )
    }

    @Composable
    private fun profile(recruited: Boolean) {
        HeroProfileContent(
            state = HeroProfileUiState(
                loading = false,
                now = now,
                detail = HeroDetail(
                    hero = CharacterEntity(
                        cvId = 4005_1460,
                        name = "Colossus",
                        realName = "Piotr Nikolaievitch Rasputin",
                        deck = "Membro dos X-Men, mutante capaz de transformar o corpo em aço orgânico.",
                        aliases = "Peter Rasputin\nColossal",
                        imageUrl = null,
                        numPowers = 8,
                        numAppearances = 7917,
                        adjustedPower = 15.0,
                        veterancy = 80.6,
                    ),
                    teamNames = listOf("X-Men", "Avengers", "Excalibur", "Alpha Flight"),
                    arcs = arcs.take(2),
                    recruited = recruited,
                    availableAt = if (recruited) now + hour else null,
                ),
            ),
            onBack = {},
            onGoRoster = {},
            onArcClick = {},
        )
    }
}
