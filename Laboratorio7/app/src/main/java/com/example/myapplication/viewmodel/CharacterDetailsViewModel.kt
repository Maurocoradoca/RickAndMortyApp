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
import com.example.myapplication.Character
import com.example.myapplication.data.ServiceLocator
import com.example.myapplication.data.repository.CharacterRepository
import com.example.myapplication.navigation.CharacterDetailsRoute
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CharacterDetailsUiState(
    val isLoading: Boolean = true,
    val data: Character? = null,
    val hasError: Boolean = false
)

class CharacterDetailsViewModel(
    private val repository: CharacterRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val characterId: Int = savedStateHandle.toRoute<CharacterDetailsRoute>().id

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
            repository.getCharacter(characterId)
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
            repository.setFavorite(character.id, !character.isFavorite)
        }
    }

    fun deleteCharacter(onDeleted: () -> Unit) {
        viewModelScope.launch {
            repository.deleteCharacter(characterId)
            onDeleted()
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
                CharacterDetailsViewModel(
                    ServiceLocator.characterRepository(app),
                    createSavedStateHandle()
                )
            }
        }
    }
}