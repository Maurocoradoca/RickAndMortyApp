package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myapplication.data.ServiceLocator
import com.example.myapplication.data.UserPreferencesRepository
import com.example.myapplication.data.repository.CharacterRepository
import com.example.myapplication.data.repository.LocationRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userPreferences: UserPreferencesRepository,
    private val characterRepository: CharacterRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    val userName: StateFlow<String> = userPreferences.userName
        .map { it ?: "" }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            userPreferences.clearName()
            characterRepository.clear()
            locationRepository.clear()
            onLoggedOut()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as Application
                ProfileViewModel(
                    UserPreferencesRepository(app),
                    ServiceLocator.characterRepository(app),
                    ServiceLocator.locationRepository(app)
                )
            }
        }
    }
}