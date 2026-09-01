package com.dmariani.weathermvi.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dmariani.weathermvi.R
import com.dmariani.weathermvi.domain.model.Cities
import com.dmariani.weathermvi.domain.model.City
import com.dmariani.weathermvi.domain.model.Weather
import com.dmariani.weathermvi.ui.theme.WeatherTheme
import com.dmariani.weathermvi.util.celsiusToFahrenheit

//region Composable Screen
/**
 * Stateful entry point for the weather screen. Obtains [WeatherViewModel] via Hilt,
 * collects its [WeatherUiState] and one-time Snackbar events, and delegates all
 * rendering to [MainContent].
 */
@Composable
fun MainScreen(
    onNavigateToSettings: () -> Unit,
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.snackbarEvent.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    MainContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onNavigateToSettings = onNavigateToSettings,
        onIntent = viewModel::onIntent
    )
}

/**
 * Stateless rendering of the weather screen. Has no knowledge of the ViewModel or
 * Hilt — takes the current [state] and reports user actions via [onIntent], making
 * it directly previewable and testable with fake data.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    state: WeatherUiState,
    snackbarHostState: SnackbarHostState,
    onNavigateToSettings: () -> Unit,
    onIntent: (WeatherIntent) -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(R.string.settings_title)
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(modifier = Modifier
            .padding(paddingValues)
            .padding(16.dp)) {
            // Section: City Picker
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(R.string.city))
                Spacer(modifier = Modifier.width(18.dp))
                CityPicker(
                    selectedCity = state.selectedCity,
                    cities = Cities.ALL,
                    onCitySelected = { city -> onIntent(WeatherIntent.SelectCity(city)) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Section: Recent Searches
            LazyColumn(
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(state.recentSearches, key = { it }) { cityName ->
                    Text(
                        text = cityName,
                        modifier = Modifier.clickable {
                            val city = Cities.ALL.find { it.name == cityName }
                            city?.let { onIntent(WeatherIntent.SelectCity(it)) }
                        }
                    )
                }
            }

            // Section: Content
            when (val content = state.contentState) {
                is WeatherContentState.Idle -> { /* do nothing */ }
                is WeatherContentState.Loading -> {
                    Loader(modifier = Modifier.weight(1f).fillMaxSize())
                }
                is WeatherContentState.Success -> {
                    WeatherView(
                        weather = content.weather,
                        isFahrenheit = state.isFahrenheit,
                        modifier = Modifier.weight(1f).fillMaxSize()
                    )
                }
                is WeatherContentState.Error -> {
                    ErrorView(
                        error = content.message,
                        onRetry = { onIntent(WeatherIntent.Retry)},
                        modifier = Modifier.weight(1f).fillMaxSize()
                    )
                }
            }
        }
    }
}
//endregion

//region Composable Components
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CityPicker(
    selectedCity: City?,
    cities: List<City>,
    onCitySelected: (City) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        // Label
        TextField(
            value = selectedCity?.name ?: cities.first().name,
            onValueChange = { },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
                .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable)
        )

        // Dropdown
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            cities.forEach { city ->
                DropdownMenuItem(
                    text = { Text(city.name) },
                    onClick = {
                        onCitySelected(city)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun Loader(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(strokeWidth = 4.dp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = stringResource(R.string.loading_message))
        }
    }
}

@Composable
private fun WeatherView(
    weather: Weather,
    isFahrenheit: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row (verticalAlignment = Alignment.CenterVertically) {
                Text(text = stringResource(R.string.city_weather, weather.city))
                Spacer(modifier = Modifier.width(18.dp))

                val displayTemperature = if (isFahrenheit) celsiusToFahrenheit(weather.temperature) else weather.temperature
                val unitSymbol = if (isFahrenheit) "F" else "C"

                Text(
                    text = stringResource(R.string.city_temperature, displayTemperature, unitSymbol),
                    fontSize = 32.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            val dayNightLabel = if (weather.isDay) stringResource(R.string.day) else stringResource(R.string.night)
            Text(text = stringResource(R.string.city_condition,  weather.condition, dayNightLabel))
        }
    }
}

@Composable
private fun ErrorView(error: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = error)
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onRetry) {
                Text(text = stringResource(R.string.retry))
            }
        }
    }
}
//endregion

//region Preview
@Preview(showBackground = true)
@Composable
fun MainScreenIdleStatePreview() {
    WeatherTheme {
        MainContent(
            state = WeatherUiState(),
            snackbarHostState = SnackbarHostState(),
            onNavigateToSettings = {},
            onIntent = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenLoadingStatePreview() {
    WeatherTheme {
        MainContent(
            state = WeatherUiState(contentState = WeatherContentState.Loading),
            snackbarHostState = SnackbarHostState(),
            onNavigateToSettings = {},
            onIntent = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenSuccessStatePreview() {
    WeatherTheme {
        val weather = Weather(
            city = "Los Angeles",
            temperature = 32.0,
            condition = "Clear",
            isDay = true
        )

        val recentSearches = listOf("Los Angeles", "Caracas", "New York", "Madrid", "Buenos Aires")

        val state = WeatherUiState(
            recentSearches = recentSearches,
            contentState = WeatherContentState.Success(weather)
        )

        MainContent(
            state = state,
            snackbarHostState = SnackbarHostState(),
            onNavigateToSettings = {},
            onIntent = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenErrorStatePreview() {
    WeatherTheme {
        MainContent(
            state = WeatherUiState(contentState = WeatherContentState.Error("Unable to load weather")),
            snackbarHostState = SnackbarHostState(),
            onNavigateToSettings = {},
            onIntent = {}
        )
    }
}
//endregion