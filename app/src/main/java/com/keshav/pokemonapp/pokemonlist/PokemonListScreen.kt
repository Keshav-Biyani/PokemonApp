package com.keshav.pokemonapp.pokemonlist

import android.util.Log
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.decode.SvgDecoder
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.keshav.pokemonapp.R
import com.keshav.pokemonapp.models.PokemonListData


@Composable
fun PokemonListScreen(navController: NavController){
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val viewModel : PokemonListViewModel = PokemonListViewModel()
    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.verticalScroll(state = rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            TopBar(
                imageModifier = Modifier.align(CenterHorizontally),
                textModifier = Modifier.align(Alignment.Start)
            )



            // Grid Section
            PokemonList(navController, screenHeight,viewModel)
        }

    }
}




@Composable
private fun TopBar(imageModifier: Modifier, textModifier: Modifier) {
    Image(
        painter = painterResource(id = R.drawable.ic_pokemon_logo),
        contentDescription = "Pokemon logo",
        modifier = imageModifier
            .fillMaxWidth()
    )
    Text(
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(stringResource(R.string.welcome_title))
            }
            append("\n")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(stringResource(R.string.welcome_title_))
            }
        },
        //fontFamily = Roboto,
        fontWeight = FontWeight.Black,
        fontSize = 32.sp,
        color = MaterialTheme.colorScheme.secondary,
        modifier = textModifier
            .padding(16.dp)
    )
}




@Composable
fun PokemonList(
    navController: NavController,
    screenHeight: Dp,
    viewModel: PokemonListViewModel
) {
    val pokemonList by remember { viewModel.pokemonList }
    val isEndReached by remember { viewModel.isEndReached }
    val isLoading by remember { viewModel.isLoading }
    val workflowError by remember { viewModel.errorMessage }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        // Display the Pokemon entries
        Log.e("Size",pokemonList.size.toString())
        LazyVerticalGrid(
            modifier = Modifier.height(screenHeight),
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(pokemonList.size) { index ->
                PokemonEntryCard(
                    entry = pokemonList[index],
                    navController = navController,
                    modifier = Modifier,
                    viewModel
                )
                if (index >= pokemonList.size - 1 && !isEndReached && !isLoading) {
                    viewModel.getPokemonList()
                }
            }
        }

        // Loading indicator
        if (isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .align(Center)
            )
        }

        // Retry option
        if (workflowError.isNotEmpty()) {
            Retry(
                error = workflowError,
                onRetry = { viewModel.getPokemonList() },
            )
        }
    }
}


@Composable
fun PokemonEntryCard(
    entry: PokemonListData,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: PokemonListViewModel
) {
    val defaultPokemonColor = MaterialTheme.colorScheme.tertiary
    var pokemonColor by remember {
        mutableStateOf(defaultPokemonColor)
    }

    Box(
        contentAlignment = Center,
        modifier = modifier
            .shadow(5.dp, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .aspectRatio(1f) // Making it a square
            .background(pokemonColor)
            .clickable {
                navController.navigate(
                    "pokemon_details_screen/${pokemonColor.toArgb()}/${entry.pokemonName}"
                )
            }
    ) {
        Column {
            val imageUrl = entry.imageURl
            val placeholderImage = R.drawable.ic_pokemon_logo
            val imageRequest = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .memoryCacheKey(imageUrl)
                .diskCacheKey(imageUrl)
                .error(placeholderImage)
                .decoderFactory(SvgDecoder.Factory())
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .build()
            SubcomposeAsyncImage(
                model = imageRequest,
                modifier = Modifier
                    .size(120.dp)
                    .align(CenterHorizontally),
                contentDescription = entry.pokemonName,
                loading = {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.scale(0.5f)
                    )
                },
                onSuccess = { success ->
                    val drawable = success.result.drawable

//                    viewModel.generatePokemonColor(drawable) { color ->
//                        pokemonColor = color
//                    }
                }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = entry.pokemonName,
                   // fontFamily = RobotoCondensed,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Forward",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
fun Retry(
    error: String,
    onRetry: () -> Unit
) {
    Column {
        Text(error, color = Red, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onRetry() },
            modifier = Modifier
                .align(CenterHorizontally)
                .background(Red, RoundedCornerShape(8.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = Red)
        ) {
            Text(
                text = stringResource(R.string.retry),
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}