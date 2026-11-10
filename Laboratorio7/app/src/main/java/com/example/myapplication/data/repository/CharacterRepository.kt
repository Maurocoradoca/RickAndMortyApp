package com.example.myapplication.data.repository

import com.example.myapplication.Character
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {
    fun getCharacters(): Flow<List<Character>>
    fun getCharacter(id: Int): Flow<Character?>

    suspend fun refreshFromApi()

    suspend fun createCharacter(name: String, status: String, species: String, gender: String, image: String)
    suspend fun updateCharacter(id: Int, name: String, status: String, species: String, gender: String, image: String)
    suspend fun deleteCharacter(id: Int)
    suspend fun setFavorite(id: Int, isFavorite: Boolean)
    suspend fun clear()
}