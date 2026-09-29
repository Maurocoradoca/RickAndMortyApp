package com.example.myapplication.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.example.myapplication.ui.screens.characterdetails.CharacterDetailsScreen
import com.example.myapplication.ui.screens.characters.CharactersScreen

fun NavGraphBuilder.charactersGraph(navController: NavHostController) {
    navigation<CharactersGraph>(startDestination = CharactersRoute) {

        composable<CharactersRoute> {
            CharactersScreen(
                onCharacterClick = { characterId ->
                    navController.navigate(CharacterDetailsRoute(id = characterId))
                }
            )
        }

        composable<CharacterDetailsRoute> { backStackEntry ->
            val details: CharacterDetailsRoute = backStackEntry.toRoute()
            CharacterDetailsScreen(
                characterId = details.id,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}