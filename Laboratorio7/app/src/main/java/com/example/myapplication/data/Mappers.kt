package com.example.myapplication.data

import com.example.myapplication.Character
import com.example.myapplication.Location

// Entity -> Model (lo usan los ViewModels al leer de Room)
fun CharacterEntity.toModel(): Character = Character(
    id = id,
    name = name,
    status = status,
    species = species,
    gender = gender,
    image = image,
    isFavorite = isFavorite
)

fun LocationEntity.toModel(): Location = Location(
    id = id,
    name = name,
    type = type,
    dimension = dimension
)

// Model -> Entity (lo usa la sincronización inicial)
fun Character.toEntity(): CharacterEntity = CharacterEntity(
    id = id,
    name = name,
    status = status,
    species = species,
    gender = gender,
    image = image,
    isFavorite = isFavorite
)

fun Location.toEntity(): LocationEntity = LocationEntity(
    id = id,
    name = name,
    type = type,
    dimension = dimension
)