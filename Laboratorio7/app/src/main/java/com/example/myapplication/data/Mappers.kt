package com.example.myapplication.data

import com.example.myapplication.Character
import com.example.myapplication.Location
import com.example.myapplication.data.remote.CharacterDto
import com.example.myapplication.data.remote.LocationDto

fun CharacterDto.toEntity() = CharacterEntity(
    id = id,
    name = name,
    status = status,
    species = species,
    gender = gender,
    image = image
)

fun LocationDto.toEntity() = LocationEntity(
    id = id,
    name = name,
    type = type,
    dimension = dimension
)

fun CharacterEntity.toModel() = Character(
    id = id,
    name = name,
    status = status,
    species = species,
    gender = gender,
    image = image,
    isFavorite = isFavorite
)

fun LocationEntity.toModel() = Location(
    id = id,
    name = name,
    type = type,
    dimension = dimension,
    isFavorite = isFavorite
)