package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.myapplication.Character
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.toModel
import com.example.myapplication.navigation.CharacterDetailsRoute
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CharacterDetailsUiState(
    val isLoading: Boolean = true,
    val data: Character? = null,
    val hasError: Boolean = false
)

class CharacterDetailsViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val characterId: Int = savedStateHandle.toRoute<CharacterDetailsRoute>().id

    private val characterDao = AppDatabase.getInstance(application).characterDao()

    private val _uiState = MutableStateFlow(CharacterDetailsUiState())
    val uiState: StateFlow<CharacterDetailsUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadCharacter()
    }

    fun loadCharacter() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = CharacterDetailsUiState(isLoading = true)
            characterDao.getById(characterId)
                .map { it?.toModel() }
                .catch {
                    _uiState.value = CharacterDetailsUiState(isLoading = false, hasError = true)
                }
                .collect { character ->
                    _uiState.value = CharacterDetailsUiState(isLoading = false, data = character)
                }
        }
    }

    fun toggleFavorite() {
        val character = _uiState.value.data ?: return
        viewModelScope.launch {
            characterDao.updateFavorite(character.id, !character.isFavorite)
        }
    }

    fun onLoadingClick() {
        loadJob?.cancel()
        _uiState.update { it.copy(isLoading = false, hasError = true) }
    }
}