package com.marvel.recruiter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marvel.recruiter.data.repository.GameRepository
import com.marvel.recruiter.data.repository.HeroDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HeroProfileUiState(
    val loading: Boolean = true,
    val detail: HeroDetail? = null,
    val now: Long = 0L,
)

class HeroProfileViewModel(
    private val repo: GameRepository,
    private val cvId: Long,
    private val now: () -> Long = System::currentTimeMillis,
) : ViewModel() {

    private val _state = MutableStateFlow(HeroProfileUiState())
    val state: StateFlow<HeroProfileUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            val clock = now()
            _state.value = HeroProfileUiState(loading = false, detail = repo.heroDetail(cvId, clock), now = clock)
        }
    }
}
