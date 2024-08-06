package com.keshav.pokemonapp.pokemonInfo

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keshav.pokemonapp.api.RetrofitInstance
import com.keshav.pokemonapp.api.responses.Pokemon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PokemonInfoUiState {
    data object Loading : PokemonInfoUiState()
    data class Success(val pokemonInfo: Pokemon) : PokemonInfoUiState()
    data class Error(val error: String) : PokemonInfoUiState()
}
class PokemonInfoViewModel :ViewModel() {
    private val _uiState = MutableStateFlow<PokemonInfoUiState>(PokemonInfoUiState.Loading)
    val uiState = _uiState.asStateFlow()
    fun getPokemonInfo(id : Int) {
        Log.e("HELLOff","HELLOff")

        viewModelScope.launch(Dispatchers.Main) {
            val response = RetrofitInstance.apiService.getPokemonInfo(id)
            Log.e("Responese",response.toString())
            try {
                setSuccessState(response.body()!!)
                Log.e("HELLO","body")

            }catch (e: Exception) {
                setErrorState(e.message.orEmpty())
                Log.e("Error",e.message.toString())
            }

        }
    }
    private fun setSuccessState(pokemonInfo: Pokemon) {
        Log.e("HELLO","HELLOdddd")

        _uiState.value = PokemonInfoUiState.Success(pokemonInfo)
    }
    fun setErrorState(error: String) {
        _uiState.value = PokemonInfoUiState.Error(error)
    }


}