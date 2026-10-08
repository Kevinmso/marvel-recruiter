package com.marvel.recruiter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marvel.recruiter.data.repository.GameRepository
import com.marvel.recruiter.data.repository.PokedexEntry
import com.marvel.recruiter.game.Rarity
import com.marvel.recruiter.game.rarityOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

enum class PokedexFilter { ALL, RECRUITED, LOCKED }

enum class PokedexSort { CATALOG, ALPHABETICAL }

data class PokedexUiState(
    val loading: Boolean = true,
    val entries: List<PokedexEntry> = emptyList(),
    val discovered: Int = 0,
    val total: Int = 0,
    val filter: PokedexFilter = PokedexFilter.ALL,
    val sort: PokedexSort = PokedexSort.CATALOG,
    val rarity: Rarity? = null,
)

class PokedexViewModel(private val repo: GameRepository) : ViewModel() {

    private val filter = MutableStateFlow(PokedexFilter.ALL)
    private val sort = MutableStateFlow(PokedexSort.CATALOG)
    private val rarity = MutableStateFlow<Rarity?>(null)

    val state: StateFlow<PokedexUiState> = combine(repo.observePokedex(), filter, sort, rarity) { all, f, s, r ->
        val byStatus = when (f) {
            PokedexFilter.ALL -> all
            PokedexFilter.RECRUITED -> all.filter { it.recruited }
            PokedexFilter.LOCKED -> all.filter { !it.recruited }
        }
        // Raridade depende dos atributos, que só são visíveis para recrutados: bloqueados ficam fora do filtro.
        val byRarity = if (r == null) byStatus else byStatus.filter { it.recruited && rarityOfEntry(it) == r }
        PokedexUiState(
            loading = false,
            entries = if (s == PokedexSort.ALPHABETICAL) alphabetical(byRarity) else byRarity,
            discovered = all.count { it.recruited },
            total = all.size,
            filter = f,
            sort = s,
            rarity = r,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PokedexUiState())

    fun setFilter(value: PokedexFilter) {
        filter.value = value
    }

    fun setSort(value: PokedexSort) {
        sort.value = value
    }

    fun setRarity(value: Rarity?) {
        rarity.value = if (rarity.value == value) null else value
    }

    private fun rarityOfEntry(entry: PokedexEntry): Rarity =
        rarityOf(0.6 * (entry.adjustedPower ?: 0.0) + 0.4 * (entry.veterancy ?: 0.0))

    // Recrutados em ordem A–Z primeiro; bloqueados mantêm a ordem do catálogo.
    private fun alphabetical(entries: List<PokedexEntry>): List<PokedexEntry> {
        val (recruited, locked) = entries.partition { it.recruited }
        return recruited.sortedBy { it.name } + locked
    }
}
