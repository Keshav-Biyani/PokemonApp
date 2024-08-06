package com.keshav.pokemonapp.pokemonlist

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keshav.pokemonapp.api.RetrofitInstance
import com.keshav.pokemonapp.api.RetrofitInstance.PAGE_SIZE
import com.keshav.pokemonapp.models.PokemonListData
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.Locale

class PokemonListViewModel :ViewModel() {
    private var currentPage = 0
    var pokemonList = mutableStateOf<List<PokemonListData>>(listOf())
    var isEndReached = mutableStateOf(false)
    var isLoading = mutableStateOf(false)
    private var endReached= mutableStateOf(false)
    var errorMessage = mutableStateOf("")
    init {
        getPokemonList()
    }
    fun getPokemonList() {
        isLoading.value = true
        viewModelScope.launch {
            val response = RetrofitInstance.apiService.getPokemonList(PAGE_SIZE,currentPage* PAGE_SIZE)
            Log.e("ERRROR",response.toString())

            try {
                if(response.isSuccessful){
                    endReached.value = currentPage * PAGE_SIZE >= response.body()!!.count
                    val pokedexEntries = response.body()!!.results.mapIndexed { index, entry ->
                        val number = if(entry.url.endsWith("/")) {
                            entry.url.dropLast(1).takeLastWhile { it.isDigit() }
                        } else {
                            entry.url.takeLastWhile { it.isDigit() }
                        }
                        val url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${number}.png"
                        PokemonListData(entry.name.capitalize(Locale.ROOT), url, number.toInt())
                    }
                    currentPage++

                    errorMessage.value = ""
                    isLoading.value = false
                    pokemonList.value += pokedexEntries

                }


            }
            catch (e: Exception) {
                isLoading.value= false
                errorMessage.value = e.message.toString()
                Log.e("EORRr",errorMessage.toString())
            }

        }
    }
}