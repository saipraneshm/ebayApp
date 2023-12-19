package com.sample.ebayapp.ui.theme.earthquake_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.ebayapp.data.model.Earthquake
import com.sample.ebayapp.data.repository.EarthquakeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface EarthquakeDetailState {
    data object Loading : EarthquakeDetailState
    data class Success(val earthquake: Earthquake?) : EarthquakeDetailState
}

@HiltViewModel
class EarthquakeDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: EarthquakeRepository
) : ViewModel() {

    val uiState = savedStateHandle.getStateFlow("eqId", "")
        .map {
            val result = repository.getEarthquake(it)
            EarthquakeDetailState.Success(result)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EarthquakeDetailState.Loading)
}