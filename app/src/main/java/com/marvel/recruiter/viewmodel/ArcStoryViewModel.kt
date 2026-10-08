package com.marvel.recruiter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marvel.recruiter.data.repository.ArcSummary
import com.marvel.recruiter.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ArcStoryUiState(
    val loading: Boolean = true,
    val arc: ArcSummary? = null,
)

class ArcStoryViewModel(
    private val repo: GameRepository,
    private val arcCvId: Long,
) : ViewModel() {

    private val _state = MutableStateFlow(ArcStoryUiState())
    val state: StateFlow<ArcStoryUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = ArcStoryUiState(loading = false, arc = repo.arcSummary(arcCvId))
        }
    }
}
