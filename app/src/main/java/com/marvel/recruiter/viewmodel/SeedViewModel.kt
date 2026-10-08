package com.marvel.recruiter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marvel.recruiter.data.sync.RosterSeeder
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface SeedUiState {
    data object Loading : SeedUiState
    data object Ready : SeedUiState
    data object Error : SeedUiState
}

/** Uma tentativa automática por abertura; depois disso só [retry] manual (RF-20). */
class SeedViewModel(private val seeder: RosterSeeder) : ViewModel() {

    private val _state = MutableStateFlow<SeedUiState>(SeedUiState.Loading)
    val state: StateFlow<SeedUiState> = _state.asStateFlow()

    private var job: Job? = null

    init {
        attempt()
    }

    fun retry() {
        if (_state.value == SeedUiState.Error) attempt()
    }

    private fun attempt() {
        if (job?.isActive == true) return
        _state.value = SeedUiState.Loading
        job = viewModelScope.launch {
            _state.value = try {
                seeder.seed()
                SeedUiState.Ready
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                android.util.Log.e("RosterSeed", "falha no seed", e)
                SeedUiState.Error
            }
        }
    }
}
