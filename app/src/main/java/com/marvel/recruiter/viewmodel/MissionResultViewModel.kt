package com.marvel.recruiter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marvel.recruiter.data.repository.GameRepository
import com.marvel.recruiter.data.repository.MissionOutcome
import com.marvel.recruiter.data.repository.RosterHero
import com.marvel.recruiter.game.cooldownMinutes
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

sealed interface MissionResultUiState {
    data object Calculating : MissionResultUiState
    data object SaveFailed : MissionResultUiState
    data class Ready(
        val outcome: MissionOutcome,
        val heroes: List<RosterHero>,
        val cooldownMinutes: Double,
        val revealed: Boolean = false,
        val arcImageUrl: String? = null,
    ) : MissionResultUiState
}

/** Executa a missão uma vez ao abrir a tela; a rolagem visual só revela o resultado já gravado. */
class MissionResultViewModel(
    private val repo: GameRepository,
    private val arcCvId: Long,
    private val heroIds: List<Long>,
    private val coordinationPosition: Double,
    private val random: Random = Random.Default,
    private val now: () -> Long = System::currentTimeMillis,
) : ViewModel() {

    private val _state = MutableStateFlow<MissionResultUiState>(MissionResultUiState.Calculating)
    val state: StateFlow<MissionResultUiState> = _state.asStateFlow()

    init {
        run()
    }

    fun retry() {
        if (_state.value == MissionResultUiState.SaveFailed) run()
    }

    fun onRollSettled() {
        _state.update { if (it is MissionResultUiState.Ready) it.copy(revealed = true) else it }
    }

    private fun run() {
        _state.value = MissionResultUiState.Calculating
        viewModelScope.launch {
            _state.value = try {
                val outcome = repo.startMission(
                    arcCvId = arcCvId,
                    teamCvIds = heroIds,
                    now = now(),
                    random = random,
                    coordinationPosition = coordinationPosition,
                )
                val heroes = repo.observeRoster().first()
                    .filter { it.cvId in heroIds }
                    .sortedBy { heroIds.indexOf(it.cvId) }
                MissionResultUiState.Ready(
                    outcome = outcome,
                    heroes = heroes,
                    cooldownMinutes = cooldownMinutes(outcome.difficulty),
                    arcImageUrl = repo.arcSummary(arcCvId)?.imageUrl,
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                MissionResultUiState.SaveFailed
            }
        }
    }
}
