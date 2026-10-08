package com.marvel.recruiter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marvel.recruiter.data.repository.ArcSummary
import com.marvel.recruiter.data.repository.GameRepository
import com.marvel.recruiter.game.DifficultyLabel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

enum class DifficultyFilter { ALL, EASY, MEDIUM, EPIC }

data class MissionsUiState(
    val loading: Boolean = true,
    val arcs: List<ArcSummary> = emptyList(),
    val unlockedCount: Int = 0,
    val restingHeroes: Int = 0,
    val hasRecruits: Boolean = false,
    val filter: DifficultyFilter = DifficultyFilter.ALL,
)

class MissionsViewModel(
    private val repo: GameRepository,
    private val now: () -> Long = System::currentTimeMillis,
) : ViewModel() {

    private val filter = MutableStateFlow(DifficultyFilter.ALL)

    val state: StateFlow<MissionsUiState> = combine(
        repo.observeUnlockedArcs(),
        repo.observeRoster(),
        filter,
        clockTicks(now = now),
    ) { arcs, roster, f, clock ->
        val wanted = when (f) {
            DifficultyFilter.ALL -> null
            DifficultyFilter.EASY -> DifficultyLabel.EASY
            DifficultyFilter.MEDIUM -> DifficultyLabel.MEDIUM
            DifficultyFilter.EPIC -> DifficultyLabel.EPIC
        }
        MissionsUiState(
            loading = false,
            arcs = arcs
                .filter { wanted == null || DifficultyLabel.of(it.difficulty) == wanted }
                .sortedBy { it.difficulty },
            unlockedCount = arcs.size,
            restingHeroes = roster.count { it.availableAt > clock },
            hasRecruits = roster.isNotEmpty(),
            filter = f,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MissionsUiState())

    fun setFilter(value: DifficultyFilter) {
        filter.update { value }
    }
}
