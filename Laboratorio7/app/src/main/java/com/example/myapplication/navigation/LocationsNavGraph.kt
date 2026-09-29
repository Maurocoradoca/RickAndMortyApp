package com.example.myapplication.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.example.myapplication.ui.screens.locationdetails.LocationDetailsScreen
import com.example.myapplication.ui.screens.locations.LocationsScreen

fun NavGraphBuilder.locationsGraph(navController: NavHostController) {
    navigation<LocationsGraph>(startDestination = LocationsRoute) {

        composable<LocationsRoute> {
            LocationsScreen(
                onLocationClick = { locationId ->
                    // Solo se pasa el ID a la otra pantalla
                    navController.navigate(LocationDetailsRoute(id = locationId))
                }
            )
        }

        composable<LocationDetailsRoute> { backStackEntry ->
            val details: LocationDetailsRoute = backStackEntry.toRoute()
            LocationDetailsScreen(
                locationId = details.id,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}