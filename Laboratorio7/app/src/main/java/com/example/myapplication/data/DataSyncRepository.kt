package com.example.myapplication.data

import com.example.myapplication.CharacterDb
import com.example.myapplication.LocationDb
import kotlinx.coroutines.delay

class DataSyncRepository(private val database: AppDatabase) {

    private val characterDao = database.characterDao()
    private val locationDao = database.locationDao()

    suspend fun syncInitialData() {
        val alreadySynced = characterDao.count() > 0 && locationDao.count() > 0
        if (alreadySynced) return

        delay(4000)

        characterDao.insertAll(CharacterDb().getAllCharacters().map { it.toEntity() })
        locationDao.insertAll(LocationDb().getAllLocations().map { it.toEntity() })
    }

    suspend fun clearAll() {
        characterDao.deleteAll()
        locationDao.deleteAll()
    }
}