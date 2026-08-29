package com.dmariani.weathermvi.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dmariani.weathermvi.domain.model.Cities
import com.dmariani.weathermvi.domain.model.City
import com.dmariani.weathermvi.ui.theme.WeatherTheme

/**
 * Stateful entry point for the weather screen. Obtains [WeatherViewModel] via Hilt,
 * collects its [WeatherUiState] and one-time Snackbar events, and delegates all
 * rendering to [MainContent].
 */
@Composable
fun MainScreen(viewModel: WeatherViewModel = hiltViewModel()) {

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
        onIntent = viewModel::onIntent
    )
}

/**
 * Stateless rendering of the weather screen. Has no knowledge of the ViewModel or
 * Hilt — takes the current [state] and reports user actions via [onIntent], making
 * it directly previewable and testable with fake data.
 */
@Composable
fun MainContent(
    state: WeatherUiState,
    snackbarHostState: SnackbarHostState,
    onIntent: (WeatherIntent) -> Unit
) {

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
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
            state.recentSearches.forEach {
                // TODO("Load Recent Searches")
            }

            // Section: Content
            when (val content = state.contentState) {
                is WeatherContentState.Idle -> { /* do nothing */ }
                is WeatherContentState.Loading ->  { /* show loader */ }
                is WeatherContentState.Success ->  { /* show weather */ }
                is WeatherContentState.Error ->  { /* show error + retry */ }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityPicker(
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

@Preview(showBackground = false)
@Composable
fun MainContentIdlePreview() {
    WeatherTheme {
        MainContent(
            state = WeatherUiState(),
            snackbarHostState = SnackbarHostState(),
            onIntent = {}
        )
    }
}