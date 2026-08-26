package com.dmariani.weathermvi.ui.main

import com.dmariani.weathermvi.domain.model.City
import com.dmariani.weathermvi.domain.model.Weather

/**
 * Single, unified state for the weather screen, following MVI's principle of one
 * source of truth per screen. [selectedCity] and [recentSearches] persist independently
 * of [content], since neither represents a mutually-exclusive UI condition.
 */
data class WeatherUiState(
    val selectedCity: City? = null,
    val recentSearches: List<String> = emptyList(),
    val contentState: WeatherContentState = WeatherContentState.Idle
)

/**
 * Sealed hierarchy representing the mutually-exclusive states of a weather fetch.
 * Unlike a flat data class with nullable fields, this makes impossible combinations
 * (e.g. loading and error simultaneously) unrepresentable at the type level.
 */
sealed interface WeatherContentState {
    data object Idle: WeatherContentState
    data object Loading: WeatherContentState
    data class Success(val weather: Weather) : WeatherContentState
    data class Error(val message: String) : WeatherContentState
}
