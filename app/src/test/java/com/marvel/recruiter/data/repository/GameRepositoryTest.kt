package com.marvel.recruiter.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.marvel.recruiter.data.local.AppDatabase
import com.marvel.recruiter.data.local.InitialStateCallback
import com.marvel.recruiter.data.sync.RosterFetcher
import com.marvel.recruiter.data.sync.RosterSeeder
import com.marvel.recruiter.game.ArcInput
import com.marvel.recruiter.game.CharacterInput
import com.marvel.recruiter.game.TeamRef
import com.marvel.recruiter.game.buildSeedPlan
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import android.app.Application
import org.robolectric.RobolectricTestRunner
import kotlin.random.Random

@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class GameRepositoryTest {

    private lateinit var db: AppDatabase
    private lateinit var repo: GameRepository
    private val now = 1_000_000L

    @Before
    fun setUp() = runTest {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
            .addCallback(InitialStateCallback)
            .allowMainThreadQueries()
            .build()
        repo = GameRepository(db)

        val team = TeamRef(10, "X-Men")
        val characters = (1L..4L).map { id ->
            CharacterInput(
                cvId = id, name = "h$id", realName = null, deck = null, aliases = null, imageUrl = null,
                powerCount = 6 + id.toInt() * 6, issueAppearances = 337 + id.toInt() * 1000,
                teams = listOf(team), friendCvIds = emptySet(), issueIds = setOf(100L + id),
            )
        }
        val arcs = listOf(ArcInput(500, "arco", null, null, (100L..110L).toSet()))
        seeder().persist(buildSeedPlan(characters, arcs, seededAt = 1L))
    }

    private fun seeder(): RosterSeeder {
        return RosterSeeder(RosterFetcher(readSnapshot = { "{\"characters\":[],\"arcs\":[]}" }), db)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `seed grava e marca como concluido`() = runTest {
        assertTrue(seeder().isSeeded())
        assertEquals(0, repo.observeRoster().first().size)
    }

    @Test
    fun `comprar pacote debita 100 e recruta um heroi`() = runTest {
        val before = repo.observeGame().first().coins
        val outcome = repo.buyPack(now, Random(1))

        assertTrue(outcome is PackOutcome.Recruited)
        assertEquals(before - 100, repo.observeGame().first().coins)
        assertEquals(1, repo.observeRoster().first().size)
    }

    @Test
    fun `sem moedas nao compra`() = runTest {
        repeat(3) { repo.buyPack(now, Random(it)) }
        assertEquals(PackOutcome.NotEnoughCoins, repo.buyPack(now, Random(9)))
    }

    @Test
    fun `missao exige 3 herois recrutados e aplica cooldown`() = runTest {
        repeat(3) { repo.buyPack(now, Random(it)) }
        val heroes = repo.observeRoster().first().map { it.cvId }
        val outcome = repo.startMission(500, heroes.take(3), now, Random(5))

        assertTrue(outcome.roll in 0..99)
        assertEquals(3, db.missionResultDao().heroIdsOf(outcome.resultId).size)
        val afterCooldown = repo.observeRoster().first().map { it.availableAt }.distinct()
        assertEquals(1, afterCooldown.size)
        assertTrue(afterCooldown.single() > now)
    }

    @Test
    fun `time com menos de 3 herois e rejeitado`() = runTest {
        repo.buyPack(now, Random(1))
        val one = repo.observeRoster().first().map { it.cvId }
        try {
            repo.startMission(500, one, now, Random(1))
            throw AssertionError("deveria rejeitar")
        } catch (e: IllegalArgumentException) {
            // esperado
        }
    }
}
