package com.marvel.recruiter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marvel.recruiter.data.repository.ArcSummary
import com.marvel.recruiter.data.repository.GameRepository
import com.marvel.recruiter.data.repository.RosterHero
import com.marvel.recruiter.data.repository.StrengthEstimate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

const val NO_ARC_ID = -1L
const val MIN_TEAM = 3
const val MAX_TEAM = 5

enum class SquadBlock { NO_ARC, NOT_ENOUGH_AVAILABLE, NEED_MORE }

data class SquadUiState(
    val loading: Boolean = true,
    val arc: ArcSummary? = null,
    val heroes: List<RosterHero> = emptyList(),
    val selected: List<Long> = emptyList(),
    val estimate: StrengthEstimate? = null,
    val availableCount: Int = 0,
    val canStart: Boolean = false,
    val block: SquadBlock? = null,
    val now: Long = 0L,
)

class SquadViewModel(
    private val repo: GameRepository,
    private val arcCvId: Long,
    private val now: () -> Long = System::currentTimeMillis,
) : ViewModel() {

    private val selected = MutableStateFlow<List<Long>>(emptyList())

    private val estimate = selected.mapLatest { ids ->
        if (ids.isEmpty()) null else repo.strengthEstimate(ids)
    }

    val state: StateFlow<SquadUiState> = combine(
        repo.observeUnlockedArcs().map { list -> list.firstOrNull { it.cvId == arcCvId } },
        repo.observeRoster(),
        selected,
        clockTicks(now = now),
        estimate,
    ) { arc, heroes, sel, clock, est ->
        val available = heroes.filter { it.availableAt <= clock }
        val availableIds = available.mapTo(HashSet()) { it.cvId }
        val block = when {
            arc == null -> SquadBlock.NO_ARC
            available.size < MIN_TEAM -> SquadBlock.NOT_ENOUGH_AVAILABLE
            sel.size < MIN_TEAM -> SquadBlock.NEED_MORE
            else -> null
        }
        SquadUiState(
            loading = false,
            arc = arc,
            heroes = heroes.sortedWith(compareBy({ it.availableAt > clock }, { it.name })),
            selected = sel,
            estimate = est,
            availableCount = available.size,
            canStart = block == null && sel.size <= MAX_TEAM && sel.all { it in availableIds },
            block = block,
            now = clock,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SquadUiState())

    fun toggle(cvId: Long) {
        val current = selected.value
        when {
            cvId in current -> selected.update { it - cvId }
            current.size >= MAX_TEAM -> Unit
            state.value.heroes.firstOrNull { it.cvId == cvId }?.let { it.availableAt <= now() } != true -> Unit
            else -> selected.update { it + cvId }
        }
    }
}
