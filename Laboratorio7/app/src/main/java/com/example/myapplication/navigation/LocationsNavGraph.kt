package com.example.myapplication.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.myapplication.ui.screens.locationdetails.LocationDetailsScreen
import com.example.myapplication.ui.screens.locations.LocationsScreen

fun NavGraphBuilder.locationsGraph(navController: NavHostController) {
    navigation<LocationsGraph>(startDestination = LocationsRoute) {

        composable<LocationsRoute> {
            LocationsScreen(
                onLocationClick = { locationId ->
                    navController.navigate(LocationDetailsRoute(id = locationId))
                }
            )
        }

        composable<LocationDetailsRoute> {
            LocationDetailsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}