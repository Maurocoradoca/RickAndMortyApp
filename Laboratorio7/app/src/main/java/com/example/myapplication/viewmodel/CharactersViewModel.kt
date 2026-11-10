package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myapplication.Character
import com.example.myapplication.data.ServiceLocator
import com.example.myapplication.data.repository.CharacterRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CharactersUiState(
    val isLoading: Boolean = true,
    val data: List<Character> = emptyList(),
    val hasError: Boolean = false,
    val showOnlyFavorites: Boolean = false
)

class CharactersViewModel(
    private val repository: CharacterRepository
) : ViewModel() {

    private val showOnlyFavorites = MutableStateFlow(false)

    private val _uiState = MutableStateFlow(CharactersUiState())
    val uiState: StateFlow<CharactersUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadCharacters()
    }

    fun loadCharacters() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = CharactersUiState(
                isLoading = true,
                showOnlyFavorites = showOnlyFavorites.value
            )
            combine(repository.getCharacters(), showOnlyFavorites) { characters, onlyFavorites ->
                CharactersUiState(
                    isLoading = false,
                    data = if (onlyFavorites) characters.filter { it.isFavorite } else characters,
                    showOnlyFavorites = onlyFavorites
                )
            }
                .catch {
                    _uiState.value = CharactersUiState(isLoading = false, hasError = true)
                }
                .collect { _uiState.value = it }
        }
    }

    fun toggleFavorite(character: Character) {
        viewModelScope.launch {
            repository.setFavorite(character.id, !character.isFavorite)
        }
    }

    fun toggleFavoritesFilter() {
        showOnlyFavorites.update { !it }
    }

    fun onLoadingClick() {
        loadJob?.cancel()
        _uiState.update { it.copy(isLoading = false, hasError = true) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as Application
                CharactersViewModel(ServiceLocator.characterRepository(app))
            }
        }
    }
}