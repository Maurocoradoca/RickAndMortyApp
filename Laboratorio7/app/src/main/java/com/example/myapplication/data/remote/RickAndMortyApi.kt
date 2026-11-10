package com.example.myapplication.data.remote

interface RickAndMortyApi {
    suspend fun getAllCharacters(): List<CharacterDto>
    suspend fun getAllLocations(): List<LocationDto>
}