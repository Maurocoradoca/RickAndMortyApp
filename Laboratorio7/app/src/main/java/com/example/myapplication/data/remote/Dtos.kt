package com.example.myapplication.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class PageInfoDto(val pages: Int)

@Serializable
data class PageDto<T>(
    val info: PageInfoDto,
    val results: List<T>
)

@Serializable
data class CharacterDto(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val gender: String,
    val image: String
)

@Serializable
data class LocationDto(
    val id: Int,
    val name: String,
    val type: String,
    val dimension: String
)