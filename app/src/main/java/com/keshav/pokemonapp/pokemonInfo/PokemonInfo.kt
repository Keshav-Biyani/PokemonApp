package com.keshav.pokemonapp.pokemonInfo

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.keshav.pokemonapp.MainActivity
import com.keshav.pokemonapp.R
import com.keshav.pokemonapp.Utils.getDominantColor
import com.keshav.pokemonapp.Utils.getSecondDominantColor
import com.keshav.pokemonapp.api.responses.Pokemon
import com.keshav.pokemonapp.api.responses.Type
import com.keshav.pokemonapp.pokemonInfo.tabs.about.AboutTab
import com.keshav.pokemonapp.pokemonInfo.tabs.stats.StatsTab

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

@Composable
private fun Content(
    padding: PaddingValues,
    pokemonInfo: Pokemon,
    dominantColor: Int,
    secondColor: Int,
    onSuccess: (result: SuccessResult) -> Unit,
    onError: (error: String) -> Unit,
) {
    Column {
        Column(
            modifier = Modifier
                .padding(
                    top = padding.calculateTopPadding(), bottom = padding.calculateBottomPadding()
                )
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(dominantColor), Color.White)
                    )
                )
        ) {
            AsyncImage(modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.75f),
                model = pokemonInfo.sprites.front_default,
                contentDescription = "Pokemon image",
                onError = { error -> onError(error.result.toString()) },
                onSuccess = { result -> onSuccess(result.result) })

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
            ) { pokemonInfo.types.forEach { type -> Chip(type) } }
        }
        TabLayout(pokemonInfo, secondColor)
    }
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
private fun TabLayout(pokemonInfo: Pokemon, secondColor: Int) {
    val titles = listOf("About", "Stats")
    var selectedTabIndex by remember { mutableIntStateOf(0) }

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

                else -> throw IllegalArgumentException("Tab desconocido.")
            }
        }
    )
}

enum class PokemonType(val type: String, val color: Long) {
    BUG("bug", 0xFFA8B820),
    DARK("dark", 0xFF705848),
    DRAGON("dragon", 0xFF7038F8),
    ELECTRIC("electric", 0xFFF8D030),
    FAIRY("fairy", 0xFFEE99AC),
    FIGHTING("fighting", 0xFFC03028),
    FIRE("fire", 0xFFF08030),
    FLYING("flying", 0xFFA890F0),
    GHOST("ghost", 0xFF705898),
    GRASS("grass", 0xFF78C850),
    GROUND("ground", 0xFFE0C068),
    ICE("ice", 0xFF98D8D8),
    NORMAL("normal", 0xFFA8A878),
    POISON("poison", 0xFFA040A0),
    PSYCHIC("psychic", 0xFFF85888),
    ROCK("rock", 0xFFB8A038),
    SHADOW("shadow", 0xFF3D3D3D),
    STEEL("steel", 0xFFB8B8D0),
    UNKNOWN("unknown", 0xFF68A090),
    WATER("water", 0xFF6890F0)
}

fun getPokemonType(type: String) =
    try {
        PokemonType.valueOf(type.uppercase())
    } catch (e: IllegalArgumentException) {
        PokemonType.UNKNOWN
    }
fun Pokemon.getFormattedId() = when {
    id < 10 -> "#00$id"
    id < 100 -> "#0$id"
    else -> "#$id"
}