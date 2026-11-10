package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myapplication.Location
import com.example.myapplication.data.ServiceLocator
import com.example.myapplication.data.repository.LocationRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class LocationFilter(val label: String) {
    ALL("Todas"),
    FAVORITES("Favoritas"),
    NOT_FAVORITES("No favoritas")
}

data class LocationsUiState(
    val isLoading: Boolean = true,
    val data: List<Location> = emptyList(),
    val hasError: Boolean = false,
    val filter: LocationFilter = LocationFilter.ALL
)

class LocationsViewModel(
    private val repository: LocationRepository
) : ViewModel() {

    private val filter = MutableStateFlow(LocationFilter.ALL)

    private val _uiState = MutableStateFlow(LocationsUiState())
    val uiState: StateFlow<LocationsUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadLocations()
    }

    fun loadLocations() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = LocationsUiState(isLoading = true, filter = filter.value)
            combine(repository.getLocations(), filter) { locations, selected ->
                LocationsUiState(
                    isLoading = false,
                    filter = selected,
                    data = when (selected) {
                        LocationFilter.ALL -> locations
                        LocationFilter.FAVORITES -> locations.filter { it.isFavorite }
                        LocationFilter.NOT_FAVORITES -> locations.filter { !it.isFavorite }
                    }
                )
            }
                .catch {
                    _uiState.value = LocationsUiState(isLoading = false, hasError = true)
                }
                .collect { _uiState.value = it }
        }
    }

    fun setFilter(newFilter: LocationFilter) {
        filter.value = newFilter
    }

    fun toggleFavorite(location: Location) {
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
                LocationsViewModel(ServiceLocator.locationRepository(app))
            }
        }
    }
}