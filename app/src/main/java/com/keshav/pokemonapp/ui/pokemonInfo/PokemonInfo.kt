package com.keshav.pokemonapp.ui.pokemonInfo

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.SuccessResult

import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.keshav.pokemonapp.ui.MainActivity
import com.keshav.pokemonapp.R
import com.keshav.pokemonapp.Utils.enum.getPokemonType
import com.keshav.pokemonapp.Utils.getDominantColor
import com.keshav.pokemonapp.Utils.getSecondDominantColor
import com.keshav.pokemonapp.api.responses.Pokemon
import com.keshav.pokemonapp.api.responses.Type
import com.keshav.pokemonapp.api.responses.getFormattedId
import com.keshav.pokemonapp.ui.pokemonInfo.tabs.about.AboutTab
import com.keshav.pokemonapp.ui.pokemonInfo.tabs.stats.StatsTab

inline fun <reified T : ViewModel> Context.getViewModel() =
    ViewModelProvider(this as MainActivity)[T::class.java]


@Composable
fun PokemonInfoScreen(navController: NavHostController, id : Int) {
    val viewModel = LocalContext.current.getViewModel<PokemonInfoViewModel>()
    val uiState: PokemonInfoUiState by viewModel.uiState.collectAsStateWithLifecycle()
Log.e("HELLO","HELLO")
    LaunchedEffect(Unit) { viewModel.getPokemonInfo(id) }

    when (val state = uiState) {
        is PokemonInfoUiState.Loading -> LoadingScreen()
        is PokemonInfoUiState.Error -> ErrorScreen(navController, state.error)
        is PokemonInfoUiState.Success -> {
            Log.e("HELLO","HELLOScree")

            SuccessScreen(
                navController = navController,
                pokemonInfo = state.pokemonInfo,
                setErrorState = { error -> viewModel.setErrorState(error) })
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.width(64.dp),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun ErrorScreen(navController: NavHostController, error: String = "") {
    AlertDialog(onDismissRequest = { },
        title = { Text(stringResource(R.string.error_occurred)) },
        text = { Text(error) },
        confirmButton = {
            Text(
                modifier = Modifier.clickable { navController.popBackStack() },
                text = stringResource(R.string.accept),
            )
        },
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_error),
                contentDescription = "Error Icon"
            )
        })
}

@Composable
private fun SuccessScreen(
    navController: NavHostController,
    pokemonInfo: Pokemon,
    setErrorState: (error: String) -> Unit,
) {
    val systemUiController = rememberSystemUiController()
    val dominantColor = remember { mutableIntStateOf(Black.value.toInt()) }
    val secondColor = remember { mutableIntStateOf(Black.value.toInt()) }

    systemUiController.setStatusBarColor (color = Color(dominantColor.intValue))

    Scaffold(
        topBar = { TopBar(navController, dominantColor, pokemonInfo) },
        content = { padding ->
            Content(
                padding = padding,
                pokemonInfo = pokemonInfo,
                dominantColor = dominantColor.intValue,
                secondColor = secondColor.intValue,
                onSuccess = { result ->
                    dominantColor.intValue = getDominantColor(result)
                    secondColor.intValue = getSecondDominantColor(result)
                },
                onError = { error -> setErrorState(error) },
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    navController: NavHostController,
    dominantColor: MutableIntState,
    pokemonInfo: Pokemon,
) {
    TopAppBar(colors = TopAppBarDefaults.topAppBarColors().copy(
        containerColor = Color(dominantColor.intValue)
    ), navigationIcon = {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = "backIcon"
            )
        }
    }, title = {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                color = Color.Black,
                text = stringResource(R.string.app_name)
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp),
                textAlign = TextAlign.End,
                color = Color.White,
                text = pokemonInfo.getFormattedId()
            )
        }
    })
}


@SuppressLint("DiscouragedApi")
@Composable
private fun Chip(pokemonType: Type ) {
    val context = LocalContext.current
    val resourceId = context.resources.getIdentifier(
        pokemonType.type.name.lowercase(),
        "drawable",
        context.packageName
    )
    Log.e("Respurce" ,resourceId.toString())
    Card(
        modifier = Modifier
            .padding(8.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors().copy(containerColor = Color(getPokemonType( pokemonType.type.name.uppercase()).color)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = pokemonType.type.name.uppercase(),
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
            Image(
                modifier = Modifier.size(20.dp).padding(start = 8.dp),
                painter = painterResource(id = resourceId),
                contentDescription = "typeImage"
            )
        }
    }
}
@Composable
 fun Content(
    padding: PaddingValues,
    pokemonInfo: Pokemon,
    dominantColor: Int,
    secondColor: Int,
    onSuccess: (result: SuccessResult) -> Unit,
    onError: (error: String) -> Unit,
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Padding for status bar and action bar
    val topPadding = padding.calculateTopPadding()
    val bottomPadding = padding.calculateBottomPadding()

    if (isLandscape) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(dominantColor), Color.White)
                    )
                )
                .padding(top = topPadding, bottom = bottomPadding) // Add padding
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()) // Enable vertical scrolling if needed
            ) {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.5f), // Adjust aspect ratio for landscape
                    model = pokemonInfo.sprites.front_default,
                    contentDescription = "Pokemon image",
                    onError = { error -> onError(error.result.toString()) },
                    onSuccess = { result -> onSuccess(result.result) }
                )

                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp),
                    text = pokemonInfo.name,
                    color = Color(dominantColor),
                    fontSize = 36.sp
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    pokemonInfo.types.forEach { type -> Chip(type) }
                }
            }

            // TabLayout takes remaining space in landscape
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f) // Takes the remaining width
                    .padding(16.dp)
            ) {
                TabLayout(
                    pokemonInfo = pokemonInfo,
                    secondColor = secondColor
                )
            }
        }
    } else {
        // Portrait mode
        Column(
            modifier = Modifier
                .padding(
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding()
                )
                .fillMaxSize() // Fill screen size
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(dominantColor), Color.White)
                    )
                )
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.75f), // Original aspect ratio
                model = pokemonInfo.sprites.front_default,
                contentDescription = "Pokemon image",
                onError = { error -> onError(error.result.toString()) },
                onSuccess = { result -> onSuccess(result.result) }
            )

            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 16.dp),
                text = pokemonInfo.name,
                color = Color(dominantColor),
                fontSize = 36.sp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                pokemonInfo.types.forEach { type -> Chip(type) }
            }

            TabLayout(
                pokemonInfo = pokemonInfo,
                secondColor = secondColor
            )
        }
    }
}


@Composable
private fun TabLayout(
    modifier: Modifier = Modifier,
    pokemonInfo: Pokemon,
    secondColor: Int
) {
    val titles = listOf("About", "Stats")
    var selectedTabIndex by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TabRow(
                contentColor = Color.White,
                containerColor = Color.White,
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        height = 2.dp,
                        color = Color.Gray
                    )
                },
                selectedTabIndex = selectedTabIndex
            ) {
                titles.forEachIndexed { index, title ->
                    Tab(
                        text = {
                            Text(
                                text = title,
                                color = Color(secondColor)
                            )
                        },
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index }
                    )
                }
            }
        },
        content = { paddingValues ->
            when (selectedTabIndex) {
                0 -> AboutTab(paddingValues, pokemonInfo, secondColor)
                1 -> StatsTab(paddingValues, pokemonInfo, secondColor)
                else -> throw IllegalArgumentException("Unknown tab.")
            }
        }
    )
}



//@Composable
//private fun Content(
//    padding: PaddingValues,
//    pokemonInfo: Pokemon,
//    dominantColor: Int,
//    secondColor: Int,
//    onSuccess: (result: SuccessResult) -> Unit,
//    onError: (error: String) -> Unit,
//) {
//    // Determine the orientation
//    val configuration = LocalConfiguration.current
//    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
//
//    if (isLandscape) {
//        // Landscape mode
//        Row(
//            modifier = Modifier
//                .padding(
//                    top = padding.calculateTopPadding(),
//                    bottom = padding.calculateBottomPadding()
//                )
//                .fillMaxSize() // Ensure it takes full size available
//                .background(
//                    brush = Brush.verticalGradient(
//                        colors = listOf(Color(dominantColor), Color.White)
//                    )
//                )
//        ) {
//            Column(
//                modifier = Modifier
//                    .weight(1f) // Take up half of the screen
//                    .padding(16.dp)
//            ) {
//                AsyncImage(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .aspectRatio(1.5f), // Adjust aspect ratio as needed
//                    model = pokemonInfo.sprites.front_default,
//                    contentDescription = "Pokemon image",
//                    onError = { error -> onError(error.result.toString()) },
//                    onSuccess = { result -> onSuccess(result.result) }
//                )
//
//                Text(
//                    modifier = Modifier
//                        .align(Alignment.CenterHorizontally)
//                        .padding(top = 16.dp),
//                    text = pokemonInfo.name,
//                    color = Color(dominantColor),
//                    fontSize = 36.sp
//                )
//
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 16.dp),
//                    horizontalArrangement = Arrangement.Center
//                ) {
//                    pokemonInfo.types.forEach { type -> Chip(type) }
//                }
//            }
//
//            // Set Modifier.height to control TabLayout size
//            TabLayout(
//                modifier = Modifier
//                    .weight(1f) // Ensure it takes the remaining half of the screen
//                    .fillMaxHeight() // Fill the available height
//                    .background(Color(secondColor))
//                    .padding(16.dp), // Add padding for inner content
//                pokemonInfo = pokemonInfo,
//                secondColor = secondColor
//            )
//        }
//    } else {
//        // Portrait mode
//        Column(
//            modifier = Modifier
//                .padding(
//                    top = padding.calculateTopPadding(),
//                    bottom = padding.calculateBottomPadding()
//                )
//                .fillMaxSize() // Fill screen size
//                .background(
//                    brush = Brush.verticalGradient(
//                        colors = listOf(Color(dominantColor), Color.White)
//                    )
//                )
//        ) {
//            AsyncImage(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .aspectRatio(1.75f), // Original aspect ratio
//                model = pokemonInfo.sprites.front_default,
//                contentDescription = "Pokemon image",
//                onError = { error -> onError(error.result.toString()) },
//                onSuccess = { result -> onSuccess(result.result) }
//            )
//
//            Text(
//                modifier = Modifier
//                    .align(Alignment.CenterHorizontally)
//                    .padding(top = 16.dp),
//                text = pokemonInfo.name,
//                color = Color(dominantColor),
//                fontSize = 36.sp
//            )
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 16.dp),
//                horizontalArrangement = Arrangement.Center
//            ) {
//                pokemonInfo.types.forEach { type -> Chip(type) }
//            }
//
//            TabLayout(
//                pokemonInfo = pokemonInfo,
//                secondColor = secondColor
//            )
//        }
//    }
//}
//
