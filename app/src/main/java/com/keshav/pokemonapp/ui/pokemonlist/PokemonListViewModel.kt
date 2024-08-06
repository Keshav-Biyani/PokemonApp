package com.keshav.pokemonapp.ui.pokemonlist

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keshav.pokemonapp.api.RetrofitInstance
import com.keshav.pokemonapp.api.RetrofitInstance.PAGE_SIZE
import com.keshav.pokemonapp.models.PokemonListData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.Locale

class PokemonListViewModel : ViewModel() {
    private var currentPage = 0

    // Using StateFlow for better Compose integration
    private val _pokemonList = MutableStateFlow<List<PokemonListData>>(emptyList())
    val pokemonList: StateFlow<List<PokemonListData>> get() = _pokemonList

    private val _isEndReached = MutableStateFlow(false)
    val isEndReached: StateFlow<Boolean> get() = _isEndReached

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> get() = _errorMessage

    init {
        getPokemonList()
    }

    fun getPokemonList() {
        if (_isLoading.value || _isEndReached.value) return

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.apiService.getPokemonList(PAGE_SIZE, currentPage * PAGE_SIZE)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _isEndReached.value = currentPage * PAGE_SIZE >= body.count

                        val pokedexEntries = body.results.mapIndexed { index, entry ->
                            val number = if (entry.url.endsWith("/")) {
                                entry.url.dropLast(1).takeLastWhile { it.isDigit() }
                            } else {
                                entry.url.takeLastWhile { it.isDigit() }
                            }
                            val url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$number.png"
                            PokemonListData(entry.name.capitalize(Locale.ROOT), url, number.toInt())
                        }
                        currentPage++

                        _errorMessage.value = ""
                        _isLoading.value = false
                        _pokemonList.value += pokedexEntries

                        Log.e("Size DONE!", _pokemonList.value.toString())
                    }
                } else {
                    _errorMessage.value = "Failed to load data: ${response.message()}"
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = e.message.toString()
                Log.e("EORRr", _errorMessage.value)
            }
        }
    }
}
