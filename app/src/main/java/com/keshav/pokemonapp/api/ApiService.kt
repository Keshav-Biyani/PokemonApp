package com.keshav.pokemonapp.api

import com.keshav.pokemonapp.api.responses.Pokemon
import com.keshav.pokemonapp.api.responses.PokemonList
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("pokemon/{id}")
   suspend fun getPokemonInfo(@Path("id") id: Int): Response<Pokemon>
    @GET("pokemon")
   suspend fun getPokemonList(@Query("limit") limit: Int, @Query("offset") offset: Int): Response<PokemonList>
}