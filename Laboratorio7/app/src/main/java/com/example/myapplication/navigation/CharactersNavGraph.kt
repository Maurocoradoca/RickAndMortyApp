package com.example.myapplication.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.myapplication.ui.screens.characterdetails.CharacterDetailsScreen
import com.example.myapplication.ui.screens.characterform.CharacterFormScreen
import com.example.myapplication.ui.screens.characters.CharactersScreen

fun NavGraphBuilder.charactersGraph(navController: NavHostController) {
    navigation<CharactersGraph>(startDestination = CharactersRoute) {

        composable<CharactersRoute> {
            CharactersScreen(
                onCharacterClick = { characterId ->
                    navController.navigate(CharacterDetailsRoute(id = characterId))
                },
                onAddClick = {
                    navController.navigate(CharacterFormRoute())
                }
            )
        }

        composable<CharacterDetailsRoute> {
            CharacterDetailsScreen(
                onBackClick = { navController.popBackStack() },
                onEditClick = { characterId ->
                    navController.navigate(CharacterFormRoute(id = characterId))
                }
            )
        }

        composable<CharacterFormRoute> {
            CharacterFormScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}