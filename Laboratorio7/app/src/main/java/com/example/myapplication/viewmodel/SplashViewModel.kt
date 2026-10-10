package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.myapplication.data.UserPreferencesRepository
import kotlinx.coroutines.flow.first

class SplashViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UserPreferencesRepository(application)

    suspend fun getSavedName(): String? = repository.userName.first()
}