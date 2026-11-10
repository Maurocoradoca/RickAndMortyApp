package com.example.myapplication.data.repository

import androidx.room.withTransaction
import com.example.myapplication.Location
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.remote.RickAndMortyApi
import com.example.myapplication.data.toEntity
import com.example.myapplication.data.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocationRepositoryImpl(
    private val api: RickAndMortyApi,
    private val database: AppDatabase
) : LocationRepository {

    private val dao = database.locationDao()

    override fun getLocations(): Flow<List<Location>> =
        dao.getAll().map { entities -> entities.map { it.toModel() } }

    override fun getLocation(id: Int): Flow<Location?> =
        dao.getById(id).map { it?.toModel() }

    override suspend fun refreshFromApi() {
        val entities = api.getAllLocations().map { it.toEntity() }
        database.withTransaction {
            dao.deleteAll()
            dao.insertAll(entities)
        }
    }

    override suspend fun setFavorite(id: Int, isFavorite: Boolean) =
        dao.updateFavorite(id, isFavorite)

    override suspend fun clear() = dao.deleteAll()
}