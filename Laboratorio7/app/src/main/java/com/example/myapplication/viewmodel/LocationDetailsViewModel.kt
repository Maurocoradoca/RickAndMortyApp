package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.toRoute
import com.example.myapplication.Location
import com.example.myapplication.data.ServiceLocator
import com.example.myapplication.data.repository.LocationRepository
import com.example.myapplication.navigation.LocationDetailsRoute
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LocationDetailsUiState(
    val isLoading: Boolean = true,
    val data: Location? = null,
    val hasError: Boolean = false
)

class LocationDetailsViewModel(
    private val repository: LocationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val locationId: Int = savedStateHandle.toRoute<LocationDetailsRoute>().id

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
            repository.getLocation(locationId)
                .catch {
                    _uiState.value = LocationDetailsUiState(isLoading = false, hasError = true)
                }
                .collect { location ->
                    _uiState.value = LocationDetailsUiState(isLoading = false, data = location)
                }
        }
    }

    fun toggleFavorite() {
        val location = _uiState.value.data ?: return
        viewModelScope.launch {
            repository.setFavorite(location.id, !location.isFavorite)
        }
    }

    fun onLoadingClick() {
        loadJob?.cancel()
        _uiState.update { it.copy(isLoading = false, hasError = true) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as Application
                LocationDetailsViewModel(
                    ServiceLocator.locationRepository(app),
                    createSavedStateHandle()
                )
            }
        }
    }
}