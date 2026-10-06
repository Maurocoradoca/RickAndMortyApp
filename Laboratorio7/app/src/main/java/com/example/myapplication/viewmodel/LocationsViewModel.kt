package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.Location
import com.example.myapplication.LocationDb
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LocationsUiState(
    val isLoading: Boolean = true,
    val data: List<Location> = emptyList(),
    val hasError: Boolean = false
)

class LocationsViewModel : ViewModel() {

    private val locationDb = LocationDb()

    private val _uiState = MutableStateFlow(LocationsUiState())
    val uiState: StateFlow<LocationsUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadLocations()
    }

    fun loadLocations() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = LocationsUiState(isLoading = true)
            delay(4000)
            _uiState.value = LocationsUiState(
                isLoading = false,
                data = locationDb.getAllLocations()
            )
        }
    }

    fun onLoadingClick() {
        loadJob?.cancel()
        _uiState.update { it.copy(isLoading = false, hasError = true) }
    }
}