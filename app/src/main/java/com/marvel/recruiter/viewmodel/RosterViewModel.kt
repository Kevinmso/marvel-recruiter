package com.marvel.recruiter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marvel.recruiter.data.repository.GameRepository
import com.marvel.recruiter.data.repository.PackOutcome
import com.marvel.recruiter.data.repository.RosterHero
import com.marvel.recruiter.game.levelOf
import com.marvel.recruiter.game.pendingLevelPacks
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class RosterMessage { NOT_ENOUGH_COINS, NO_HERO_LEFT, PURCHASE_FAILED }

data class RosterUiState(
    val loading: Boolean = true,
    val coinBalance: Int = 0,
    val xpTotal: Int = 0,
    val level: Int = 1,
    val pendingLevelPacks: Int = 0,
    val heroes: List<RosterHero> = emptyList(),
    val recruitedCount: Int = 0,
    val totalHeroes: Int = 0,
    val onlyAvailable: Boolean = false,
    val buying: Boolean = false,
    val revealedCvId: Long? = null,
    val now: Long = 0L,
)

class RosterViewModel(
    private val repo: GameRepository,
    private val random: Random = Random.Default,
    private val now: () -> Long = System::currentTimeMillis,
) : ViewModel() {

    private data class LocalState(val onlyAvailable: Boolean, val buying: Boolean, val revealed: Long?)

    private val local = MutableStateFlow(LocalState(onlyAvailable = false, buying = false, revealed = null))
    private val messageChannel = Channel<RosterMessage>(Channel.BUFFERED)
    val messages: Flow<RosterMessage> = messageChannel.receiveAsFlow()

    val state: StateFlow<RosterUiState> = combine(
        repo.observeGame(),
        repo.observeRoster(),
        repo.observeTotalHeroes(),
        clockTicks(now = now),
        local,
    ) { game, heroes, total, clock, l ->
        val visible = if (l.onlyAvailable) heroes.filter { it.availableAt <= clock } else heroes
        RosterUiState(
            loading = false,
            coinBalance = game.coins,
            xpTotal = game.xp,
            level = levelOf(game.xp),
            pendingLevelPacks = pendingLevelPacks(game.xp, game.packsOpened),
            heroes = visible,
            recruitedCount = heroes.size,
            totalHeroes = total,
            onlyAvailable = l.onlyAvailable,
            buying = l.buying,
            revealedCvId = l.revealed,
            now = clock,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), RosterUiState())

    fun setOnlyAvailable(value: Boolean) {
        local.update { it.copy(onlyAvailable = value) }
    }

    fun buyPack() = purchase { repo.buyPack(now(), random) }

    fun openLevelPack() = purchase { repo.buyLevelPack(now(), random) }

    private fun purchase(block: suspend () -> PackOutcome) {
        if (local.value.buying) return
        local.update { it.copy(buying = true) }
        viewModelScope.launch {
            try {
                when (val outcome = block()) {
                    is PackOutcome.Recruited -> local.update { it.copy(revealed = outcome.cvId) }
                    PackOutcome.NotEnoughCoins -> messageChannel.send(RosterMessage.NOT_ENOUGH_COINS)
                    PackOutcome.NoHeroLeft -> messageChannel.send(RosterMessage.NO_HERO_LEFT)
                    PackOutcome.NoPackPending -> Unit
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                messageChannel.send(RosterMessage.PURCHASE_FAILED)
            } finally {
                local.update { it.copy(buying = false) }
            }
        }
    }

    fun dismissReveal() {
        local.update { it.copy(revealed = null) }
    }
}
