package com.keshav.pokemonapp.pokemonlist

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
    var endReached= mutableStateOf(false)
    var errorMessage: String by mutableStateOf("")
    fun getPokemonList(phoneNumber : String) {
        isLoading.value = true
        viewModelScope.launch {
            val response = RetrofitInstance.apiService.getPokemonList(PAGE_SIZE,currentPage* PAGE_SIZE)

            try {
                if(response.isSuccessful){
                    endReached.value = currentPage * PAGE_SIZE >=response.body()!!.count
                    val pokemonEntries = response.body()!!.results.mapIndexed { index, entry ->
                        val number = if(entry.url.endsWith("/")) {
                            entry.url.dropLast(1).takeLastWhile { it.isDigit() }
                        } else {
                            entry.url.takeLastWhile { it.isDigit() }
                        }
                        val url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${number}.png"
                        PokemonListData(entry.name.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(
                                Locale.ROOT
                            ) else it.toString()
                        }, url, number.toInt())
                    }
                    currentPage++
                    isLoading.value = false
                    pokemonList.value += pokemonEntries
                }


            }
            catch (e: Exception) {
                isLoading.value= false
                errorMessage = e.message.toString()
            }
        }
    }
}