package com.example.myapplication.data

import android.content.Context
import com.example.myapplication.data.remote.KtorRickAndMortyApi
import com.example.myapplication.data.remote.RickAndMortyApi
import com.example.myapplication.data.repository.CharacterRepository
import com.example.myapplication.data.repository.CharacterRepositoryImpl
import com.example.myapplication.data.repository.LocationRepository
import com.example.myapplication.data.repository.LocationRepositoryImpl

object ServiceLocator {

    private val api: RickAndMortyApi by lazy { KtorRickAndMortyApi() }

    fun characterRepository(context: Context): CharacterRepository =
        CharacterRepositoryImpl(api, AppDatabase.getInstance(context))

    fun locationRepository(context: Context): LocationRepository =
        LocationRepositoryImpl(api, AppDatabase.getInstance(context))
}