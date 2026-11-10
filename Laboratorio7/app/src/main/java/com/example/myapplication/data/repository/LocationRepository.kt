package com.example.myapplication.data.repository

import com.example.myapplication.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getLocations(): Flow<List<Location>>
    fun getLocation(id: Int): Flow<Location?>
    suspend fun refreshFromApi()
    suspend fun setFavorite(id: Int, isFavorite: Boolean)
    suspend fun clear()
}