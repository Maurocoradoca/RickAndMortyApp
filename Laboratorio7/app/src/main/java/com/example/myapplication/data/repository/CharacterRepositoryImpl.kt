package com.example.myapplication.data.repository

import androidx.room.withTransaction
import com.example.myapplication.Character
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.CharacterEntity
import com.example.myapplication.data.remote.RickAndMortyApi
import com.example.myapplication.data.toEntity
import com.example.myapplication.data.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CharacterRepositoryImpl(
    private val api: RickAndMortyApi,
    private val database: AppDatabase
) : CharacterRepository {

    private val dao = database.characterDao()

    override fun getCharacters(): Flow<List<Character>> =
        dao.getAll().map { entities -> entities.map { it.toModel() } }

    override fun getCharacter(id: Int): Flow<Character?> =
        dao.getById(id).map { it?.toModel() }

    override suspend fun refreshFromApi() {
        val entities = api.getAllCharacters().map { it.toEntity() }
        database.withTransaction {
            dao.deleteAll()
            dao.insertAll(entities)
        }
    }

    override suspend fun createCharacter(
        name: String, status: String, species: String, gender: String, image: String
    ) {
        database.withTransaction {
            val newId = minOf(dao.getMinId(), 0) - 1
            dao.insert(
                CharacterEntity(
                    id = newId,
                    name = name.trim(),
                    status = status.trim(),
                    species = species.trim(),
                    gender = gender.trim(),
                    image = image.trim()
                )
            )
        }
    }

    override suspend fun updateCharacter(
        id: Int, name: String, status: String, species: String, gender: String, image: String
    ) {
        dao.updateDetails(id, name.trim(), status.trim(), species.trim(), gender.trim(), image.trim())
    }

    override suspend fun deleteCharacter(id: Int) = dao.deleteById(id)

    override suspend fun setFavorite(id: Int, isFavorite: Boolean) =
        dao.updateFavorite(id, isFavorite)

    override suspend fun clear() = dao.deleteAll()
}