package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.Character
import com.example.myapplication.CharacterDb
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CharactersUiState(
    val isLoading: Boolean = true,
    val data: List<Character> = emptyList(),
    val hasError: Boolean = false
)

class CharactersViewModel : ViewModel() {

    private val characterDb = CharacterDb()

    private val _uiState = MutableStateFlow(CharactersUiState())
    val uiState: StateFlow<CharactersUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadCharacters()
    }

    fun loadCharacters() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = CharactersUiState(isLoading = true)
            delay(4000)
            _uiState.value = CharactersUiState(
                isLoading = false,
                data = characterDb.getAllCharacters()
            )
        }
    }

    fun onLoadingClick() {
        loadJob?.cancel()
        _uiState.update { it.copy(isLoading = false, hasError = true) }
    }
}