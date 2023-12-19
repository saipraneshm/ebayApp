package com.sample.ebayapp.ui.theme.earthquake_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.ebayapp.data.model.Earthquake
import com.sample.ebayapp.data.repository.EarthquakeRepository
import com.sample.ebayapp.ui.theme.earthquake_list.EarthquakeListUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private val TAG = EarthquakeViewModel::class.java.simpleName

sealed interface EarthquakeListUiState {
    data object Loading : EarthquakeListUiState

    data class Success(val earthquakes: List<Earthquake>) : EarthquakeListUiState

    data class Error(val message: String?) : EarthquakeListUiState
}

@HiltViewModel
class EarthquakeViewModel @Inject constructor(
    private val repository: EarthquakeRepository
) : ViewModel() {

    val uiState: StateFlow<EarthquakeListUiState> = flow {
        emit(repository.getEarthquakes())
    }
        .map<List<Earthquake>, EarthquakeListUiState>(::Success)
        .catch {
            Log.e(TAG, "unable to fetch earthquakes", it)
            emit(EarthquakeListUiState.Error(it.message))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EarthquakeListUiState.Loading)
}