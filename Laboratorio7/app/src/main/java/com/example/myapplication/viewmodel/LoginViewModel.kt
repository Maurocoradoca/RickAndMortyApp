package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.UserPreferencesRepository
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UserPreferencesRepository(application)

    fun login(name: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.saveName(name.trim())
            onSuccess()
        }
    }
}