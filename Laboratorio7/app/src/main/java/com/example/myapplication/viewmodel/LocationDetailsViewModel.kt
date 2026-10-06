package com.example.myapplication.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.myapplication.Location
import com.example.myapplication.LocationDb
import com.example.myapplication.navigation.LocationDetailsRoute
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LocationDetailsUiState(
    val isLoading: Boolean = true,
    val data: Location? = null,
    val hasError: Boolean = false
)

class LocationDetailsViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val locationId: Int = savedStateHandle.toRoute<LocationDetailsRoute>().id

    private val locationDb = LocationDb()

    private val _uiState = MutableStateFlow(LocationDetailsUiState())
    val uiState: StateFlow<LocationDetailsUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadLocation()
    }

    fun loadLocation() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = LocationDetailsUiState(isLoading = true)
            delay(2000)
            _uiState.value = LocationDetailsUiState(
                isLoading = false,
                data = locationDb.getLocationById(locationId)
            )
        }
    }

    fun onLoadingClick() {
        loadJob?.cancel()
        _uiState.update { it.copy(isLoading = false, hasError = true) }
    }
}