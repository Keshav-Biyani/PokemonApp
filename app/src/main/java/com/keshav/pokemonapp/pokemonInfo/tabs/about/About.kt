package com.keshav.pokemonapp.pokemonInfo.tabs.about
import android.graphics.Color.BLACK
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.keshav.pokemonapp.api.responses.Pokemon
import com.keshav.pokemonapp.pokemonInfo.components.CircularProgress


@Composable
fun AboutTab(
    paddingValues: PaddingValues,
    pokemonInfo: Pokemon,
    color: Int,
) {
    SuccessScreen(
            paddingValues = paddingValues,
            pokemonInfo = pokemonInfo,
            color = color,

        )
    }



@Composable
private fun SuccessScreen(
    paddingValues: PaddingValues = PaddingValues(),
    pokemonInfo: Pokemon ,
    color: Int = BLACK,

) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(top = paddingValues.calculateTopPadding())
            .background(Color.White)
            .verticalScroll(rememberScrollState()),
    ) {


        Row(
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.66f)
        ) {
            Column(
                modifier = Modifier.weight(0.33f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 12.dp),
                    text = "Base Exp.",
                    color = Color(BLACK),
                    fontSize = 16.sp,
                )
                CircularProgress(
                    progress = pokemonInfo.base_experience,
                    total = 340f,
                    color = color,
                )
            }

            Column(
                modifier = Modifier.weight(0.33f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 12.dp),
                    text = "Height",
                    color = Color(BLACK),
                    fontSize = 16.sp,
                )
                CircularProgress(
                    progress = pokemonInfo.height,
                    total = 70f,
                    text = " m",
                    color = color,
                )
            }

            Column(
                modifier = Modifier.weight(0.33f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 12.dp),
                    text = "Weight",
                    color = Color(BLACK),
                    fontSize = 16.sp,
                )
                CircularProgress(
                    progress = pokemonInfo.weight,
                    total = 1200f,
                    text = " Kg",
                    color = color,
                )
            }
        }
    }
}
