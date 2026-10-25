package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.myapplication.Location
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.toModel
import com.example.myapplication.navigation.LocationDetailsRoute
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LocationDetailsUiState(
    val isLoading: Boolean = true,
    val data: Location? = null,
    val hasError: Boolean = false
)

class LocationDetailsViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val locationId: Int = savedStateHandle.toRoute<LocationDetailsRoute>().id

    private val locationDao = AppDatabase.getInstance(application).locationDao()

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
            locationDao.getById(locationId)
                .map { it?.toModel() }
                .catch {
                    _uiState.value = LocationDetailsUiState(isLoading = false, hasError = true)
                }
                .collect { location ->
                    _uiState.value = LocationDetailsUiState(isLoading = false, data = location)
                }
        }
    }

    fun onLoadingClick() {
        loadJob?.cancel()
        _uiState.update { it.copy(isLoading = false, hasError = true) }
    }
}