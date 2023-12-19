package com.sample.ebayapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sample.ebayapp.ui.theme.EbayAppTheme
import com.sample.ebayapp.ui.theme.earthquake_detail.EarthquakeDetailState
import com.sample.ebayapp.ui.theme.earthquake_detail.EarthquakeDetailViewModel
import com.sample.ebayapp.ui.theme.earthquake_list.EarthquakeListUiState
import com.sample.ebayapp.ui.theme.earthquake_list.EarthquakeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EbayAppTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "/earthquakeList") {
                    composable("/earthquakeList") {
                        // A surface container using the 'background' color from the theme
                        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                            EarthquakeList(onEqClick = {
                                navController.navigate("/earthquakeDetail/$it") {
                                    launchSingleTop = true
                                    popUpTo("/earthquakeList")
                                }
                            })
                        }
                    }
                    composable(
                        "/earthquakeDetail/{eqId}",
                        arguments = listOf(
                            navArgument("eqId") { type = NavType.StringType }
                        )
                    ) {
                        EarthquakeDetail()
                    }
                }

            }
        }
    }
}

@Composable
fun EarthquakeList(
    onEqClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EarthquakeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = modifier) {
        when (val state = uiState) {
            EarthquakeListUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

            is EarthquakeListUiState.Error -> {
                Text(text = state.message ?: "Something went wrong", modifier = Modifier.align(Alignment.Center))
            }

            is EarthquakeListUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.earthquakes) {
                        Column(modifier = Modifier.clickable { onEqClick(it.eqid) }) {
                            Text(text = it.eqid)
                            Text(text = it.magnitude.toString())
                            Divider(thickness = 1.dp, modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun EarthquakeDetail(
    modifier: Modifier = Modifier,
    viewModel: EarthquakeDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = modifier) {
        when (val state = uiState) {
            EarthquakeDetailState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            is EarthquakeDetailState.Success -> {
                val color by remember(this) {
                    derivedStateOf {
                        state.earthquake?.run {
                            when {
                                magnitude > 8.5 -> Color.Red
                                magnitude >= 8.0 -> Color.Yellow
                                else -> null
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(if (color != null) Modifier.background(color = color!!) else Modifier)
                ) {

                    state.earthquake?.run {
                        Text(text = src)
                        Text(text = magnitude.toString())
                    }
                }
            }
        }
    }
}