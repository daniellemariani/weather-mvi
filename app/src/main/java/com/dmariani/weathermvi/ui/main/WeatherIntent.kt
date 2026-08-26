package com.dmariani.weathermvi.ui.main

import com.dmariani.weathermvi.domain.model.City

/**
 * User actions the View can dispatch to the ViewModel.
 * The View never calls ViewModel methods directly.
 * All interaction flows through [onIntent].
 */
sealed interface WeatherIntent {
    data class SelectCity(val city: City): WeatherIntent
    data object Retry : WeatherIntent
}