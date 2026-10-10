package com.example.myapplication.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferencesRepository(private val context: Context) {

    private val nameKey = stringPreferencesKey("name")

    val userName: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[nameKey]
    }

    suspend fun saveName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[nameKey] = name
        }
    }

    suspend fun clearName() {
        context.dataStore.edit { preferences ->
            preferences.remove(nameKey)
        }
    }
}