package com.keshav.pokemonapp.ui.pokemonlist

import android.content.Intent
import android.content.res.Configuration
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.material.icons.Icons
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.decode.SvgDecoder
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.keshav.pokemonapp.R
import com.keshav.pokemonapp.Utils.ConnectionManager
import com.keshav.pokemonapp.Utils.getDominantColor
import com.keshav.pokemonapp.models.PokemonListData


@Composable
fun PokemonListScreen(navController: NavController){
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    var isNetwork by remember {
        mutableStateOf(false)
    }
    val systemUiController = rememberSystemUiController()
    val context = LocalContext.current

    systemUiController.setStatusBarColor ( Color(0xFF2C3251))
    isNetwork = if (ConnectionManager().checkConectivity(context)) {
        //  Toast.makeText(context, "Condition is true", Toast.LENGTH_SHORT).show()
        false
    } else {
        // Toast.makeText(context, "Please Connect to network for better result", Toast.LENGTH_SHORT).show()
        true
    }

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

            if (!isNetwork) {
                val viewModel : PokemonListViewModel = PokemonListViewModel()
                // Grid Section
                PokemonList(navController, screenHeight, viewModel)
                Log.e("Pokemon","Poke")
            }
            else{
                AlertDialogExample {
                    isNetwork = false
                }
            }
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
            .padding(10.dp)
    )
    Text(
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                append(stringResource(R.string.welcome_title))
            }
            append("\n")
            withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
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
    // Collect StateFlow as State using collectAsState
    val pokemonList by viewModel.pokemonList.collectAsState()
    val isEndReached by viewModel.isEndReached.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val workflowError by viewModel.errorMessage.collectAsState()

    Log.e("Size DONE", pokemonList.toString())

    // Determine orientation
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Set columns based on orientation
    val columns = if (isLandscape) 3 else 2

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        // Display the Pokemon entries
        Log.e("", pokemonList.size.toString())
        LazyVerticalGrid(
            modifier = Modifier.height(screenHeight),
            columns = GridCells.Fixed(columns),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(pokemonList.size) { index ->
                PokemonEntryCard(
                    entry = pokemonList[index],
                    navController = navController,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(if (isLandscape) 1.5f else 1f) // Adjust aspect ratio for landscape
                )
                if (index >= pokemonList.size - 1 && !isEndReached && !isLoading) {
                    viewModel.getPokemonList()

                    Log.e("RUNNNN", "RUNNN")
                }
            }
        }

        // Loading indicator
        if (isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.align(Alignment.Center)
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
    modifier: Modifier = Modifier
) {
    val defaultPokemonColor = MaterialTheme.colorScheme.tertiary
    var pokemonColor by remember {
        mutableStateOf(defaultPokemonColor)
    }
    val configuration = LocalConfiguration.current
    val imageSize = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        140.dp // Larger size for landscape
    } else {
        120.dp // Default size for portrait
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
                    "pokemon_info_screen/${entry.pokemonId}"
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
                    .size(imageSize)
                    .align(CenterHorizontally),
                contentDescription = entry.pokemonName,
                loading = {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.scale(0.5f)
                    )
                },
                onSuccess = { success ->
                   pokemonColor = Color( getDominantColor(success.result))


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
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
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
@Composable
fun AlertDialogExample(
    onDismissRequest: () -> Unit,
    //onConfirmation: () -> Unit
) {
    val context = LocalContext.current
    AlertDialog(
        icon = {
            Icon(Icons.Default.Settings, contentDescription = "Example Icon", tint = Color(0xFF353D64))
        },
        title = {
            Text(text = "No Internet",color = Black)
        },
        text = {
            Text(text = "Network connectivity is necessary for the app", color = Color.Black)
        },
        onDismissRequest = {
            //onDismissRequest()
           (context as? ComponentActivity)?.finish()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    //    onConfirmation()
                    val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    context.startActivity(intent)
                }
            ) {
                Text("Settings", color =Color(0xFF353D64))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss App",color =Color(0xFF353D64))
            }
        },
        containerColor = White
    )
}