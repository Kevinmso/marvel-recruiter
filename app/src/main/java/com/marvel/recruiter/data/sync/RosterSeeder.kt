package com.marvel.recruiter.data.sync

import androidx.room.withTransaction
import com.marvel.recruiter.data.local.AppDatabase
import com.marvel.recruiter.data.local.entity.CharacterEntity
import com.marvel.recruiter.data.local.entity.CharacterSynergyEntity
import com.marvel.recruiter.data.local.entity.CharacterTeamEntity
import com.marvel.recruiter.data.local.entity.CharacterUnlockEntity
import com.marvel.recruiter.data.local.entity.GameStateEntity
import com.marvel.recruiter.data.local.entity.SeedMetaEntity
import com.marvel.recruiter.data.local.entity.StoryArcEntity
import com.marvel.recruiter.data.local.entity.TeamEntity
import com.marvel.recruiter.game.SeedPlan

/** Rotina de seed (RF-01/02/03): busca na rede, depois grava tudo numa transação. */
class RosterSeeder(
    private val fetcher: RosterFetcher,
    private val db: AppDatabase,
) {

    suspend fun isSeeded(): Boolean = ensureGameState().seedCompleted

    private suspend fun ensureGameState(): GameStateEntity =
        db.gameStateDao().get() ?: GameStateEntity(
            coinBalance = INITIAL_COINS,
            xpTotal = 0,
            seedCompleted = false,
        ).also { db.gameStateDao().upsert(it) }

    suspend fun seed() {
        if (isSeeded()) return
        val plan = fetcher.fetchPlan()
        persist(plan)
    }

    suspend fun persist(plan: SeedPlan) = db.withTransaction {
        val characterDao = db.characterDao()
        characterDao.clearSynergies()
        characterDao.clearCharacterTeams()
        db.storyArcDao().clearUnlocks()
        characterDao.clearCharacters()
        characterDao.clearTeams()
        db.storyArcDao().clearArcs()
        db.seedMetaDao().clear()

        characterDao.insertTeams(plan.teams.map { TeamEntity(it.cvId, it.name) })
        characterDao.insertCharacters(plan.characters.map { c ->
            CharacterEntity(
                cvId = c.cvId,
                name = c.name,
                realName = c.realName,
                deck = c.deck,
                aliases = c.aliases,
                imageUrl = c.imageUrl,
                numPowers = c.numPowers,
                numAppearances = c.numAppearances,
                adjustedPower = c.adjustedPower,
                veterancy = c.veterancy,
            )
        })
        characterDao.insertCharacterTeams(plan.characters.flatMap { c ->
            c.teamCvIds.map { CharacterTeamEntity(c.cvId, it) }
        })
        characterDao.insertSynergies(plan.synergies.map { CharacterSynergyEntity(it.lowCvId, it.highCvId) })

        db.storyArcDao().insertArcs(plan.arcs.map { a ->
            StoryArcEntity(a.cvId, a.name, a.deck, a.numIssues, a.difficulty, a.imageUrl, a.story)
        })
        db.storyArcDao().insertUnlocks(plan.unlocks.map { CharacterUnlockEntity(it.characterCvId, it.arcCvId) })

        db.seedMetaDao().insertAll(plan.meta.map { (k, v) -> SeedMetaEntity(k, v) })
        db.gameStateDao().upsert(ensureGameState().copy(seedCompleted = true))
    }
}

private const val INITIAL_COINS = 300
