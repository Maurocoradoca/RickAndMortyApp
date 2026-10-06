package com.example.myapplication.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.myapplication.Character
import com.example.myapplication.CharacterDb
import com.example.myapplication.navigation.CharacterDetailsRoute
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CharacterDetailsUiState(
    val isLoading: Boolean = true,
    val data: Character? = null,
    val hasError: Boolean = false
)

class CharacterDetailsViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val characterId: Int = savedStateHandle.toRoute<CharacterDetailsRoute>().id

    private val characterDb = CharacterDb()

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
            delay(2000)
            _uiState.value = CharacterDetailsUiState(
                isLoading = false,
                data = characterDb.getCharacterById(characterId)
            )
        }
    }

    fun onLoadingClick() {
        loadJob?.cancel()
        _uiState.update { it.copy(isLoading = false, hasError = true) }
    }
}