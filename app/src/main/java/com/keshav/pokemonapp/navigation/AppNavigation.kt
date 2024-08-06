package com.keshav.pokemonapp.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.keshav.pokemonapp.ui.pokemonInfo.PokemonInfoScreen
import com.keshav.pokemonapp.ui.pokemonlist.PokemonListScreen


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController, startDestination = AppScreen.PokemonsScreen.route
    ) {
        //composable(route = AppScreen.SplashScreen.route) { SplashScreen(navController) }

        composable(route = AppScreen.PokemonsScreen.route) { PokemonListScreen(navController ) }

        composable(
            route = AppScreen.PokemonInfoScreen.route + "/{name}",
            arguments = listOf(
                navArgument(name = "name") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = false
                }
            )
        ) {
            val name = it.arguments?.getString("name").orEmpty()
            Log.e("Id",name)
            PokemonInfoScreen(navController, name.toInt())
        }
    }
}