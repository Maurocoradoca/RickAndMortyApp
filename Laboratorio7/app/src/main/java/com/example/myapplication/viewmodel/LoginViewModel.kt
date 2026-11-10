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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class LoginViewModel(
    private val userPreferences: UserPreferencesRepository,
    private val characterRepository: CharacterRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(name: String, onSuccess: () -> Unit) {
        if (_uiState.value.isLoading) return
        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            try {
                coroutineScope {
                    val characters = async { characterRepository.refreshFromApi() }
                    val locations = async { locationRepository.refreshFromApi() }
                    characters.await()
                    locations.await()
                }
                userPreferences.saveName(name.trim())
                _uiState.value = LoginUiState()
                onSuccess()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = LoginUiState(
                    errorMessage = "No se pudieron descargar los datos.\nRevisa tu conexión a internet e intenta de nuevo."
                )
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as Application
                LoginViewModel(
                    UserPreferencesRepository(app),
                    ServiceLocator.characterRepository(app),
                    ServiceLocator.locationRepository(app)
                )
            }
        }
    }
}