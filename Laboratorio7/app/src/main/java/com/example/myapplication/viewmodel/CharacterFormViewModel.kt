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
import com.example.myapplication.data.ServiceLocator
import com.example.myapplication.data.repository.CharacterRepository
import com.example.myapplication.navigation.CharacterFormRoute
import com.example.myapplication.navigation.NEW_CHARACTER_ID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CharacterFormUiState(
    val isLoading: Boolean = false,
    val isEditing: Boolean = false,
    val name: String = "",
    val status: String = "",
    val species: String = "",
    val gender: String = "",
    val image: String = ""
) {
    val isValid: Boolean
        get() = name.isNotBlank() && status.isNotBlank() &&
                species.isNotBlank() && gender.isNotBlank()
}

class CharacterFormViewModel(
    private val repository: CharacterRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val characterId: Int = savedStateHandle.toRoute<CharacterFormRoute>().id
    private val isEditing = characterId != NEW_CHARACTER_ID

    private val _uiState = MutableStateFlow(
        CharacterFormUiState(isLoading = isEditing, isEditing = isEditing)
    )
    val uiState: StateFlow<CharacterFormUiState> = _uiState.asStateFlow()

    init {
        if (isEditing) {
            viewModelScope.launch {
                val character = repository.getCharacter(characterId).first()
                _uiState.value = if (character != null) {
                    CharacterFormUiState(
                        isLoading = false,
                        isEditing = true,
                        name = character.name,
                        status = character.status,
                        species = character.species,
                        gender = character.gender,
                        image = character.image
                    )
                } else {
                    CharacterFormUiState(isLoading = false, isEditing = true)
                }
            }
        }
    }

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value) }
    fun onStatusChange(value: String) = _uiState.update { it.copy(status = value) }
    fun onSpeciesChange(value: String) = _uiState.update { it.copy(species = value) }
    fun onGenderChange(value: String) = _uiState.update { it.copy(gender = value) }
    fun onImageChange(value: String) = _uiState.update { it.copy(image = value) }

    fun save(onSaved: () -> Unit) {
        val form = _uiState.value
        if (!form.isValid) return
        viewModelScope.launch {
            if (isEditing) {
                repository.updateCharacter(
                    characterId, form.name, form.status, form.species, form.gender, form.image
                )
            } else {
                repository.createCharacter(
                    form.name, form.status, form.species, form.gender, form.image
                )
            }
            onSaved()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as Application
                CharacterFormViewModel(
                    ServiceLocator.characterRepository(app),
                    createSavedStateHandle()
                )
            }
        }
    }
}