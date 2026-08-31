package com.dmariani.weathermvi.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dmariani.weathermvi.R
import com.dmariani.weathermvi.ui.theme.WeatherTheme

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SettingsContent(
        state = state,
        onNavigateBack = onNavigateBack,
        onIntent = viewModel::onIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    state: SettingsUiState,
    onNavigateBack: () -> Unit,
    onIntent: (SettingsIntent) -> Unit) {

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            TemperatureUnitToggle(
                isFahrenheit = state.isFahrenheit,
                onToggle = { onIntent(SettingsIntent.ToggleTemperatureUnit(it)) }
            )
        }
    }
}

@Composable
private fun TemperatureUnitToggle(
    isFahrenheit: Boolean,
    onToggle: (Boolean) -> Unit
) {
    SingleChoiceSegmentedButtonRow {
        // Celsius Button
        SegmentedButton(
            selected = !isFahrenheit,
            onClick = { onToggle(false) },
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
        ) {
            Text(stringResource(R.string.celsius))
        }

        // Fahrenheit Button
        SegmentedButton(
            selected = isFahrenheit,
            onClick = { onToggle(true) },
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
        ) {
            Text(stringResource(R.string.fahrenheit))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    WeatherTheme {
        SettingsContent(
            state = SettingsUiState(isFahrenheit = false),
            onNavigateBack = {},
            onIntent = {}
        )
    }
}