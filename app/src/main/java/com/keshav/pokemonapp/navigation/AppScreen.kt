package com.keshav.pokemonapp.navigation


sealed class AppScreen(val route: String) {
    //data object SplashScreen : AppScreen("splash_screen")
    data object PokemonsScreen : AppScreen("pokemons_screen")
    data object PokemonInfoScreen : AppScreen("pokemon_info_screen")
}