package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.Location
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.toModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LocationsUiState(
    val isLoading: Boolean = true,
    val data: List<Location> = emptyList(),
    val hasError: Boolean = false
)

class LocationsViewModel(application: Application) : AndroidViewModel(application) {

    private val locationDao = AppDatabase.getInstance(application).locationDao()

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
            locationDao.getAll()
                .map { entities -> entities.map { it.toModel() } }
                .catch {
                    _uiState.value = LocationsUiState(isLoading = false, hasError = true)
                }
                .collect { locations ->
                    _uiState.value = LocationsUiState(isLoading = false, data = locations)
                }
        }
    }

    fun onLoadingClick() {
        loadJob?.cancel()
        _uiState.update { it.copy(isLoading = false, hasError = true) }
    }
}