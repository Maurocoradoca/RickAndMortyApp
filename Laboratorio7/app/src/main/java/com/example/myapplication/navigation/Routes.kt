package com.example.myapplication.navigation

import kotlinx.serialization.Serializable

@Serializable
object LoginRoute

@Serializable
object MainRoute

@Serializable
object CharactersGraph

@Serializable
object CharactersRoute

@Serializable
data class CharacterDetailsRoute(val id: Int)

@Serializable
object LocationsGraph

@Serializable
object LocationsRoute

@Serializable
data class LocationDetailsRoute(val id: Int)

@Serializable
object ProfileRoute
