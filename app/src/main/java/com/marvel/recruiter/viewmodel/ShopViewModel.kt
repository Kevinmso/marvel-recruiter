package com.marvel.recruiter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marvel.recruiter.data.repository.GameRepository
import com.marvel.recruiter.data.repository.RosterHero
import com.marvel.recruiter.data.repository.ShopOffer
import com.marvel.recruiter.data.repository.ShopOutcome
import com.marvel.recruiter.game.SHOP_UNLOCK_HEROES
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import kotlin.random.Random

enum class ShopMessage { NOT_ENOUGH_COINS, UNAVAILABLE, PURCHASE_FAILED }

data class ShopReveal(val hero: RosterHero, val price: Int)

data class ShopUiState(
    val coinBalance: Int = 0,
    val offers: List<ShopOffer> = emptyList(),
    val buyingId: Long? = null,
    val revealed: ShopReveal? = null,
    val now: Long = 0L,
    val nextRefreshAt: Long = 0L,
    val unlocked: Boolean = false,
)

class ShopViewModel(
    private val repo: GameRepository,
    private val random: Random = Random.Default,
    private val today: () -> Long = { LocalDate.now().toEpochDay() },
    private val now: () -> Long = System::currentTimeMillis,
) : ViewModel() {

    private data class LocalState(val buyingId: Long?, val revealed: ShopReveal?)

    private val local = MutableStateFlow(LocalState(buyingId = null, revealed = null))
    private val messageChannel = Channel<ShopMessage>(Channel.BUFFERED)
    val messages: Flow<ShopMessage> = messageChannel.receiveAsFlow()

    private val dayKey = today()
    private val nextRefreshAt = LocalDate.ofEpochDay(dayKey + 1)
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

    val state: StateFlow<ShopUiState> = combine(
        repo.observeGame(),
        repo.observeRoster(),
        repo.observeShop(dayKey),
        local,
        clockTicks(periodMs = 1_000L, now = now),
    ) { game, roster, offers, l, clock ->
        ShopUiState(
            coinBalance = game.coins,
            offers = offers,
            buyingId = l.buyingId,
            revealed = l.revealed,
            now = clock,
            nextRefreshAt = nextRefreshAt,
            unlocked = roster.size >= SHOP_UNLOCK_HEROES,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ShopUiState())

    init {
        // A vitrine só é gerada depois que a loja libera (RF-28).
        viewModelScope.launch {
            repo.observeRoster()
                .map { it.size >= SHOP_UNLOCK_HEROES }
                .distinctUntilChanged()
                .filter { it }
                .first()
            repo.ensureShop(dayKey, random)
        }
    }

    fun buy(offer: ShopOffer) {
        if (local.value.buyingId != null) return
        local.update { it.copy(buyingId = offer.id) }
        viewModelScope.launch {
            try {
                when (val outcome = repo.buyOffer(offer.id, now())) {
                    is ShopOutcome.Bought -> local.update { it.copy(revealed = ShopReveal(outcome.hero, offer.price)) }
                    ShopOutcome.NotEnoughCoins -> messageChannel.send(ShopMessage.NOT_ENOUGH_COINS)
                    ShopOutcome.Unavailable -> messageChannel.send(ShopMessage.UNAVAILABLE)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                messageChannel.send(ShopMessage.PURCHASE_FAILED)
            } finally {
                local.update { it.copy(buyingId = null) }
            }
        }
    }

    fun dismissReveal() {
        local.update { it.copy(revealed = null) }
    }
}
