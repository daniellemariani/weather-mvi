package com.dmariani.weathermvi.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmariani.weathermvi.domain.model.City
import com.dmariani.weathermvi.domain.repository.SettingsRepository
import com.dmariani.weathermvi.domain.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WeatherUiState())
    val state: StateFlow<WeatherUiState> = _state.asStateFlow()

    private val _snackbarEvent = Channel<String>()
    val snackbarEvent: Flow<String> = _snackbarEvent.receiveAsFlow()

    private var fetchJob: Job? = null

    init {
        viewModelScope.launch {
            weatherRepository.observeRecentSearches().collect { searches ->
                // create copy of WeatherUiState and update searches
                _state.update { it.copy(recentSearches = searches) }
            }
        }

        viewModelScope.launch {
            settingsRepository.isFahrenheit.collect { newValue ->
                // create copy of WeatherUiState and update isFahrenheit
                _state.update { it.copy(isFahrenheit = newValue) }
            }
        }
    }

    /**
     * Single entry point for all user actions on the weather screen. The View never calls
     * ViewModel methods directly, every interaction is expressed as a [WeatherIntent] and
     * routed here, keeping state changes centralized and traceable to a specific user action.
     */
    fun onIntent(intent: WeatherIntent) {
        when (intent) {
            is WeatherIntent.SelectCity -> handleSelectCity(intent.city)
            is WeatherIntent.Retry -> handleRetry()
        }
    }

    private fun handleSelectCity(city: City) {
        _state.update { it.copy(selectedCity = city) }
        fetchWeather(city)
    }

    private fun handleRetry() {
        val city = _state.value.selectedCity ?: return
        fetchWeather(city = city, forceRefresh = true)
    }

    private fun fetchWeather(city: City, forceRefresh: Boolean = false) {
        // cancel previous job if still running
        fetchJob?.cancel()

        // update loading state
        _state.update { it.copy(contentState = WeatherContentState.Loading) }

        fetchJob  = viewModelScope.launch {
            // fetch weather
            weatherRepository.getWeather(city = city, forceRefresh = forceRefresh)
                .onSuccess { weather ->
                    _state.update {
                        it.copy(contentState = WeatherContentState.Success(weather))
                    }
                }
                .onFailure {
                    val errorMessage = "Unable to load weather from ${city.name}"
                    _state.update { it.copy(contentState = WeatherContentState.Error(errorMessage)) }
                    _snackbarEvent.send(errorMessage)
                }
        }
    }
}