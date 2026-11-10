package com.example.myapplication.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(characters: List<CharacterEntity>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(character: CharacterEntity)

    @Query("SELECT * FROM characters ORDER BY id")
    fun getAll(): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM characters WHERE id = :id")
    fun getById(id: Int): Flow<CharacterEntity?>

    @Query(
        "UPDATE characters SET name = :name, status = :status, species = :species, " +
                "gender = :gender, image = :image WHERE id = :id"
    )
    suspend fun updateDetails(
        id: Int,
        name: String,
        status: String,
        species: String,
        gender: String,
        image: String
    )

    @Query("UPDATE characters SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Int, isFavorite: Boolean)

    @Query("DELETE FROM characters WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT COALESCE(MIN(id), 0) FROM characters")
    suspend fun getMinId(): Int

    @Query("DELETE FROM characters")
    suspend fun deleteAll()
}