package com.marvel.recruiter.data.repository

import androidx.room.withTransaction
import com.marvel.recruiter.data.local.AppDatabase
import com.marvel.recruiter.data.local.entity.CharacterEntity
import com.marvel.recruiter.data.local.entity.GameStateEntity
import com.marvel.recruiter.data.local.entity.MissionResultEntity
import com.marvel.recruiter.data.local.entity.MissionResultHeroEntity
import com.marvel.recruiter.data.local.entity.ShopOfferEntity
import com.marvel.recruiter.data.local.entity.StoryArcEntity
import com.marvel.recruiter.data.local.entity.UserRosterEntity
import com.marvel.recruiter.game.HeroStats
import com.marvel.recruiter.game.PACK_COST
import com.marvel.recruiter.game.PackCandidate
import com.marvel.recruiter.game.Rarity
import com.marvel.recruiter.game.drawShop
import com.marvel.recruiter.game.SynergyPair
import com.marvel.recruiter.game.availableAtAfterMission
import com.marvel.recruiter.game.coordinationBonus
import com.marvel.recruiter.game.streakMultiplier
import com.marvel.recruiter.game.drawFromPack
import com.marvel.recruiter.game.factionBonus
import com.marvel.recruiter.game.missionReward
import com.marvel.recruiter.game.pendingLevelPacks
import com.marvel.recruiter.game.rollMission
import com.marvel.recruiter.game.successChance
import com.marvel.recruiter.game.SYNERGY_CAP
import com.marvel.recruiter.game.synergyBonus
import com.marvel.recruiter.game.teamStrength
import kotlin.math.roundToInt
import com.marvel.recruiter.game.MissionReward
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlin.random.Random

data class GameSnapshot(val coins: Int, val xp: Int, val seeded: Boolean, val packsOpened: Int = 0)

data class StrengthEstimate(val total: Double, val base: Double, val synergy: Int, val faction: Int)

data class RosterHero(
    val cvId: Long,
    val name: String,
    val imageUrl: String?,
    val veterancy: Double,
    val adjustedPower: Double,
    val availableAt: Long,
)

data class PokedexEntry(
    val cvId: Long,
    val name: String,
    val imageUrl: String?,
    val recruited: Boolean,
    val veterancy: Double?,
    val adjustedPower: Double?,
)

data class ArcSummary(
    val cvId: Long,
    val name: String,
    val deck: String?,
    val imageUrl: String?,
    val numIssues: Int,
    val difficulty: Double,
    val story: String? = null,
)

data class HeroDetail(
    val hero: CharacterEntity,
    val teamNames: List<String>,
    val arcs: List<ArcSummary>,
    val recruited: Boolean,
    val availableAt: Long?,
)

sealed interface PackOutcome {
    data class Recruited(val cvId: Long, val name: String) : PackOutcome
    data object NotEnoughCoins : PackOutcome
    data object NoHeroLeft : PackOutcome
    data object NoPackPending : PackOutcome
}

data class ShopOffer(
    val id: Long,
    val hero: RosterHero,
    val strength: Double,
    val rarity: Rarity,
    val price: Int,
    val purchased: Boolean,
)

sealed interface ShopOutcome {
    data class Bought(val hero: RosterHero) : ShopOutcome
    data object NotEnoughCoins : ShopOutcome
    data object Unavailable : ShopOutcome
}

data class MissionOutcome(
    val resultId: Long,
    val arcName: String,
    val teamStrength: Double,
    val difficulty: Double,
    val chance: Double,
    val roll: Int,
    val success: Boolean,
    val xpEarned: Int,
    val coinsEarned: Int,
    val streak: Int,
)

class GameRepository(private val db: AppDatabase) {

    fun observeGame(): Flow<GameSnapshot> =
        db.gameStateDao().observe().map { it.toSnapshot() }

    fun observeRoster(): Flow<List<RosterHero>> =
        combine(db.userRosterDao().observeAll(), db.characterDao().observeAll()) { roster, characters ->
            val byId = characters.associateBy { it.cvId }
            roster.mapNotNull { row ->
                val c = byId[row.characterCvId] ?: return@mapNotNull null
                RosterHero(c.cvId, c.name, c.imageUrl, c.veterancy, c.adjustedPower, row.availableAt)
            }
        }

    fun observeTotalHeroes(): Flow<Int> = db.characterDao().observeAll().map { it.size }

    fun observePokedex(): Flow<List<PokedexEntry>> =
        combine(db.characterDao().observeAll(), db.userRosterDao().observeAll()) { characters, roster ->
            val owned = roster.mapTo(HashSet()) { it.characterCvId }
            characters.map { c ->
                val recruited = c.cvId in owned
                PokedexEntry(
                    cvId = c.cvId,
                    name = c.name,
                    imageUrl = c.imageUrl,
                    recruited = recruited,
                    veterancy = c.veterancy.takeIf { recruited },
                    adjustedPower = c.adjustedPower.takeIf { recruited },
                )
            }
        }

    fun observeUnlockedArcs(): Flow<List<ArcSummary>> =
        db.storyArcDao().observeUnlocked().map { arcs -> arcs.map { it.toSummary() } }

    fun observeHistory(): Flow<List<MissionResultEntity>> = db.missionResultDao().observeAll()

    suspend fun arcSummary(cvId: Long): ArcSummary? = db.storyArcDao().getByCvId(cvId)?.toSummary()

    suspend fun heroDetail(cvId: Long, now: Long): HeroDetail? {
        val hero = db.characterDao().getByCvId(cvId) ?: return null
        val teamNames = teamNamesOf(cvId)
        val arcs = db.storyArcDao().getArcsOf(cvId).map { it.toSummary() }
        val owned = db.userRosterDao().getAll().firstOrNull { it.characterCvId == cvId }
        return HeroDetail(hero, teamNames, arcs, recruited = owned != null, availableAt = owned?.availableAt?.takeIf { it > now })
    }

    suspend fun buyPack(now: Long, random: Random): PackOutcome = db.withTransaction {
        val state = db.gameStateDao().get() ?: error("game_state ausente")
        if (state.coinBalance < PACK_COST) return@withTransaction PackOutcome.NotEnoughCoins

        val outcome = drawNewHero(now, random) ?: return@withTransaction PackOutcome.NoHeroLeft
        db.gameStateDao().upsert(state.copy(coinBalance = state.coinBalance - PACK_COST))
        outcome
    }

    /** Pacote de nível (RF-29): sem custo em Moeda; só abre se houver pacote pendente. */
    suspend fun buyLevelPack(now: Long, random: Random): PackOutcome = db.withTransaction {
        val state = db.gameStateDao().get() ?: error("game_state ausente")
        if (pendingLevelPacks(state.xpTotal, state.packsOpened) == 0) return@withTransaction PackOutcome.NoPackPending

        val outcome = drawNewHero(now, random) ?: return@withTransaction PackOutcome.NoHeroLeft
        db.gameStateDao().upsert(state.copy(packsOpened = state.packsOpened + 1))
        outcome
    }

    /** Sorteia um herói ainda não recrutado e o recruta. Null se o pool estiver vazio. Chamar dentro de transação. */
    private suspend fun drawNewHero(now: Long, random: Random): PackOutcome.Recruited? {
        val owned = db.userRosterDao().getAll().mapTo(HashSet()) { it.characterCvId }
        val pool = db.characterDao().getAll()
            .filter { it.cvId !in owned }
            .map { PackCandidate(it.cvId, it.veterancy, 0.6 * it.adjustedPower + 0.4 * it.veterancy) }
        if (pool.isEmpty()) return null

        val cvId = drawFromPack(pool, random)
        db.userRosterDao().insert(UserRosterEntity(characterCvId = cvId, availableAt = 0, recruitedAt = now))
        return PackOutcome.Recruited(cvId, db.characterDao().getByCvId(cvId)!!.name)
    }

    suspend fun startMission(
        arcCvId: Long,
        teamCvIds: List<Long>,
        now: Long,
        random: Random,
        coordinationPosition: Double = 0.5,
    ): MissionOutcome =
        db.withTransaction {
            require(teamCvIds.size in 3..5) { "time precisa de 3 a 5 heróis" }
            require(teamCvIds.distinct().size == teamCvIds.size) { "herói repetido no time" }

            val arc = db.storyArcDao().getByCvId(arcCvId) ?: error("arco $arcCvId não existe")
            val roster = db.userRosterDao().getAll().associateBy { it.characterCvId }
            require(teamCvIds.all { id -> roster[id]?.let { it.availableAt <= now } == true }) {
                "herói não recrutado ou em cooldown"
            }

            val members = teamCvIds.map { db.characterDao().getByCvId(it) ?: error("herói $it ausente") }
            val teamsByHero = teamMembership()
            val stats = members.map {
                HeroStats(it.cvId, it.adjustedPower, it.veterancy, teamsByHero[it.cvId].orEmpty())
            }
            val synergies = db.characterDao().getAllSynergies().mapTo(HashSet()) {
                SynergyPair(it.lowCvId, it.highCvId)
            }

            val strength = teamStrength(stats, synergies) * (1 + coordinationBonus(coordinationPosition))
            val chance = successChance(strength, arc.difficulty)
            val outcome = rollMission(chance, random)
            val previous = db.missionResultDao().recent()
            val streakBefore = previous.takeWhile { it.success }.size
            val base = missionReward(arc.difficulty, outcome.success)
            val multiplier = if (outcome.success) streakMultiplier(streakBefore) else 1.0
            val reward = MissionReward(
                xp = (base.xp * multiplier).roundToInt(),
                coins = (base.coins * multiplier).roundToInt(),
            )
            val streak = if (outcome.success) streakBefore + 1 else 0
            val cooldownEnd = availableAtAfterMission(arc.difficulty, now)

            val resultId = db.missionResultDao().insertResult(
                MissionResultEntity(
                    arcCvId = arcCvId,
                    teamStrength = strength,
                    difficulty = arc.difficulty,
                    chance = chance,
                    rollValue = outcome.roll.toDouble(),
                    success = outcome.success,
                    xpEarned = reward.xp,
                    coinsEarned = reward.coins,
                    createdAt = now,
                ),
            )
            db.missionResultDao().insertHeroes(teamCvIds.map { MissionResultHeroEntity(resultId, it) })
            teamCvIds.forEach { db.userRosterDao().setCooldown(it, cooldownEnd) }

            val state = db.gameStateDao().get() ?: error("game_state ausente")
            db.gameStateDao().upsert(
                state.copy(
                    coinBalance = state.coinBalance + reward.coins,
                    xpTotal = state.xpTotal + reward.xp,
                ),
            )

            MissionOutcome(
                resultId = resultId,
                arcName = arc.name,
                teamStrength = strength,
                difficulty = arc.difficulty,
                chance = chance,
                roll = outcome.roll,
                success = outcome.success,
                xpEarned = reward.xp,
                coinsEarned = reward.coins,
                streak = streak,
            )
        }

    fun observeShop(dayKey: Long): Flow<List<ShopOffer>> =
        combine(db.shopDao().observeDay(dayKey), db.characterDao().observeAll()) { offers, characters ->
            val byId = characters.associateBy { it.cvId }
            offers.mapNotNull { row ->
                val c = byId[row.characterCvId] ?: return@mapNotNull null
                ShopOffer(
                    id = row.id,
                    hero = RosterHero(c.cvId, c.name, c.imageUrl, c.veterancy, c.adjustedPower, availableAt = 0),
                    strength = 0.6 * c.adjustedPower + 0.4 * c.veterancy,
                    rarity = Rarity.valueOf(row.rarity),
                    price = row.price,
                    purchased = row.purchased,
                )
            }
        }

    /** Gera a vitrine do dia se ainda não existe (RF-28). Ofertas gravadas não mudam ao reabrir a tela. */
    suspend fun ensureShop(dayKey: Long, random: Random) = db.withTransaction {
        if (db.shopDao().countForDay(dayKey) > 0) return@withTransaction
        val owned = db.userRosterDao().getAll().mapTo(HashSet()) { it.characterCvId }
        val pool = db.characterDao().getAll()
            .filter { it.cvId !in owned }
            .map { PackCandidate(it.cvId, it.veterancy, 0.6 * it.adjustedPower + 0.4 * it.veterancy) }
        db.shopDao().clear()
        db.shopDao().insertAll(
            drawShop(pool, random).map {
                ShopOfferEntity(dayKey = dayKey, characterCvId = it.cvId, rarity = it.rarity.name, price = it.price)
            },
        )
    }

    suspend fun buyOffer(offerId: Long, now: Long): ShopOutcome = db.withTransaction {
        val offer = db.shopDao().getById(offerId)
        if (offer == null || offer.purchased) return@withTransaction ShopOutcome.Unavailable
        // Herói pode ter sido recrutado pelo pacote depois da vitrine ser gerada.
        if (db.userRosterDao().getAll().any { it.characterCvId == offer.characterCvId }) {
            return@withTransaction ShopOutcome.Unavailable
        }
        val state = db.gameStateDao().get() ?: error("game_state ausente")
        if (state.coinBalance < offer.price) return@withTransaction ShopOutcome.NotEnoughCoins

        db.userRosterDao().insert(UserRosterEntity(characterCvId = offer.characterCvId, availableAt = 0, recruitedAt = now))
        db.shopDao().markPurchased(offerId)
        db.gameStateDao().upsert(state.copy(coinBalance = state.coinBalance - offer.price))
        val c = db.characterDao().getByCvId(offer.characterCvId) ?: error("herói ausente")
        ShopOutcome.Bought(RosterHero(c.cvId, c.name, c.imageUrl, c.veterancy, c.adjustedPower, availableAt = 0))
    }

    /** Pré-visualização da ForçaTime (Tela 2). Mesma fórmula de [startMission], sem gravar nada. */
    suspend fun strengthEstimate(teamCvIds: List<Long>): StrengthEstimate {
        val teamsByHero = teamMembership()
        val stats = teamCvIds.mapNotNull { db.characterDao().getByCvId(it) }.map {
            HeroStats(it.cvId, it.adjustedPower, it.veterancy, teamsByHero[it.cvId].orEmpty())
        }
        val synergies = db.characterDao().getAllSynergies().mapTo(HashSet()) {
            SynergyPair(it.lowCvId, it.highCvId)
        }
        val base = stats.sumOf { 0.6 * it.adjustedPower + 0.4 * it.veterancy }
        val synergy = minOf(synergyBonus(stats, synergies), SYNERGY_CAP)
        val faction = factionBonus(stats)
        return StrengthEstimate(base + synergy + faction, base, synergy, faction)
    }

    private suspend fun teamMembership(): Map<Long, Set<Long>> =
        db.characterDao().getAllCharacterTeams()
            .groupBy({ it.characterCvId }, { it.teamCvId })
            .mapValues { it.value.toSet() }

    private suspend fun teamNamesOf(cvId: Long): List<String> {
        val teamIds = teamMembership()[cvId].orEmpty()
        val names = db.characterDao().getAllTeams().associate { it.cvId to it.name }
        return teamIds.mapNotNull { names[it] }.sorted()
    }

    private fun GameStateEntity?.toSnapshot() =
        if (this == null) GameSnapshot(coins = 0, xp = 0, seeded = false)
        else GameSnapshot(coinBalance, xpTotal, seedCompleted, packsOpened)

    private fun StoryArcEntity.toSummary() = ArcSummary(cvId, name, deck, imageUrl, numIssues, difficulty, story)
}
